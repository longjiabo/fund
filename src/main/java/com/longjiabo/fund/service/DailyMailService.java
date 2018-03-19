package com.longjiabo.fund.service;

import com.longjiabo.fund.Constant;
import com.longjiabo.fund.bean.report.Group;
import com.longjiabo.fund.bean.report.TransactionSummary;
import com.longjiabo.fund.model.fund.FundGroup;
import com.longjiabo.fund.model.fund.History;
import com.longjiabo.fund.model.fund.Transaction;
import com.longjiabo.fund.repository.FundGroupRepository;
import com.longjiabo.fund.repository.HistoryRepository;
import com.longjiabo.fund.repository.TargetRepository;
import com.longjiabo.fund.repository.TransactionRepository;
import com.longjiabo.fund.util.BaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DailyMailService {

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    FundGroupRepository fundGroupRepository;
    @Autowired
    HistoryRepository historyRepository;
    @Autowired
    TargetRepository targetRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Group> getCashGroups(Date day) {
        Iterable<FundGroup> fundGroups = fundGroupRepository.findAllByTargetTypeWhichInTransaction(Constant.Target_CASH);
        List<Group> groups = new ArrayList<>();
        fundGroups.forEach(fundGroup -> {
            Group group = new Group();
            group.setFundGroup(fundGroup);
            groups.add(group);
            addCashTransactionSummaries(group, day);
        });
        return groups;
    }

    private void addCashTransactionSummaries(Group group, Date day) {
        List<History> histories = getHistories(day);
        List<Transaction> transactions = transactionRepository.findAllByGroupIdAndType(group.getFundGroup().getId(), Constant.Target_CASH);
        List<TransactionSummary> list = new ArrayList<>();
        group.setTransactionSummaries(list);
        transactions.stream().collect(Collectors.groupingBy(Transaction::getCode)).forEach((code, transactions1) -> {
            TransactionSummary transactionSummary = new TransactionSummary();
            list.add(transactionSummary);
            transactionSummary.setCode(code);
            transactionSummary.setName(targetRepository.findByCode(code).getName());
            transactionSummary.setTransactions(transactions1);
            Optional<History> todayHistory = histories.stream().filter(history -> history.getCode().equals(code)).findFirst();
            if (!todayHistory.isPresent()) {
                transactionSummary.setNoData(true);
                return;
            }
            transactionSummary.setData(caculCashData(transactions1, todayHistory.get()));
        });
        list.sort((transactionSummary, transactionSummary2) -> {
            if (transactionSummary.isNoData()) return 1;
            if (transactionSummary2.isNoData()) return -1;
            return transactionSummary2.getData().get(3) > transactionSummary.getData().get(3) ? 1 : -1;
        });
    }

    private List<Double> caculCashData(List<Transaction> transactions, History todayHistory) {
        List<Double> data = Arrays.asList(0.0, 0.0, 0.0, 0.0);
        Double totalAmount = transactions.stream().mapToDouble(Transaction::getAmount).sum();
        Date now = todayHistory.getPriceDate();
        String sql = "select sum(price1) as sum,count(1) as days from history where code=? and price_date>?";
        for (Transaction transaction : transactions) {
            Map<String, Object> map = jdbcTemplate.queryForMap(sql, transaction.getCode(), transaction.getTxnDate());
            Double data4 = (Double) map.get("sum");
            //long days = (Long) map.get("days");
            if (data4 == null) data4 = 0.0;
            data4 = data4 / 10000 * transaction.getAmount();
            //Double data3 = data4 / transaction.getAmount() * 100;
            Double data2 = todayHistory.getPrice1() / 10000 * transaction.getAmount();
            //Double data1 = data2 / transaction.getAmount() * 100;
            //data.set(0, data1 + data.get(0));
            data.set(1, data2 + data.get(1));
            //data.set(2, data3 + data.get(2));
            data.set(3, data4 + data.get(3));
            //单独计算每一个transaction的年华收益
            Date start = transaction.getTxnDate();
            long days = (now.getTime() - start.getTime()) / Constant.oneDay;
            if (days == 0) {
                transaction.setRate(0.0);
            } else {
                transaction.setRate(data4 / transaction.getVolume() / days * 365 * 100);
            }
        }
        data.set(0, data.get(1) / totalAmount * 100);
        data.set(2, data.get(3) / totalAmount * 100);
        return BaseUtils.formatDouble(data, null);
    }


    public List<Group> getGroups(Date day) {
        List<History> histories = getHistories(day);
        Iterable<FundGroup> fundGroups = fundGroupRepository.findAllByTargetTypeWhichInTransaction(Constant.Target_Other);
        List<Group> groups = new ArrayList<>();
        fundGroups.forEach(fundGroup -> {
            Group group = new Group();
            group.setFundGroup(fundGroup);
            groups.add(group);
            addTransactionSummaries(group, histories);
        });
        return groups;
    }

    private List<History> getHistories(Date day) {
        Calendar date = Calendar.getInstance();
        date.setTime(day);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        Date start = date.getTime();
        date.add(Calendar.DAY_OF_MONTH, 1);
        Date end = date.getTime();
        return historyRepository.findBetweenPriceDate(start,
                end);
    }

    private void addTransactionSummaries(Group group, List<History> historys) {
        List<Transaction> transactions = transactionRepository.findAllByGroupIdAndType(group.getFundGroup().getId(), Constant.Target_Other);
        List<TransactionSummary> list = new ArrayList<>();
        group.setTransactionSummaries(list);
        transactions.stream().collect(Collectors.groupingBy(Transaction::getCode)).forEach((code, transactions1) -> {
            TransactionSummary transactionSummary = new TransactionSummary();
            transactionSummary.setName(targetRepository.findByCode(code).getName());
            transactionSummary.setTransactions(transactions1);
            list.add(transactionSummary);
            Optional<History> todayHistory = historys.stream().filter(history -> history.getCode().equals(code)).findFirst();
            Optional<History> yesterdayHistory = historyRepository.findYesterdayHistoryByCode(code);

            if (!todayHistory.isPresent() || !yesterdayHistory.isPresent()) {
                transactionSummary.setNoData(true);
                return;
            }
            transactionSummary.setData(caculData(transactions1, todayHistory.get(), yesterdayHistory.get()));
        });
        list.sort((transactionSummary, transactionSummary2) -> {
            if (transactionSummary.isNoData()) return 1;
            if (transactionSummary2.isNoData()) return -1;
            return transactionSummary2.getData().get(3) > transactionSummary.getData().get(3) ? 1 : -1;
        });
    }

    private List<Double> caculData(List<Transaction> transactions, History today, History yesterday) {
        transactions = transactions.stream().filter(transaction -> transaction.getVolume() != null && transaction.getAmount() != null).collect(Collectors.toList());
        List<Double> data = new ArrayList<>(4);
        Double totalVolume = transactions.stream().mapToDouble(Transaction::getVolume).sum();
        Double totalAmount = transactions.stream().mapToDouble(Transaction::getAmount).sum();
        Double data2 = (today.getPrice1() - yesterday.getPrice1()) * totalVolume;
        Double data1 = data2 / totalAmount * 100;
        caculTxnRate(transactions, today);
        Double data4 = transactions.stream().mapToDouble(transaction -> transaction.getVolume() * today.getPrice1() - transaction.getAmount()).sum();
        Double data3 = data4 / totalAmount * 100;
        data.addAll(Arrays.asList(data1, data2, data3, data4));
        return BaseUtils.formatDouble(data, null);
    }

    private void caculTxnRate(List<Transaction> transactions, History today) {
        Date now = today.getPriceDate();
        for (Transaction transaction : transactions) {
            if (transaction.getType() != Constant.TYPE_BUY) continue;
            if (transaction.getAmount() == null || transaction.getAmount() == 0.0) {
                transaction.setRate(0.0);
            } else {
                long days = (now.getTime() - transaction.getTxnDate().getTime()) / Constant.oneDay;
                if (days == 0) {
                    transaction.setRate(0.0);
                } else {
                    Optional<History> history = historyRepository.findByPriceDateAndCode(transaction.getTxnDate(), transaction.getCode());
                    history.ifPresent(history1 -> transaction.setRate(transaction.getVolume() * (today.getPrice2() - history1.getPrice2()) / transaction.getAmount() / days * 365 * 100));
                }
            }
        }
    }
}
