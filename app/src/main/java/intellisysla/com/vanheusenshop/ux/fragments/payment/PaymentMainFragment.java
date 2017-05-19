package intellisysla.com.vanheusenshop.ux.fragments.payment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import intellisysla.com.vanheusenshop.CONST;
import intellisysla.com.vanheusenshop.MyApplication;
import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.SettingsMy;
import intellisysla.com.vanheusenshop.api.EndPoints;
import intellisysla.com.vanheusenshop.api.GsonRequest;
import intellisysla.com.vanheusenshop.entities.Bank;
import intellisysla.com.vanheusenshop.entities.BankResponse;
import intellisysla.com.vanheusenshop.entities.User.User;
import intellisysla.com.vanheusenshop.entities.client.Client;
import intellisysla.com.vanheusenshop.entities.client.DocumentListResponse;
import intellisysla.com.vanheusenshop.entities.payment.Cash;
import intellisysla.com.vanheusenshop.entities.payment.CheckPayment;
import intellisysla.com.vanheusenshop.entities.payment.InvoiceItem;
import intellisysla.com.vanheusenshop.entities.payment.Payment;
import intellisysla.com.vanheusenshop.entities.payment.Transfer;
import intellisysla.com.vanheusenshop.listeners.OnSingleClickListener;
import intellisysla.com.vanheusenshop.utils.JsonUtils;
import intellisysla.com.vanheusenshop.utils.MsgUtils;
import intellisysla.com.vanheusenshop.ux.MainActivity;
import intellisysla.com.vanheusenshop.ux.dialogs.LoginExpiredDialogFragment;
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
    private List<Fragment> fragments;
    protected TextView cashText, transferText, checkText, totalText, totalInvoiceText;
    private double cash = 0, transfer = 0, check = 0, total = 0, totalInvoice = 0;
    private Button paymentSave;
    private Client client;
    private ArrayList<Bank> banks;
    private Payment payment;

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

    public void UpdateCash(Double cash)
    {
        this.cash = cash;
        this.cashText.setText(String.valueOf(cash));
        UpdateTotal();
    }

    public void UpdateTransfer(Double transfer)
    {
        this.transfer = transfer;
        this.transferText.setText(String.valueOf(transfer));
        UpdateTotal();
    }

    public void UpdateCheck(Double check)
    {
        this.check = check;
        this.checkText.setText(String.valueOf(check));
        UpdateTotal();
    }

    public void AddInvoice(Double invoice)
    {
        if(invoice > 0){
            this.totalInvoice += invoice;
            this.totalInvoiceText.setText(String.valueOf(this.totalInvoice));
            UpdateTotal();
        }
    }

    public void RestInvoice(Double invoice)
    {
        if(invoice > 0){
            this.totalInvoice -= invoice;
            this.totalInvoiceText.setText(String.valueOf(this.totalInvoice));
            UpdateTotal();
        }
    }

    public void UpdateTotal()
    {
        Double total = cash + transfer + check;
        this.totalText.setText(String.valueOf(total));
        this.totalInvoiceText.setText(String.valueOf(this.totalInvoice));
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
        String url = String.format(EndPoints.CLIENT, card_code);
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

    private void getBanks() {
        setContentVisible(CONST.VISIBLE.PROGRESS);
        GsonRequest<BankResponse> banksGsonRequest = new GsonRequest<>(Request.Method.GET, EndPoints.BANKS, null, BankResponse.class,
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_main, container, false);

        MainActivity.setActionBarTitle(getString(R.string.Payments));
        progressView = (ProgressBar) view.findViewById(R.id.payment_progress);
        cashText = (TextView) view.findViewById(R.id.payment_main_cash);
        transferText = (TextView) view.findViewById(R.id.payment_main_transfer);
        checkText = (TextView) view.findViewById(R.id.payment_main_check);
        totalText = (TextView) view.findViewById(R.id.payment_main_total);
        totalInvoiceText = (TextView) view.findViewById(R.id.payment_main_paid_total);

        Bundle arguments = getArguments();
        if(arguments != null){
            payment = (Payment) arguments.getSerializable(ARG_PAYMENT);
            if(payment != null){
                setData();
                getBanks();
            }else{
                String cardcode = arguments.getString(ARG_CARDCODE, "");
                client = (Client)arguments.getSerializable(ARG_CLIENT);
                if(client == null)
                    getClient(cardcode);
                else{
                    getBanks();
                }
            }
        }

        mSectionsPagerAdapter = new PaymentMainFragment.SectionsPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.payment_view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        return view;
    }

    public void setData(){
        cashText.setText(String.valueOf(payment.getCash().getAmount()));
        transferText.setText(String.valueOf(payment.getTransfer().getAmount()));
        totalText.setText(String.valueOf(payment.getTotalPaid()));

        Double totalChecks = 0.0;
        for(CheckPayment c: payment.getChecks()){
            total += c.getAmount();
        }
        checkText.setText(String.valueOf(totalChecks));

        Double totalInvoices = 0.0;
        for(InvoiceItem i:payment.getInvoices()){
            totalInvoices += i.getTotalAmount();
        }
        totalInvoiceText.setText(String.valueOf(totalInvoices));
    }

    public void setFragments(View view){

        mSectionsPagerAdapter = new PaymentMainFragment.SectionsPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.payment_view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
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


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int count = 5;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            switch (position){
                case 0: fragment = payment == null ? PaymentGeneralFragment.newInstance(client) : PaymentGeneralFragment.newInstance(payment);
                    break;
                case 1: fragment = payment == null ? PaymentInvoiceFragment.newInstance(client.getInvoiceList()) : PaymentInvoiceFragment.newInstance(payment);
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
