package com.longjiabo.fund.service;

import com.longjiabo.fund.Constant;
import com.longjiabo.fund.model.fund.History;
import com.longjiabo.fund.model.fund.Target;
import com.longjiabo.fund.model.fund.Transaction;
import com.longjiabo.fund.repository.HistoryRepository;
import com.longjiabo.fund.repository.TargetRepository;
import com.longjiabo.fund.repository.TransactionRepository;
import com.longjiabo.fund.util.BaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DailyPriceService {
    private static final Logger log = LoggerFactory.getLogger(DailyPriceService.class);

    @Autowired
    TargetRepository targetRepository;

    @Autowired
    HistoryRepository historyRepository;

    @Autowired
    TransactionRepository transactionRepository;

    private ApplicationContext context;

    public void dailyPrice() {
        Iterable<Target> targets = targetRepository.findAll();
        //Date date = new Date();
        for (Target target : targets) {
//            if (scannerFinished(date, target))
//                continue;
            scannerFund(target, null);
        }
    }

    private boolean scannerFinished(Date start, Target target) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        //cal.add(Calendar.HOUR_OF_DAY, -16);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date begin = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date end = cal.getTime();
        List<History> result = historyRepository.findBetweenPriceDateAndCodeOrderbyPriceDateAsc(begin, end, target.getCode());
        return !result.isEmpty();
    }


    @Autowired
    public void context(ApplicationContext context) {
        this.context = context;
    }

    private Scanner getScanner(Target target) {
        try {
            Class<?> c = Class.forName("com.longjiabo.fund.service.V" + target.getType() + "Scanner");
            Scanner sc = (Scanner) this.context.getBean(c);
            return sc;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    public boolean scannerFund(Target target, Integer count) {
        Scanner sc = getScanner(target);
        if (sc == null)
            return false;
        List<History> historys = sc.scanner(target, count);
        if (historys.isEmpty())
            return false;
        filterHistorys(historys);
        if (!historys.isEmpty()) {
            log.info(V1Scanner.body);
            for (History history : historys) {
                log.info("insert " + history.getPriceDate());
            }
        }
        historys.forEach(history -> historyRepository.save(history));
        updateTransactions(historys);
        return true;
    }

    private void filterHistorys(List<History> historys) {
        if (historys.isEmpty())
            return;
        List<History> list = historyRepository.findAllByCode(historys.get(0).getCode());
        historys.removeIf(history -> hasScanned(history, list));
    }

    private boolean hasScanned(History history, List<History> list) {
        if (history.getPriceDate() == null)
            return true;
        for (History h : list) {
            if (BaseUtils.oneDay(history.getPriceDate(), h.getPriceDate()))
                return true;
        }
        return false;
    }

    private void updateTransactions(List<History> historys) {
        for (History history : historys) {
            updateTransaction(history);
        }
    }

    @Transactional
    private void updateTransaction(History history) {
        if (history.getPriceDate() == null)
            return;
        history.setCreatedOn(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(history.getPriceDate());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        List<Transaction> list = transactionRepository.findNotCacul(history.getCode(), cal.getTime(), Constant.TYPE_BUY, Constant.TYPE_SELL);
        if (list.isEmpty())
            return;
        for (Transaction transaction : list) {
            if (transaction.getType() == Constant.TYPE_BUY) {
                transaction.setPrice(history.getPrice1());
                transaction.setVolume(transaction.getAmount() / transaction.getPrice());
            }
            if (transaction.getType() == Constant.TYPE_SELL) {
                transaction.setPrice(history.getPrice1());
                transaction.setAmount(transaction.getVolume() * transaction.getPrice());
            }
            transactionRepository.save(transaction);
        }
    }
}
