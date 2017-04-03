package intellisysla.com.vanheusenshop.entities.payment;

import java.util.Date;

import intellisysla.com.vanheusenshop.entities.Bank;

/**
 * Created by turupawn on 3/28/17.
 */
public class CheckPayment {
    private String checkNumber;

    private Bank bank;

    private double amount;

    private Date date;

    public CheckPayment() {}

    public CheckPayment(String checkNumber, Bank bank, double amount)
    {
        this.checkNumber = checkNumber;
        this.bank = bank;
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCheckNumber()
    {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber)
    {
        this.checkNumber = checkNumber;
    }

    public Bank getBank()
    {
        return bank;
    }

    public void setBank(Bank bank)
    {
        this.bank = bank;
    }

    public double getAmount()
    {
        return amount;
    }

    public void setAmount(double amount)
    {
        this.amount = amount;
    }
}
