package intellisysla.com.vanheusenshop.entities;

/**
 * Created by alienware on 3/29/2017.
 */

public class Bank {
    private int id;
    private String name;
    private String GeneralAccount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGeneralAccount() {
        return GeneralAccount;
    }

    public void setGeneralAccount(String generalAccount) {
        GeneralAccount = generalAccount;
    }
}
