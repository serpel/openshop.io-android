package intellisysla.com.vanheusenshop.entities.payment;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alienware on 3/29/2017.
 */

public class Cash {
    private double amount;
    @SerializedName("general_account")
    private String generalAccount;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getGeneralAccount() {
        return generalAccount;
    }

    public void setGeneralAccount(String generalAccount) {
        this.generalAccount = generalAccount;
    }
}
