package intellisysla.com.vanheusenshop.ux.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.entities.client.Client;
import intellisysla.com.vanheusenshop.entities.payment.Cash;
import intellisysla.com.vanheusenshop.entities.payment.CheckPayment;
import intellisysla.com.vanheusenshop.entities.payment.Payment;
import intellisysla.com.vanheusenshop.entities.payment.Transfer;
import intellisysla.com.vanheusenshop.interfaces.PaymentsRecyclerInterface;
import timber.log.Timber;

/**
 * Adapter handling list of orders from history.
 */
public class PaymentsHistoryRecyclerAdapter extends RecyclerView.Adapter<PaymentsHistoryRecyclerAdapter.ViewHolder> {

    private final PaymentsRecyclerInterface paymentsRecyclerInterface;
    private LayoutInflater layoutInflater;
    private List<Payment> payments = new ArrayList<>();

    /**
     * Creates an adapter that handles a list of payments from history
     *
     * @param paymentsRecyclerInterface listener indicating events that occurred.
     */
    public PaymentsHistoryRecyclerAdapter(PaymentsRecyclerInterface paymentsRecyclerInterface) {
        this.paymentsRecyclerInterface = paymentsRecyclerInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.list_item_payments_history, parent, false);
        return new ViewHolder(view, paymentsRecyclerInterface);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Payment payment = getPaymentItem(position);
        holder.bindContent(payment);

        holder.paymentIdTv.setText(String.valueOf(payment.getId()));
        holder.statusTv.setText(String.valueOf(payment.getStatus()));
        holder.docEntryTv.setText(String.valueOf(payment.getClient().getName()));
        holder.totalPayedTv.setText(String.valueOf(payment.getTotalPaid()));
    }

    private Payment getPaymentItem(int position) {
        return payments.get(position);
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    public void addPayments(List<Payment> paymentList) {
        if (paymentList != null && !paymentList.isEmpty()) {
            payments.addAll(paymentList);
            notifyDataSetChanged();
        } else {
            Timber.e("Adding empty payments list.");
        }
    }

    /**
     * Clear all data.
     */
    public void clear() {
        payments.clear();
    }

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView paymentIdTv;
        private TextView statusTv;
        private TextView docEntryTv;
        private TextView totalPayedTv;

        private Payment payment;

        public ViewHolder(View itemView, final PaymentsRecyclerInterface paymentsRecyclerInterface) {
            super(itemView);
            paymentIdTv = (TextView) itemView.findViewById(R.id.payment_history_id);
            statusTv = (TextView) itemView.findViewById(R.id.payment_history_status);
            docEntryTv = (TextView) itemView.findViewById(R.id.payment_history_client);
            totalPayedTv = (TextView) itemView.findViewById(R.id.payment_history_total_payed);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //paymentsRecyclerInterface.onPaymentSelected(v, payment);
                }
            });
        }

        public void bindContent(Payment payment) {
            this.payment = payment;
        }
    }
}
