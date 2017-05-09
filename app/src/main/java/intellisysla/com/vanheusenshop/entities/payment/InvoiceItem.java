package intellisysla.com.vanheusenshop.entities.payment;

import java.io.Serializable;

/**
 * Created by alienware on 4/5/2017.
 */

public class InvoiceItem implements Serializable {

    public int docEntry;
    public String documentNumber;
    public double totalAmount;
    public double payedAmount;

    public InvoiceItem(){ }

    public InvoiceItem(int docEntry, String documentNumber, double totalAmount, double payedAmount) {
        this.docEntry = docEntry;
        this.documentNumber = documentNumber;
        this.totalAmount = totalAmount;
        this.payedAmount = payedAmount;
    }

    public int getDocEntry() {
        return docEntry;
    }

    public void setDocEntry(int docEntry) {
        this.docEntry = docEntry;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getPayedAmount() {
        return payedAmount;
    }

    public void setPayedAmount(double payedAmount) {
        this.payedAmount = payedAmount;
    }
}
