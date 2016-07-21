package com.rednovo.ace.data.events;

/**
 *
 */
public class SendGiftResponse extends BaseEvent{
    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    String balance;

}
