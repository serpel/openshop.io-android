package intellisysla.com.vanheusenshop.interfaces;

import android.view.View;

import intellisysla.com.vanheusenshop.entities.order.Order;

public interface OrdersRecyclerInterface {

    void onOrderSelected(View v, Order order);

}
