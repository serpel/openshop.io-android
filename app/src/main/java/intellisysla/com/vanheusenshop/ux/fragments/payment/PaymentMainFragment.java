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

    private void getClient(String card_code) {
        String url = String.format(EndPoints.CLIENT, card_code);
        GsonRequest<Client> clientGsonRequest = new GsonRequest<>(Request.Method.GET, url, null, Client.class,
                new Response.Listener<Client>() {
                    @Override
                    public void onResponse(@NonNull Client response) {
                        client = response;
                        getBanks();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        clientGsonRequest.setRetryPolicy(MyApplication.getSimpleRetryPolice());
        clientGsonRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(clientGsonRequest, CONST.CLIENT_REQUESTS_TAG);
    }

    private void getBanks() {
        String url = String.format(EndPoints.BANKS);
        GsonRequest<BankResponse> banksGsonRequest = new GsonRequest<>(Request.Method.GET, url, null, BankResponse.class,
                new Response.Listener<BankResponse>() {
                    @Override
                    public void onResponse(@NonNull BankResponse response) {
                        banks = response.getBanks();
                        setFragments(client);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        banksGsonRequest.setRetryPolicy(MyApplication.getSimpleRetryPolice());
        banksGsonRequest.setShouldCache(true);
        MyApplication.getInstance().addToRequestQueue(banksGsonRequest, CONST.BANNER_REQUESTS_TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_main, container, false);

        cashText = (TextView) view.findViewById(R.id.payment_main_cash);
        transferText = (TextView) view.findViewById(R.id.payment_main_transfer);
        checkText = (TextView) view.findViewById(R.id.payment_main_check);
        totalText = (TextView) view.findViewById(R.id.payment_main_total);
        totalInvoiceText = (TextView) view.findViewById(R.id.payment_main_paid_total);
        //paymentSave = (Button) view.findViewById(R.id.product_payment_general_ok);

        mSectionsPagerAdapter = new PaymentMainFragment.SectionsPagerAdapter(getFragmentManager());

        mViewPager = (ViewPager) view.findViewById(R.id.payment_view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        MainActivity.setActionBarTitle(getString(R.string.Payments));

        /*paymentSave.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Bank bank = new Bank(0, "BATL", "_SYS00000001377");
                Cash cash = new Cash(0, 10000, "_SYS00000001377");
                Transfer transfer = new Transfer(0, "123", 10000, Date.valueOf("2017-03-31"), bank);

                payment.setTransfer(transfer);
                payment.setClient(client);
                payment.setCash(cash);
                putPayment(payment);
            }
        });*/

        Bundle arguments = getArguments();
        if(arguments != null){
            String cardcode = arguments.getString(ARG_CARDCODE, "");
            client = (Client)arguments.getSerializable(ARG_CLIENT);
            if(client == null)
                getClient(cardcode);
            else{
                getBanks();
            }
        }

        return view;
    }

    private void putPayment(Payment payment) {
            if (payment != null) {
                JSONObject joPayment = new JSONObject();
                try {
                    joPayment.put("ClientId", payment.getClient().getId());
                    joPayment.put("CashId", payment.getCash().getId());
                    joPayment.put("TransferId", payment.getTransfer().getId());
                    joPayment.put("TotalAmount", payment.getTotalPaid());
                    joPayment.put("CreatedDate", payment.getDate());
                    joPayment.put("DeviceUserId", 1);
                } catch (JSONException e) {
                    Timber.e(e, "Parse new payment exception.");
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                    return;
                }

                //progressDialog.show();
                GsonRequest<Payment> req = new GsonRequest<>(Request.Method.PUT, EndPoints.ADD_PAYMENT, joPayment.toString(), Payment.class,
                        new Response.Listener<Payment>() {
                            @Override
                            public void onResponse(@NonNull Payment payment) {
                                //progressDialog.cancel();
                                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Ok), MsgUtils.ToastLength.SHORT);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //if (progressDialog != null) progressDialog.cancel();
                        MsgUtils.logAndShowErrorMessage(getActivity(), error);
                    }
                }, getFragmentManager(),  null);
                req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
                req.setShouldCache(false);
                MyApplication.getInstance().addToRequestQueue(req, CONST.ADD_PAYMENT_TAG);
            }
    }

    public void setFragments(Client client){

        fragments = new ArrayList<>();
        fragments.add(PaymentGeneralFragment.newInstance(client));
        fragments.add(PaymentInvoiceFragment.newInstance(client.getInvoiceList()));
        fragments.add(PaymentCashFragment.newInstance());
        fragments.add(PaymentTransferFragment.newInstance(banks));
        fragments.add(PaymentCheckFragment.newInstance(banks));

        mSectionsPagerAdapter.setFragments(fragments);
        mSectionsPagerAdapter.updateView();
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

        private List<Fragment> fragments;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
        }

        public void setFragments(List<Fragment> fragments) {
            this.fragments = fragments;
        }


        public void updateView(){
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Fragment fragment = fragments.get(position);
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
            }

            return title;
        }
    }
}
