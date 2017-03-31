package intellisysla.com.vanheusenshop.entities.client;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by alienware on 2/1/2017.
 */

public class Document implements Serializable {

    @SerializedName("document_code")
    private String documentCode;
    @SerializedName("created_date")
    private String createdDate;
    @SerializedName("due_date")
    private String dueDate;
    @SerializedName("total_amount")
    private Double totalAmount;
    @SerializedName("payed_amount")
    private Double payedAmount;
    @SerializedName("balance_due")
    private Double balanceDue;
    @SerializedName("overdue_days")
    private int overdueDays;

    public Document(){}

    public Double getBalanceDue() {
        return balanceDue;
    }

    public void setBalanceDue(Double balanceDue) {
        this.balanceDue = balanceDue;
    }

    public int getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(int overdueDays) {
        this.overdueDays = overdueDays;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public void setDocumentCode(String documentCode) {
        this.documentCode = documentCode;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Double getPayedAmount() {
        return payedAmount;
    }

    public void setPayedAmount(Double payedAmount) {
        this.payedAmount = payedAmount;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
