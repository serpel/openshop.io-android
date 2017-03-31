package intellisysla.com.vanheusenshop.interfaces;

import android.view.View;

import intellisysla.com.vanheusenshop.entities.order.Order;
import intellisysla.com.vanheusenshop.entities.payment.Payment;

public interface PaymentsRecyclerInterface {

    void onPaymentSelected(View v, Payment payment);

}
