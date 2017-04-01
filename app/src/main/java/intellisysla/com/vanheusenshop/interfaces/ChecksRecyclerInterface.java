package intellisysla.com.vanheusenshop.interfaces;

import android.view.View;

import intellisysla.com.vanheusenshop.entities.payment.CheckPayment;

/**
 * Created by alienware on 3/31/2017.
 */

public interface ChecksRecyclerInterface {
    void onCheckSelected(View view, CheckPayment checkPayment);
}
