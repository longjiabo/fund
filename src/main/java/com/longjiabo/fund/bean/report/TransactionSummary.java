package com.longjiabo.fund.bean.report;

import com.longjiabo.fund.model.fund.Transaction;

import java.util.List;

public class TransactionSummary {
    private List<Transaction> transactions;
    private List<Double> data;
    private boolean noData;
    private String code;
    private String name;

    public List<Double> getData() {
        return data;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }

    public boolean isNoData() {
        return noData;
    }

    public void setNoData(boolean noData) {
        this.noData = noData;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
