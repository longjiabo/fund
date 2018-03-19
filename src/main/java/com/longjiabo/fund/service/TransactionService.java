package com.longjiabo.fund.service;

import com.longjiabo.fund.Constant;
import com.longjiabo.fund.model.fund.History;
import com.longjiabo.fund.model.fund.Target;
import com.longjiabo.fund.model.fund.Transaction;
import com.longjiabo.fund.repository.HistoryRepository;
import com.longjiabo.fund.repository.TargetRepository;
import com.longjiabo.fund.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    HistoryRepository historyRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    TargetRepository targetRepository;

    public void newOperation(Transaction transaction) {
        String code = transaction.getCode();
        Target target = targetRepository.findByCode(code);
        if (target.getType().equals(Constant.Target_CASH)) {
            switch (transaction.getType()) {
                case Constant.TYPE_BUY:
                    transaction.setPrice(1.0);
                    transaction.setVolume(transaction.getAmount());
                    break;
                case Constant.TYPE_SELL:
                    transaction.setPrice(1.0);
                    transaction.setVolume(-transaction.getVolume());
                    transaction.setAmount(transaction.getVolume());
                    break;
                case Constant.TYPE_CASH:
                    transaction.setPrice(1.0);
                    transaction.setVolume(transaction.getAmount());
                    break;
                case Constant.TYPE_CASHAGAIN:
                    transaction.setPrice(1.0);
                    transaction.setAmount(transaction.getVolume());
                    break;
                default:
                    break;
            }
        } else {
            switch (transaction.getType()) {
                case Constant.TYPE_BUY:
                    buyService(transaction);
                    break;
                case Constant.TYPE_SELL:
                    sellService(transaction);
                    break;
                case Constant.TYPE_CASH:
                    cashService(transaction);
                    break;
                case Constant.TYPE_CASHAGAIN:
                    cashAgainService(transaction);
                    break;
                default:
                    break;
            }
        }
        transactionRepository.save(transaction);
    }

    private void cashAgainService(Transaction transaction) {
        transaction.setPrice((double) 0);
        transaction.setAmount((double) 0);
    }

    private void cashService(Transaction transaction) {
        transaction.setPrice((double) 0);
        transaction.setVolume((double) 0);
        transaction.setAmount(transaction.getAmount() * -1);
    }

    private void sellService(Transaction transaction) {
        Date date = transaction.getTxnDate();
        if (date == null)
            return;
        History history = getHistory(date, transaction.getCode());
        if (history == null) {
            transaction.setPrice(null);
            transaction.setVolume(transaction.getVolume() * -1);
            transaction.setAmount(null);
        } else {
            transaction.setPrice(history.getPrice1());
            transaction.setVolume(transaction.getVolume() * -1);
            transaction
                    .setAmount(transaction.getVolume() * history.getPrice1());
        }
    }

    private void buyService(Transaction transaction) {
        Date date = transaction.getTxnDate();
        if (date == null)
            return;
        History history = getHistory(date, transaction.getCode());
        if (history == null) {
            transaction.setPrice(null);
            transaction.setVolume(null);
        } else {
            transaction.setPrice(history.getPrice1());
            transaction
                    .setVolume(transaction.getAmount() / history.getPrice1());
        }
    }

    private History getHistory(Date date, String code) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        List<History> list = historyRepository.findBetweenPriceDateAndCodeOrderbyPriceDateAsc(date, cal.getTime(), code);
        if (list.isEmpty()) return null;
        return list.get(0);
    }
}
