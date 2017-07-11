package grintsys.com.vanshop.ux.fragments.payment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import grintsys.com.vanshop.CONST;
import grintsys.com.vanshop.MyApplication;
import grintsys.com.vanshop.R;
import grintsys.com.vanshop.SettingsMy;
import grintsys.com.vanshop.api.EndPoints;
import grintsys.com.vanshop.api.GsonRequest;
import grintsys.com.vanshop.entities.Bank;
import grintsys.com.vanshop.entities.BankResponse;
import grintsys.com.vanshop.entities.User.User;
import grintsys.com.vanshop.entities.client.Client;
import grintsys.com.vanshop.entities.client.Document;
import grintsys.com.vanshop.entities.invoice.Invoice;
import grintsys.com.vanshop.entities.payment.Cash;
import grintsys.com.vanshop.entities.payment.CheckPayment;
import grintsys.com.vanshop.entities.payment.InvoiceItem;
import grintsys.com.vanshop.entities.payment.Payment;
import grintsys.com.vanshop.entities.payment.Transfer;
import grintsys.com.vanshop.listeners.OnSingleClickListener;
import grintsys.com.vanshop.utils.MsgUtils;
import grintsys.com.vanshop.ux.MainActivity;
import timber.log.Timber;

public class PaymentMainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CARDCODE = "cardcode";
    private static final String ARG_CLIENT = "client-payment";
    private static final String ARG_PAYMENT = "payment";

    // TODO: Rename and change types of parameters
    private String mCardCode;

    private PaymentMainFragment.SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ProgressBar progressView;
    protected TextView cashText, transferText, checkText, totalText, totalInvoiceText;
    private Client client;
    private ArrayList<Bank> banks;
    private Payment payment;
    private Button paymentSaveButton, paymentSentToSapButton, paymentCancelButton;


    private OnFragmentInteractionListener mListener;

    public PaymentMainFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PaymentMainFragment newInstance(String cardCode) {
        PaymentMainFragment fragment = new PaymentMainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CARDCODE, cardCode);
        fragment.setArguments(args);
        return fragment;
    }

    public static PaymentMainFragment newInstance(Payment payment) {
        PaymentMainFragment fragment = new PaymentMainFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PAYMENT, payment);
        fragment.setArguments(args);
        return fragment;
    }

    public static PaymentMainFragment newInstance(Client client) {
        PaymentMainFragment fragment = new PaymentMainFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CLIENT, client);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCardCode = getArguments().getString(ARG_CARDCODE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        setContentVisible(CONST.VISIBLE.CONTENT);
    }

    private void setContentVisible(CONST.VISIBLE visible) {
        if (progressView != null) {
            switch (visible) {
                case PROGRESS:
                    progressView.setVisibility(View.VISIBLE);
                    break;
                default: // Content
                    progressView.setVisibility(View.GONE);
            }
        } else {
            Timber.e(new RuntimeException(), "Setting content visibility with null views.");
        }
    }

    private void getClient(String card_code) {

        final User user = SettingsMy.getActiveUser();

        if (user != null) {
            String url = String.format(EndPoints.CLIENT, user.getId(), card_code);
            setContentVisible(CONST.VISIBLE.PROGRESS);

            GsonRequest<Client> clientGsonRequest = new GsonRequest<>(Request.Method.GET, url, null, Client.class,
                    new Response.Listener<Client>() {
                        @Override
                        public void onResponse(@NonNull Client response) {
                            client = response;
                            getBanks();
                            setContentVisible(CONST.VISIBLE.CONTENT);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setContentVisible(CONST.VISIBLE.EMPTY);
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);
                }
            });
            clientGsonRequest.setRetryPolicy(MyApplication.getSimpleRetryPolice());
            clientGsonRequest.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(clientGsonRequest, CONST.CLIENT_REQUESTS_TAG);
        }
    }

    private void getBanks() {

        final User user = SettingsMy.getActiveUser();

        if (user != null) {
            String url = String.format(EndPoints.BANKS, user.getId());

            setContentVisible(CONST.VISIBLE.PROGRESS);
            GsonRequest<BankResponse> banksGsonRequest = new GsonRequest<>(Request.Method.GET, url, null, BankResponse.class,
                    new Response.Listener<BankResponse>() {
                        @Override
                        public void onResponse(@NonNull BankResponse response) {
                            banks = response.getBanks();
                            setContentVisible(CONST.VISIBLE.CONTENT);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);
                }
            });
            banksGsonRequest.setRetryPolicy(MyApplication.getSimpleRetryPolice());
            banksGsonRequest.setShouldCache(true);
            MyApplication.getInstance().addToRequestQueue(banksGsonRequest, CONST.BANKS_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_main, container, false);

        MainActivity.setActionBarTitle(getString(R.string.Payments));
        progressView = (ProgressBar) view.findViewById(R.id.payment_progress);

        paymentSentToSapButton = (Button) view.findViewById(R.id.product_payment_main_sent_to_sap);
        paymentSaveButton = (Button) view.findViewById(R.id.product_payment_main_save);
        paymentCancelButton = (Button) view.findViewById(R.id.product_payment_main_cancel);

        paymentSentToSapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPaymentToSap();
                ((MainActivity) getActivity()).onAccountSelected();
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.PaymentProcess), MsgUtils.ToastLength.SHORT);
            }
        });

        paymentSaveButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                sendPaymentToDraft();
                ((MainActivity) getActivity()).onAccountSelected();
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.PaymentProcess), MsgUtils.ToastLength.SHORT);
            }
        });

        paymentCancelButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                cancelPayment(payment.getId());
                ((MainActivity)getActivity()).ClearPaymentData();
                ((MainActivity)getActivity()).onAccountSelected();
            }
        });

        Bundle arguments = getArguments();
        if(arguments != null){
            payment = (Payment) arguments.getSerializable(ARG_PAYMENT);
            if(payment != null){
                ((MainActivity) getActivity()).ClearPaymentData();
                ((MainActivity) getActivity()).UpdateTransfer(payment.getTransfer());
                ((MainActivity) getActivity()).UpdateCash(payment.getCash());
                ((MainActivity) getActivity()).UpdateChecks(payment.getChecks());
                //((MainActivity) getActivity()).UpdateInvoiceItems(payment.getInvoices());
                getBanks();
            } else {
                String cardcode = arguments.getString(ARG_CARDCODE, "");
                client = (Client) arguments.getSerializable(ARG_CLIENT);
                if(client == null)
                    getClient(cardcode);
                else {
                    getBanks();
                }
            }
        }

        if(payment != null) {
            switch (payment.getStatus()){
                case 1:
                    paymentSaveButton.setVisibility(View.GONE);
                    paymentSentToSapButton.setVisibility(View.VISIBLE);
                    paymentCancelButton.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    paymentSaveButton.setVisibility(View.GONE);
                    paymentSentToSapButton.setVisibility(View.VISIBLE);
                    paymentCancelButton.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    paymentSaveButton.setVisibility(View.GONE);
                    paymentSentToSapButton.setVisibility(View.GONE);
                    paymentCancelButton.setVisibility(View.GONE);
                    break;
                case 4:
                    paymentCancelButton.setVisibility(View.GONE);
                    paymentSaveButton.setVisibility(View.GONE);
                    paymentSentToSapButton.setVisibility(View.GONE);
                    break;
                default:
                    paymentCancelButton.setVisibility(View.VISIBLE);
                    paymentSaveButton.setVisibility(View.VISIBLE);
                    paymentSentToSapButton.setVisibility(View.GONE);
            }
        }

        mSectionsPagerAdapter = new PaymentMainFragment.SectionsPagerAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.payment_view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        return view;
    }

    public void sendPaymentToDraft() {

        final User user = SettingsMy.getActiveUser();

        if (user != null) {

            Cash cash = ((MainActivity) getActivity()).getCash();
            cash.setGeneralAccount("_SYS00000001377");
            Transfer transfer = ((MainActivity) getActivity()).getTransfer();
            ArrayList<CheckPayment> checks = ((MainActivity) getActivity()).getChecks();
            ArrayList<InvoiceItem> invoices = ((MainActivity) getActivity()).getInvoiceItems();
            String comment = ((MainActivity) getActivity()).getComment();
            String reference = ((MainActivity) getActivity()).getReferenceNumber();

            //TODO: este es un fix temporal ya que necesitaban con urgencia este parche
            // el problema es que cuando dejan la fecha al dia de hoy sin tocar el campo de fecha
            // esta no se estaba guardando
            if(transfer.getDueDate() == null){
                final Calendar myCalendar = Calendar.getInstance();
                String myFormat = "yyyy/MM/dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                transfer.setDueDate(sdf.format(myCalendar.getTime()));
            }

            putPayment(client, cash, transfer, checks, invoices, comment, reference);
        }
    }

    public void sendPaymentToSap()
    {
        final User user = SettingsMy.getActiveUser();

        if (user != null) {

            Cash cash = ((MainActivity) getActivity()).getCash();
            Transfer transfer = ((MainActivity) getActivity()).getTransfer();
            ArrayList<CheckPayment> checks = ((MainActivity) getActivity()).getChecks();
            ArrayList<InvoiceItem> invoices = ((MainActivity) getActivity()).getInvoiceItems();
            String comment = ((MainActivity) getActivity()).getComment();
            String reference = ((MainActivity) getActivity()).getReferenceNumber();

            /*ArrayList<InvoiceItem> invoices2 = new ArrayList<>();
            for(InvoiceItem item: invoices){

                if(invoices2.size() > 0) {

                    Boolean isFound = false;
                    for (InvoiceItem item2 : invoices2) {
                        if(item2.getDocumentNumber().equals(item.getDocumentNumber()))
                        {
                            isFound = true;
                            break;
                        }
                    }

                    if(!isFound)
                        invoices2.add(item);
                }else
                {
                    invoices2.add(item);
                }
            }*/

           /* if(invoices.size() > 0)
                invoices = invoices2;*/

            JSONObject joTransfer = new JSONObject();
            JSONObject joCash = new JSONObject();
            JSONArray joChecks = new JSONArray();
            JSONArray joInvoices = new JSONArray();

            if(invoices.size() > 0){
                try
                {
                    for (InvoiceItem invoice : invoices)
                    {
                        JSONObject invoiceJSON = new JSONObject();
                        invoiceJSON.put("DocumentNumber", invoice.getDocumentNumber());
                        invoiceJSON.put("TotalAmount", invoice.getTotalAmount());
                        invoiceJSON.put("PayedAmount", invoice.getPayedAmount());
                        invoiceJSON.put("DocEntry", invoice.getDocEntry());
                        joInvoices.put(invoiceJSON);
                    }
                    //jObject.put("StudentList", joInvoices);
                } catch (JSONException e) {
                    Timber.e(e, "Parse new Invoice exception.");
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, "Parse new Invoice exception.", MsgUtils.ToastLength.SHORT);
                }

                if (transfer != null) {
                    try {
                        joTransfer.put("ReferenceNumber", transfer.getNumber());
                        joTransfer.put("Amount", transfer.getAmount());
                        joTransfer.put("Date", transfer.getDueDate());
                        joTransfer.put("GeneralAccount", transfer.getBank().getGeneralAccount());
                    } catch (JSONException e) {
                        Timber.e(e, "Parse new transfer exception.");
                        MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, "Parse new transfer exception.", MsgUtils.ToastLength.SHORT);
                    }
                }

                try {
                    joCash.put("GeneralAccount", cash.getGeneralAccount());
                    joCash.put("Amount", cash.getAmount());
                } catch (JSONException e) {
                    Timber.e(e, "Parse new chash exception.");
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, "Parse new chash exception.", MsgUtils.ToastLength.SHORT);
                }

                if(checks != null) {
                    try {
                        for (CheckPayment c : checks) {
                            JSONObject checkJSON = new JSONObject();
                            checkJSON.put("RefenceNumber", c.getCheckNumber());
                            checkJSON.put("BankId", c.getBank().getId());
                            checkJSON.put("Amount", c.getAmount());
                            checkJSON.put("Date", c.getDate());
                            joInvoices.put(checkJSON);
                        }
                    } catch (JSONException e) {
                        Timber.e(e, "Parse checks exception.");
                        MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, "Parse checks exception", MsgUtils.ToastLength.SHORT);
                    }
                }

            } else {
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, "Debe ingresar al menos una factura", MsgUtils.ToastLength.LONG);
            }

            try{
                comment = URLEncoder.encode(comment, "UTF-8");
                reference = URLEncoder.encode(reference, "UTF-8");
            }
            catch (Exception e)
            {
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, "Comentario contiene caracteres incorrectos", MsgUtils.ToastLength.LONG);
            }

            double total = transfer != null ? transfer.getAmount() : 0.0;
            //SentPayment?userId=%d&clientId=%d&totalPaid=%d&cash=%s&transfer=%s&checks=%s%invoices=%s
            String url = String.format(EndPoints.SENT_PAYMENT,
                    user.getId(),
                    payment.getClient().getId(),
                    total,
                    comment,
                    joCash.toString(),
                    joTransfer.toString(),
                    joChecks.toString(),
                    joInvoices.toString(),
                    reference,
                    payment.getId());

            ((MainActivity) getActivity()).ClearPaymentData();

            GsonRequest<JSONObject> req = new GsonRequest<>(Request.Method.GET, url, null, JSONObject.class,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(@NonNull JSONObject payment) {
                            //progressDialog.cancel();
                            Timber.d("Esto devolvio %s", payment);
                            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Success), MsgUtils.ToastLength.SHORT);
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
            MyApplication.getInstance().addToRequestQueue(req, CONST.SENT_PAYMENT_TAG);
        } else {
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, "Usuario no logeado", MsgUtils.ToastLength.SHORT);
            //error user is not login
        }
    }

    private void putPayment(Client client, Cash cash, Transfer transfer, ArrayList<CheckPayment> checks, ArrayList<InvoiceItem> invoices, String comment, String refence) {

        final User user = SettingsMy.getActiveUser();

        if (user != null) {
            JSONObject joTransfer = new JSONObject();
            JSONObject joCash = new JSONObject();
            JSONArray joChecks = new JSONArray();
            JSONArray joInvoices = new JSONArray();

            if(invoices != null  && invoices.size() > 0){
                try
                {
                    for (InvoiceItem invoice : invoices)
                    {
                        JSONObject invoiceJSON = new JSONObject();
                        invoiceJSON.put("DocumentNumber", invoice.getDocumentNumber());
                        invoiceJSON.put("TotalAmount", invoice.getTotalAmount());
                        invoiceJSON.put("PayedAmount", invoice.getPayedAmount());
                        invoiceJSON.put("DocEntry", invoice.getDocEntry());
                        joInvoices.put(invoiceJSON);
                    }
                    //jObject.put("StudentList", joInvoices);
                } catch (JSONException e) {
                    Timber.e(e, "Parse new Invoice exception.");
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, "Parse new Invoice exception.", MsgUtils.ToastLength.SHORT);
                }

                if (transfer != null) {
                    try {
                        joTransfer.put("ReferenceNumber", transfer.getNumber());
                        joTransfer.put("Amount", transfer.getAmount());
                        joTransfer.put("Date", transfer.getDueDate());
                        joTransfer.put("GeneralAccount", transfer.getBank().getGeneralAccount());
                    } catch (JSONException e) {
                        Timber.e(e, "Parse new transfer exception.");
                        MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, "Parse new transfer exception.", MsgUtils.ToastLength.SHORT);
                    }
                }

                if (cash != null) {
                    try {
                        joCash.put("GeneralAccount", cash.getGeneralAccount());
                        joCash.put("Amount", cash.getAmount());
                    } catch (JSONException e) {
                        Timber.e(e, "Parse new chash exception.");
                        MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, "Parse new chash exception.", MsgUtils.ToastLength.SHORT);
                    }
                }

                if(checks != null) {
                    try {
                        for (CheckPayment c : checks) {
                            JSONObject checkJSON = new JSONObject();
                            checkJSON.put("RefenceNumber", c.getCheckNumber());
                            checkJSON.put("BankId", c.getBank().getId());
                            checkJSON.put("Amount", c.getAmount());
                            checkJSON.put("DueDate", c.getDate());
                            joInvoices.put(checkJSON);
                        }
                    } catch (JSONException e) {
                        Timber.e(e, "Parse checks exception.");
                        MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, "Parse checks exception", MsgUtils.ToastLength.SHORT);
                    }
                }

            } else {
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, "Debe ingresar al menos una factura", MsgUtils.ToastLength.LONG);
            }

            try{
                comment = URLEncoder.encode(comment, "UTF-8");
            }
            catch (Exception e)
            {
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, "Comentario contiene caracteres incorrectos", MsgUtils.ToastLength.LONG);
            }

            double total = transfer != null ? transfer.getAmount() : 0.0;
            //AddPayment?userId=%d&clientId=%d&totalPaid=%d&cash=%s&transfer=%s&checks=%s%invoices=%s
            String url = String.format(EndPoints.ADD_PAYMENT,
                    user.getId(),
                    client.getId(),
                    total,
                    comment,
                    joCash.toString(),
                    joTransfer.toString(),
                    joChecks.toString(),
                    joInvoices.toString(),
                    refence);

            ((MainActivity) getActivity()).ClearPaymentData();

            GsonRequest<JSONObject> req = new GsonRequest<>(Request.Method.GET, url, null, JSONObject.class,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(@NonNull JSONObject payment) {
                            //progressDialog.cancel();
                            Timber.d("Esto devolvio %s", payment);
                            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Success), MsgUtils.ToastLength.SHORT);
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
        } else {
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, "Usuario no logeado", MsgUtils.ToastLength.SHORT);
            //error user is not login
        }
    }

    public void setFragments(View view){

        mSectionsPagerAdapter = new PaymentMainFragment.SectionsPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.payment_view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }


    public void cancelPayment(int id)
    {
        final User user = SettingsMy.getActiveUser();

        if(user != null) {
            String url = String.format(EndPoints.CANCEL_PAYMENT, user.getId(), id);

            GsonRequest<JSONObject> req = new GsonRequest<>(Request.Method.GET, url, null, JSONObject.class,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(@NonNull JSONObject payment) {
                            Timber.d("Esto devolvio %s", payment);
                            ((MainActivity) getActivity()).ClearPaymentData();
                            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Success), MsgUtils.ToastLength.SHORT);
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
            MyApplication.getInstance().addToRequestQueue(req, CONST.CANCEL_PAYMENT_TAG);
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


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int count = 3;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            switch (position){
                case 0: fragment = payment == null ? PaymentGeneralFragment.newInstance(client) : PaymentGeneralFragment.newInstance(payment);
                    break;
                case 1:
                    fragment = payment == null ? PaymentInvoiceFragment.newInstance(client.getInvoiceList()) : PaymentInvoiceFragment.newInstance(payment);
                    break;
                case 2: fragment = payment == null ? PaymentTransferFragment.newInstance(banks) : PaymentTransferFragment.newInstance(payment.getTransfer(), banks);
                    break;
                case 3: fragment = payment == null ? PaymentCashFragment.newInstance() : PaymentCashFragment.newInstance(payment.getCash());
                    break;
                case 4:
                    fragment = payment == null ? PaymentCheckFragment.newInstance(banks) : PaymentCheckFragment.newInstance(payment, payment.getChecks());
                    break;
                default:
                    fragment = null;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return count;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            /*Fragment fragment = fragments.get(position);
            String title = "";
            if(fragment instanceof PaymentGeneralFragment){
                title = "General";
            }
            else if(fragment instanceof PaymentCashFragment){
                title = "Efectivo";
                double cash = ((PaymentCashFragment) fragment).cashValue;

            }
            else if(fragment instanceof PaymentTransferFragment){
                title = "Transferencia";
            }
            else if(fragment instanceof PaymentCheckFragment){
                title = "Cheque";
            }
            else if(fragment instanceof PaymentInvoiceFragment){
                title = "Factura";
            }
            else{
                title = "";
            }*/

            String title = "";

            switch (position){
                case 0:  title = "General";
                    break;
                case 1: title = "Factura";
                    break;
                case 2: title = "Transferencia";
                    break;
                case 3: title = "Efectivo";
                    break;
                case 4: title = "Cheque";
                    break;
            }

            return title;
        }
    }
}
