package intellisysla.com.vanheusenshop.entities.payment;

/**
 * Created by turupawn on 3/28/17.
 */
public class CheckPayment {
    private String checkNumber;

    private String bank;

    private double amount;

    public CheckPayment() {}

    public CheckPayment(String checkNumber, String bank, double amount)
    {
        this.checkNumber = checkNumber;
        this.bank = bank;
        this.amount = amount;
    }

    public String getCheckNumber()
    {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber)
    {
        this.checkNumber = checkNumber;
    }

    public String getBank()
    {
        return bank;
    }

    public void setBank(String bank)
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
