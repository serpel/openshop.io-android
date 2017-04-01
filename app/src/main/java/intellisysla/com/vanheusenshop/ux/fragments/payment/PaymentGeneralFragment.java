package intellisysla.com.vanheusenshop.ux.fragments.payment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.entities.Bank;
import intellisysla.com.vanheusenshop.entities.client.Client;
import intellisysla.com.vanheusenshop.entities.payment.Cash;
import intellisysla.com.vanheusenshop.entities.payment.Transfer;
import intellisysla.com.vanheusenshop.listeners.OnSingleClickListener;
import intellisysla.com.vanheusenshop.utils.MsgUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PaymentGeneralFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PaymentGeneralFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentGeneralFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CLIENT = "client";
    private TextView clientCardCode;
    private TextView clientContact;
    private TextView clientName;

    // TODO: Rename and change types of parameters
    private Client client;
    private Button paymentSave;

    private OnFragmentInteractionListener mListener;

    public PaymentGeneralFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PaymentGeneralFragment newInstance(Client client) {
        PaymentGeneralFragment fragment = new PaymentGeneralFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CLIENT, client);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            client = (Client) getArguments().getSerializable(ARG_CLIENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_general, container, false);

        clientCardCode = (TextView) view.findViewById(R.id.document_client_code);
        //clientContact = (TextView) view.findViewById(R.id.document_client_code);
        clientName = (TextView) view.findViewById(R.id.document_client_name);
        paymentSave = (Button) view.findViewById(R.id.product_payment_general_ok);

        paymentSave.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
               /* Bank bank = new Bank(0, "BATL", "_SYS00000001377");
                Cash cash = new Cash(0, 10000, "_SYS00000001377");
                Transfer transfer = new Transfer(0, "123", 10000, Date.valueOf("2017-03-31"), bank);

                payment.setTransfer(transfer);
                payment.setClient(client);
                payment.setCash(cash);
                putPayment(payment);*/

                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Ok), MsgUtils.ToastLength.SHORT);
            }
        });

        Bundle args = getArguments();

        if(args != null){
            client = (Client) args.getSerializable(ARG_CLIENT);
            clientName.setText(client.getName());
            clientCardCode.setText(client.getCardCode());
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
