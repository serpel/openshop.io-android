package intellisysla.com.vanheusenshop.ux.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.entities.client.ClientTransactions;
import intellisysla.com.vanheusenshop.entities.payment.Payment;
import intellisysla.com.vanheusenshop.interfaces.ClientTransactionsRecyclerInterface;
import intellisysla.com.vanheusenshop.interfaces.PaymentsRecyclerInterface;
import timber.log.Timber;

/**
 * Adapter handling list of orders from history.
 */
public class ClientTransactionsRecyclerAdapter extends RecyclerView.Adapter<ClientTransactionsRecyclerAdapter.ViewHolder> {

    private final ClientTransactionsRecyclerInterface transactionsRecyclerInterface;
    private LayoutInflater layoutInflater;
    private List<ClientTransactions> transactions = new ArrayList<>();

    /**
     * Creates an adapter that handles a list of payments from history
     *
     * @param transactionsRecyclerInterface listener indicating events that occurred.
     */
    public ClientTransactionsRecyclerAdapter(ClientTransactionsRecyclerInterface transactionsRecyclerInterface){
        this.transactionsRecyclerInterface = transactionsRecyclerInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.list_item_client_transactions, parent, false);
        return new ViewHolder(view, transactionsRecyclerInterface);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ClientTransactions transactions = getTransactionItem(position);
        holder.bindContent(transactions);

        holder.referenceNumberTv.setText(String.valueOf(transactions.getReference_number()));
        holder.descriptionTv.setText(transactions.getDescription());
        holder.amountTv.setText(String.valueOf(transactions.getAmount()));
        holder.dateTv.setText(String.valueOf(transactions.getDate()));
    }

    private ClientTransactions getTransactionItem(int position) {
        return transactions.get(position);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void addPayments(List<ClientTransactions> transactions) {
        if (transactions != null && !transactions.isEmpty()) {
            transactions.addAll(transactions);
            notifyDataSetChanged();
        } else {
            Timber.e("Adding empty transactions list.");
        }
    }

    /**
     * Clear all data.
     */
    public void clear() {
        transactions.clear();
    }

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView referenceNumberTv;
        private TextView descriptionTv;
        private TextView dateTv;
        private TextView amountTv;

        private ClientTransactions transactions;

        public ViewHolder(View itemView, final ClientTransactionsRecyclerInterface transactionsRecyclerInterface) {
            super(itemView);
            referenceNumberTv = (TextView) itemView.findViewById(R.id.client_transaction_reference_number);
            dateTv = (TextView) itemView.findViewById(R.id.client_transaction_date);
            descriptionTv = (TextView) itemView.findViewById(R.id.client_transaction_description);
            amountTv = (TextView) itemView.findViewById(R.id.client_transaction_amount);
        }

        public void bindContent(ClientTransactions transactions) {
            this.transactions = transactions;
        }
    }
}
