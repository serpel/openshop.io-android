package bf.io.openshop.entities.client;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alienware on 2/1/2017.
 */

public class Invoice {
    @SerializedName("invoice_code")
    private String invoiceCode;
    @SerializedName("created_date")
    private String createdDate;
    @SerializedName("due_date")
    private String dueDate;
    @SerializedName("total_amount")
    private Double totalAmount;
    @SerializedName("payed_amount")
    private Double payedAmount;

    public Invoice(){}

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
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
