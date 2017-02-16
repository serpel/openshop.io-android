package intellisysla.com.vanheusenshop.entities.client;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by alienware on 2/1/2017.
 */

public class Client {
    private String name;
    @SerializedName("card_code")
    private String cardCode;
    private String phone;
    private String email;
    @SerializedName("documents")
    private List<Document> invoiceList;

    public Client(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Document> getInvoiceList() {
        return invoiceList;
    }

    public void setInvoiceList(List<Document> invoiceList) {
        this.invoiceList = invoiceList;
    }
}
