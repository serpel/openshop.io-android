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
    private String address;
    private String RTN;
    @SerializedName("documents")
    private List<Document> invoiceList;

    public Client(){}

    public String getAddress() {
        if(address!=null)
            return address;
        return "";
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRTN() {
        if(RTN!=null)
            return RTN;
        return "";
    }

    public void setRTN(String RTN) {
        this.RTN = RTN;
    }

    public String getName() {
        if(name!=null)
            return name;
        return "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardCode() {
        if(cardCode!=null)
            return cardCode;
        return "";
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getPhone() {
        if(phone!=null)
            return phone;
        return "";
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        if(email!=null)
            return email;
        return "";
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
