package com.rednovo.ace.net.parser;

/**
 * Created by Dk on 16/3/19.
 */
public class IncomeBalanceResult extends BaseResult {
    private String balance;
    private String rate;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
