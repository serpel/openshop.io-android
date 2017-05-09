package intellisysla.com.vanheusenshop.ux.fragments.payment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.entities.client.Document;
import intellisysla.com.vanheusenshop.entities.payment.Payment;
import intellisysla.com.vanheusenshop.interfaces.DocumentRecyclerInterface;
import intellisysla.com.vanheusenshop.utils.EndlessRecyclerScrollListener;
import intellisysla.com.vanheusenshop.utils.RecyclerMarginDecorator;
import intellisysla.com.vanheusenshop.ux.MainActivity;
import intellisysla.com.vanheusenshop.ux.adapters.DocumentsRecyclerAdapter;
import intellisysla.com.vanheusenshop.ux.adapters.InvoiceRecyclerAdapter;
import intellisysla.com.vanheusenshop.ux.fragments.dummy.DummyContent.DummyItem;
import timber.log.Timber;

public class PaymentInvoiceFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_INVOICE_LIST = "invoice-list";
    private static final String ARG_PAYMENT = "payment";
    // TODO: Customize parameters
    private ArrayList<Document> documents;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView documentsRecycler;
    private GridLayoutManager documentsRecyclerLayoutManager;
    private DocumentsRecyclerAdapter documentsRecyclerAdapter;
    private Payment payment;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PaymentInvoiceFragment() {
    }

    // TODO: Customize parameter initialization
    public static PaymentInvoiceFragment newInstance(ArrayList<Document> documents) {
        PaymentInvoiceFragment fragment = new PaymentInvoiceFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_INVOICE_LIST, documents);
        fragment.setArguments(args);
        return fragment;
    }

    public static PaymentInvoiceFragment newInstance(Payment payment) {
        PaymentInvoiceFragment fragment = new PaymentInvoiceFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PAYMENT, payment);
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

        if (documentsRecyclerAdapter == null || documentsRecyclerAdapter.getItemCount() == 0) {
            prepareRecyclerAdapter();
        }

        prepareDocumentRecycler(view);

        Bundle args = getArguments();
        if (args != null) {
            payment = (Payment) args.getSerializable(ARG_PAYMENT);

            if(payment != null){

                documents = payment.getClient().getInvoiceList();
                if(documents != null && documents.size() > 0 && documentsRecycler != null)
                    documentsRecyclerAdapter.addDocuments(documents);

            } else {
                documents = (ArrayList<Document>) args.getSerializable(ARG_INVOICE_LIST);
                if(documents != null && documents.size() > 0 && documentsRecycler != null)
                    documentsRecyclerAdapter.addDocuments(documents);
            }
        }

        return view;
    }

    private void prepareRecyclerAdapter() {
        //On click event
        documentsRecyclerAdapter = new DocumentsRecyclerAdapter(getActivity(), new DocumentRecyclerInterface() {
            @Override
            public void onDocumentRecyclerInterface(View caller, Document document) {
                //if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                //    setReenterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
                //}
                //((MainActivity) getActivity()).onDocumentSelected(document.getDocumentCode());
            }
        }, true);
    }

    private void prepareDocumentRecycler(View view) {
        documentsRecycler = (RecyclerView) view.findViewById(R.id.payment_document_recycler);
        documentsRecycler.addItemDecoration(new RecyclerMarginDecorator(getActivity(), RecyclerMarginDecorator.ORIENTATION.BOTH));
        documentsRecycler.setItemAnimator(new DefaultItemAnimator());
        documentsRecycler.setHasFixedSize(true);

        documentsRecyclerLayoutManager = new GridLayoutManager(getActivity(), 1);

        documentsRecycler.setLayoutManager(documentsRecyclerLayoutManager);
        documentsRecycler.setAdapter(documentsRecyclerAdapter);
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
