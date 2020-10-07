package com.dao;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

//@Component
public class BankTransactionItemAnalyticsProcessor implements ItemProcessor<BankTransaction , BankTransaction> {
    private double totalDebit ;
    private  double totalCredit ;

    public double getTotalDebit() {
        return totalDebit;
    }

    public double getTotalCredit() {
        return totalCredit;
    }

    @Override
    public BankTransaction process(BankTransaction bankTransaction) throws Exception {
      if (bankTransaction.getTransactionType().equals("D")) totalDebit +=bankTransaction.getAmount() ;
      else if (bankTransaction.getTransactionType().equals("C")) totalCredit +=bankTransaction.getAmount() ;

      return bankTransaction;
    }
}
