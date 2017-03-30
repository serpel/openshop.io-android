package intellisysla.com.vanheusenshop.entities.payment;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import intellisysla.com.vanheusenshop.entities.Bank;

/**
 * Created by alienware on 3/29/2017.
 */

public class Transfer {
    private int id;

    private String number;
    private double amount;
    @SerializedName("due_date")
    private Date dueDate;
    private Bank bank;

    public Transfer(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }
}
