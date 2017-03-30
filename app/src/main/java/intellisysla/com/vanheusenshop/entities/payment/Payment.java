package intellisysla.com.vanheusenshop.entities.payment;

import java.util.ArrayList;
import java.util.Date;

import intellisysla.com.vanheusenshop.entities.client.Client;

/**
 * Created by alienware on 3/29/2017.
 */

public class Payment {
    private int id;
    private String vendor;
    private String docEntry;
    private Double totalPaid;
    private String lastError;
    private Date date;
    private Client client;
    private Cash cash;
    private Transfer transfer;
    private ArrayList<CheckPayment> checks;

    public Payment() {}

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
