package intellisysla.com.vanheusenshop.ux.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.entities.client.Document;
import intellisysla.com.vanheusenshop.entities.payment.CheckPayment;
import intellisysla.com.vanheusenshop.interfaces.ChecksRecyclerInterface;
import intellisysla.com.vanheusenshop.interfaces.DocumentRecyclerInterface;
import intellisysla.com.vanheusenshop.ux.MainActivity;

/**
 * Created by alienware on 2/1/2017.
 */

public class ChecksRecyclerAdapter extends RecyclerView.Adapter<ChecksRecyclerAdapter.ViewHolder> {

    private Context context;
    private ChecksRecyclerInterface checksRecyclerInterface;
    private List<CheckPayment> checks = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public ChecksRecyclerAdapter(Context context, ChecksRecyclerInterface checksRecyclerInterface) {
        this.context = context;
        this.checksRecyclerInterface = checksRecyclerInterface;
    }

    public CheckPayment getItem(int position) {
        return this.checks.get(position);
    }

    @Override
    public int getItemCount() {
        return this.checks.size();
    }

    public void addCheck(CheckPayment check){
        this.checks.add(check);
        notifyDataSetChanged();
    }

    public void clear() {
        checks.clear();
    }

    @Override
    public ChecksRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.list_item_check, parent, false);

        return new ChecksRecyclerAdapter.ViewHolder(context, view, checksRecyclerInterface);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CheckPayment checkPayment = getItem(position);
        holder.bindContent(checkPayment);
      /*  // - replace the contents of the view with that element
        holder.documentCode.setText(holder.document.getDocumentCode());
        holder.createdDate.setText(holder.document.getCreatedDate());
        holder.dueDate.setText(holder.document.getDueDate());
        holder.pastDueAmount.setText(NumberFormat.getNumberInstance(Locale.US).format(0));
        holder.totalAmount.setText(NumberFormat.getNumberInstance(Locale.US).format(holder.document.getTotalAmount()));
        holder.balanceDue.setText(NumberFormat.getNumberInstance(Locale.US).format(holder.document.getBalanceDue()));
        holder.overdueDays.setText(NumberFormat.getNumberInstance(Locale.US).format(holder.document.getOverdueDays()));*/
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView documentCode;
        public TextView createdDate;
        public TextView dueDate;
        public TextView totalAmount;
        public TextView balanceDue, overdueDays, pastDueAmount;
        public TextView selectedText;
        public CheckBox selectedCheck;
        //public TextView clientCode;
        private CheckPayment check;

        public ViewHolder(final Context context, View v, final ChecksRecyclerInterface checksRecyclerInterface) {
            super(v);
            //clientCode = (TextView) v.findViewById(R.id.document_client_code);
           /* documentCode = (TextView) v.findViewById(R.id.document_code);
            createdDate = (TextView) v.findViewById(R.id.document_created_date);
            dueDate = (TextView) v.findViewById(R.id.document_due_date);
            totalAmount = (TextView) v.findViewById(R.id.document_total_amount);
            pastDueAmount = (TextView) v.findViewById(R.id.document_past_due_amount);
            balanceDue = (TextView) v.findViewById(R.id.document_balance_due);
            overdueDays = (TextView) v.findViewById(R.id.document_overdue_days);

            selectedCheck = (CheckBox) v.findViewById(R.id.document_selected_checkbox);
            selectedText = (TextView) v.findViewById(R.id.document_selected_checkbox_text);*/

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checksRecyclerInterface.onCheckSelected(v, check);
                }
            });
        }

        public void bindContent(CheckPayment check) {
            this.check = check;
        }
    }
}
