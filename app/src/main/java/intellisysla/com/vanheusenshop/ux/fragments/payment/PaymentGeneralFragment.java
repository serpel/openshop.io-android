package intellisysla.com.vanheusenshop.ux.fragments.payment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import intellisysla.com.vanheusenshop.CONST;
import intellisysla.com.vanheusenshop.MyApplication;
import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.SettingsMy;
import intellisysla.com.vanheusenshop.api.EndPoints;
import intellisysla.com.vanheusenshop.api.GsonRequest;
import intellisysla.com.vanheusenshop.entities.Bank;
import intellisysla.com.vanheusenshop.entities.User.User;
import intellisysla.com.vanheusenshop.entities.client.Client;
import intellisysla.com.vanheusenshop.entities.payment.Cash;
import intellisysla.com.vanheusenshop.entities.payment.CheckPayment;
import intellisysla.com.vanheusenshop.entities.payment.Payment;
import intellisysla.com.vanheusenshop.entities.payment.Transfer;
import intellisysla.com.vanheusenshop.listeners.OnSingleClickListener;
import intellisysla.com.vanheusenshop.utils.MsgUtils;
import intellisysla.com.vanheusenshop.ux.MainActivity;
import timber.log.Timber;

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
    private EditText clientContact;
    private TextView clientName;

    // TODO: Rename and change types of parameters
    private Client client;
    private Button paymentSave, paymentCancel;

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

        clientCardCode = (TextView) view.findViewById(R.id.payment_general_client_code);
        clientName = (TextView) view.findViewById(R.id.payment_general_client_name);
        clientContact = (EditText) view.findViewById(R.id.payment_general_contact);
        paymentSave = (Button) view.findViewById(R.id.product_payment_general_ok);
        paymentCancel = (Button) view.findViewById(R.id.product_payment_general_cancel);

        paymentSave.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if(client != null){
                    double totalPaid = ((MainActivity)getActivity()).getTotalPaid();
                    Cash cash = ((MainActivity) getActivity()).getCash();
                    cash.setGeneralAccount("_SYS00000001377");
                    Transfer transfer = ((MainActivity) getActivity()).getTransfer();
                    ArrayList<CheckPayment> checks = ((MainActivity)getActivity()).getChecks();
                    putPayment(client, cash, transfer, checks, totalPaid);

                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.PaymentProcess), MsgUtils.ToastLength.SHORT);
                    ((MainActivity)getActivity()).onAccountSelected();
                }
            }
        });

        paymentCancel.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                ((MainActivity)getActivity()).ClearPaymentData();
                ((MainActivity)getActivity()).onOpenClientFragment();
            }
        });

        Bundle args = getArguments();

        if(args != null){
            client = (Client) args.getSerializable(ARG_CLIENT);
            clientName.setText(client.getName());
            clientCardCode.setText(client.getCardCode());

            if(client.getContact() != null)
                clientContact.setText(client.getContact());
        }

        return view;
    }

    private void putPayment(Client client, Cash cash, Transfer transfer, ArrayList<CheckPayment> checks, double totalPaid) {

        final User user = SettingsMy.getActiveUser();

        if (user != null) {
            JSONObject joTransfer = new JSONObject();
            JSONObject joCash = new JSONObject();
            JSONArray joChecks = new JSONArray(checks);

            if (transfer != null) {
                //joTransfer = new JSONObject();
                try {
                    joTransfer.put("ReferenceNumber", transfer.getNumber());
                    joTransfer.put("Amount", transfer.getAmount());
                    joTransfer.put("Date", transfer.getDueDate());
                    joTransfer.put("GeneralAccount", transfer.getBank().getGeneralAccount());
                } catch (JSONException e) {
                    Timber.e(e, "Parse new transfer exception.");
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                }
            }

            if (cash != null) {
                //joCash = new JSONObject();
                try {
                    joCash.put("GeneralAccount", cash.getGeneralAccount());
                    joCash.put("Amount", cash.getAmount());
                } catch (JSONException e) {
                    Timber.e(e, "Parse new chash exception.");
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                }
            }

            /*if (checks != null) {
                try {
                    joArray = new JSONArray(checks);
                } catch (Exception e) {
                    Timber.e(e, "Parse new chash exception.");
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                }
            }
            */

            //AddPayment?userId=%d&clientId=%d&totalPaid=%d&cash=%s&transfer=%s&checks=%s
            String url = String.format(EndPoints.ADD_PAYMENT,
                    user.getId(),
                    client.getId(),
                    totalPaid,
                    joCash.toString(),
                    joTransfer.toString(),
                    joChecks.toString()
                    );

            GsonRequest<JSONObject> req = new GsonRequest<>(Request.Method.GET, url, null, JSONObject.class,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(@NonNull JSONObject payment) {
                            //progressDialog.cancel();
                            Timber.d("Esto devolvio %s", payment);
                            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Ok), MsgUtils.ToastLength.SHORT);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //if (progressDialog != null) progressDialog.cancel();
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);
                }
            }, getFragmentManager(), null);
            req.setRetryPolicy(MyApplication.getSimpleRetryPolice());
            req.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(req, CONST.ADD_PAYMENT_TAG);
        }
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
