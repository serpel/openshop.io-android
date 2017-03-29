package intellisysla.com.vanheusenshop.entities;

import java.io.Serializable;

/**
 * Created by alienware on 3/29/2017.
 */

public class Bank implements Serializable {
    private int id;
    private String name;
    private String GeneralAccount;

    public Bank(int id, String name, String GeneralAccount)
    {
        this.id = id;
        this.name = name;
        this.GeneralAccount = GeneralAccount;
    }

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
