package intellisysla.com.vanheusenshop.interfaces;


import intellisysla.com.vanheusenshop.entities.delivery.Payment;

public interface PaymentDialogInterface {
    void onPaymentSelected(Payment payment);
}
