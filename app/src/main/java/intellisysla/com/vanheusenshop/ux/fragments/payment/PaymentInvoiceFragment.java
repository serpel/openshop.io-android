package intellisysla.com.vanheusenshop.ux.fragments.payment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.entities.client.Document;
import intellisysla.com.vanheusenshop.ux.adapters.InvoiceRecyclerAdapter;
import intellisysla.com.vanheusenshop.ux.fragments.dummy.DummyContent.DummyItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link PaymentInvoiceFragment.OnListFragmentInteractionListener}
 * interface.
 */

class DocumentAdapter extends ArrayAdapter<Document> {
    public DocumentAdapter(Context context, ArrayList<Document> documents) {
        super(context, 0, documents);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Document document = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_document, parent, false);
        }
        TextView document_code = (TextView) convertView.findViewById(R.id.document_code);
        TextView due_date = (TextView) convertView.findViewById(R.id.due_date);
        TextView created_date = (TextView) convertView.findViewById(R.id.created_date);
        TextView payed_amount = (TextView) convertView.findViewById(R.id.payed_amount);
        TextView total_amount = (TextView) convertView.findViewById(R.id.total_amount);

        document_code.setText(getContext().getString(R.string.DocumentCode) + ": " + document.getDocumentCode());
        due_date.setText(getContext().getString(R.string.DueDate) + ": " + document.getDueDate());
        created_date.setText(getContext().getString(R.string.CreatedDate) + ": " + document.getCreatedDate());
        payed_amount.setText(getContext().getString(R.string.PayedAmount) + ": " + document.getPayedAmount());
        total_amount.setText(getContext().getString(R.string.TotalAmount) + ": " + document.getTotalAmount());

        return convertView;
    }
}

public class PaymentInvoiceFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_INVOICE_LIST = "invoice-list";
    // TODO: Customize parameters
    private ArrayList<Document> documents;
    ListView my_listview;
    private OnListFragmentInteractionListener mListener;
    private InvoiceRecyclerAdapter documentsRecyclerAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PaymentInvoiceFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PaymentInvoiceFragment newInstance(ArrayList<Document> documents) {
        PaymentInvoiceFragment fragment = new PaymentInvoiceFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_INVOICE_LIST, documents);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            documents = (ArrayList<Document>) getArguments().getSerializable(ARG_INVOICE_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_document_list, container, false);

        my_listview = (ListView)view.findViewById(R.id.invoice_list_view);

        Bundle startBundle = getArguments();
        if (startBundle != null) {
            documents = (ArrayList<Document>) getArguments().getSerializable(ARG_INVOICE_LIST);
        }

        if(documents != null) {
            DocumentAdapter adapter = new DocumentAdapter(view.getContext(), documents);
            my_listview.setAdapter(adapter);
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
