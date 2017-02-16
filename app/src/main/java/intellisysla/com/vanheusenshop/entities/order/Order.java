package intellisysla.com.vanheusenshop.entities.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import intellisysla.com.vanheusenshop.entities.cart.CartProductItem;

public class Order {

    private long id;

    @SerializedName("remote_id")
    private String remoteId;

    @SerializedName("date_created")
    private String dateCreated;
    private String status;
    private int total;

    @SerializedName("total_formatted")
    private String totalFormatted;

    @SerializedName("items")
    private List<CartProductItem> products;
    private String email;
    private String phone;
    private String comment;

    @SerializedName("sales_person_code")
    private int salesPersonCode;
    private int series;

    @SerializedName("card_code")
    private String cardCode;

    public Order() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public int getSalesPersonCode() {
        return salesPersonCode;
    }

    public void setSalesPersonCode(int salesPersonCode) {
        this.salesPersonCode = salesPersonCode;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTotalFormatted() {
        return totalFormatted;
    }

    public void setTotalFormatted(String totalFormatted) {
        this.totalFormatted = totalFormatted;
    }

    public List<CartProductItem> getProducts() {
        return products;
    }

    public void setProducts(List<CartProductItem> products) {
        this.products = products;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (id != order.id) return false;
        if (total != order.total) return false;
        if (remoteId != null ? !remoteId.equals(order.remoteId) : order.remoteId != null) return false;
        if (dateCreated != null ? !dateCreated.equals(order.dateCreated) : order.dateCreated != null) return false;
        if (status != null ? !status.equals(order.status) : order.status != null) return false;
        if (totalFormatted != null ? !totalFormatted.equals(order.totalFormatted) : order.totalFormatted != null) return false;
        if (products != null ? !products.equals(order.products) : order.products != null) return false;
        if (email != null ? !email.equals(order.email) : order.email != null) return false;
        if (phone != null ? !phone.equals(order.phone) : order.phone != null) return false;
        return !(comment != null ? !comment.equals(order.comment) : order.comment != null);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (remoteId != null ? remoteId.hashCode() : 0);
        result = 31 * result + (dateCreated != null ? dateCreated.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + total;
        result = 31 * result + (totalFormatted != null ? totalFormatted.hashCode() : 0);
        result = 31 * result + (products != null ? products.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", remoteId='" + remoteId + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", status='" + status + '\'' +
                ", total=" + total +
                ", totalFormatted='" + totalFormatted + '\'' +
                ", products=" + products +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", note='" + comment + '\'' +
                '}';
    }
}
