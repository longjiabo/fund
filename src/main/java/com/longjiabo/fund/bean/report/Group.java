package com.longjiabo.fund.bean.report;

import com.longjiabo.fund.model.fund.FundGroup;
import com.longjiabo.fund.model.fund.Transaction;

import java.util.List;

public class Group {
    private FundGroup fundGroup;
    private List<TransactionSummary> transactionSummaries;

    public List<TransactionSummary> getTransactionSummaries() {
        return transactionSummaries;
    }

    public void setTransactionSummaries(List<TransactionSummary> transactionSummaries) {
        this.transactionSummaries = transactionSummaries;
    }

    public FundGroup getFundGroup() {
        return fundGroup;
    }

    public void setFundGroup(FundGroup fundGroup) {
        this.fundGroup = fundGroup;
    }
}
