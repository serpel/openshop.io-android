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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import intellisysla.com.vanheusenshop.CONST;
import intellisysla.com.vanheusenshop.MyApplication;
import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.api.EndPoints;
import intellisysla.com.vanheusenshop.api.GsonRequest;
import intellisysla.com.vanheusenshop.entities.Bank;
import intellisysla.com.vanheusenshop.entities.BankResponse;
import intellisysla.com.vanheusenshop.entities.client.Client;
import intellisysla.com.vanheusenshop.entities.client.DocumentListResponse;
import intellisysla.com.vanheusenshop.entities.delivery.Transport;
import intellisysla.com.vanheusenshop.entities.product.ProductMatrixView;
import intellisysla.com.vanheusenshop.entities.product.ProductSize;
import intellisysla.com.vanheusenshop.entities.product.ProductVariant;
import intellisysla.com.vanheusenshop.utils.MsgUtils;
import intellisysla.com.vanheusenshop.ux.fragments.BannersFragment;
import intellisysla.com.vanheusenshop.ux.fragments.ProductColorFragment;
import intellisysla.com.vanheusenshop.ux.fragments.ProductMatrixFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PaymentMainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PaymentMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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
    protected TextView CashText, TransferText, CheckText, TotalText, TotalInvoiceText;
    private double cash = 0, transfer = 0, check = 0, total = 0, totalInvoice = 0;
    private Client client;
    private ArrayList<Bank> banks;

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
        this.CashText.setText(String.valueOf(cash));
        UpdateTotal();
    }

    public void UpdateTransfer(Double transfer)
    {
        this.transfer = transfer;
        this.TransferText.setText(String.valueOf(transfer));
        UpdateTotal();
    }

    public void UpdateCheck(Double check)
    {
        this.check = check;
        this.CheckText.setText(String.valueOf(check));
        UpdateTotal();
    }

    public void AddInvoice(String invoice)
    {
        if(!invoice.isEmpty()){
            double total = Double.parseDouble(invoice);
            this.totalInvoice += total;
            this.TotalInvoiceText.setText(String.valueOf(this.totalInvoice));
            UpdateTotal();
        }
    }

    public void RestInvoice(String invoice)
    {
        if(!invoice.isEmpty()){
            double total = Double.parseDouble(invoice);
            this.totalInvoice -= total;
            this.TotalInvoiceText.setText(String.valueOf(this.totalInvoice));
            UpdateTotal();
        }
    }

    public void UpdateTotal()
    {
        double cash = 0, transfer = 0, check = 0, totalPaid = 0, totalInvoices = 0;
        String cashString = this.CashText.getText().toString();
        String transferString = this.TransferText.getText().toString();
        String checkString =  this.CheckText.getText().toString();

        if(!cashString.isEmpty())
            cash = Double.parseDouble(cashString);
        if(!transferString.isEmpty())
            transfer = Double.parseDouble(transferString);
        if(!checkString.isEmpty())
            check = Double.parseDouble(checkString);

        totalPaid = cash + transfer + check;

        this.TotalText.setText(String.valueOf(totalPaid));
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

        CashText = (TextView) view.findViewById(R.id.payment_main_cash);
        TransferText = (TextView) view.findViewById(R.id.payment_main_transfer);
        CheckText = (TextView) view.findViewById(R.id.payment_main_check);
        TotalText = (TextView) view.findViewById(R.id.payment_main_total);
        TotalInvoiceText = (TextView) view.findViewById(R.id.payment_main_paid_total);

        mSectionsPagerAdapter = new PaymentMainFragment.SectionsPagerAdapter(getFragmentManager());

        mViewPager = (ViewPager) view.findViewById(R.id.payment_view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

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
