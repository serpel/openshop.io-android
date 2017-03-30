package intellisysla.com.vanheusenshop.entities.payment;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

import intellisysla.com.vanheusenshop.entities.client.Client;

/**
 * Created by alienware on 3/29/2017.
 */

public class Payment {
    @SerializedName("id")
    private int id;
    private String vendor;
    @SerializedName("doc_entry")
    private String docEntry;
    @SerializedName("total")
    private Double totalPaid;

    private String lastError;

    private Date date;

    @SerializedName("client")
    private Client client;
    @SerializedName("cash")
    private Cash cash;
    @SerializedName("transfer")
    private Transfer transfer;
    @SerializedName("checks")
    private ArrayList<CheckPayment> checks;

    public Payment() {}

    public Payment(int id, String vendor, String docEntry, Double totalPaid, String lastError, Date date, Client client, Cash cash, Transfer transfer, ArrayList<CheckPayment> checks)
    {
        this.id = id;
        this.vendor = vendor;
        this.docEntry = docEntry;
        this.totalPaid = totalPaid;
        this.lastError = lastError;
        this.date = date;
        this.client = client;
        this.cash = cash;
        this.transfer = transfer;
        this.checks = checks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getDocEntry() {
        return docEntry;
    }

    public void setDocEntry(String docEntry) {
        this.docEntry = docEntry;
    }

    public Double getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(Double totalPaid) {
        this.totalPaid = totalPaid;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Cash getCash() {
        return cash;
    }

    public void setCash(Cash cash) {
        this.cash = cash;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public void setTransfer(Transfer transfer) {
        this.transfer = transfer;
    }

    public ArrayList<CheckPayment> getChecks() {
        return checks;
    }

    public void setChecks(ArrayList<CheckPayment> checks) {
        this.checks = checks;
    }
}
