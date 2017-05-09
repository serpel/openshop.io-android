package intellisysla.com.vanheusenshop.interfaces;

import android.view.View;

import intellisysla.com.vanheusenshop.entities.client.ClientTransactions;
import intellisysla.com.vanheusenshop.entities.payment.Payment;

public interface ClientTransactionsRecyclerInterface {

    void onClientTransactionsSelected(View v, ClientTransactions transaction);

}
