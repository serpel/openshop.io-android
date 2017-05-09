package intellisysla.com.vanheusenshop.ux.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import intellisysla.com.vanheusenshop.CONST;
import intellisysla.com.vanheusenshop.MyApplication;
import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.SettingsMy;
import intellisysla.com.vanheusenshop.api.EndPoints;
import intellisysla.com.vanheusenshop.api.GsonRequest;
import intellisysla.com.vanheusenshop.entities.Metadata;
import intellisysla.com.vanheusenshop.entities.User.User;
import intellisysla.com.vanheusenshop.entities.client.ClientTransactions;
import intellisysla.com.vanheusenshop.entities.client.ClientTransactionsResponse;
import intellisysla.com.vanheusenshop.entities.payment.Payment;
import intellisysla.com.vanheusenshop.entities.payment.PaymentResponse;
import intellisysla.com.vanheusenshop.interfaces.ClientTransactionsRecyclerInterface;
import intellisysla.com.vanheusenshop.interfaces.PaymentsRecyclerInterface;
import intellisysla.com.vanheusenshop.listeners.OnSingleClickListener;
import intellisysla.com.vanheusenshop.utils.EndlessRecyclerScrollListener;
import intellisysla.com.vanheusenshop.utils.MsgUtils;
import intellisysla.com.vanheusenshop.utils.RecyclerMarginDecorator;
import intellisysla.com.vanheusenshop.utils.Utils;
import intellisysla.com.vanheusenshop.ux.MainActivity;
import intellisysla.com.vanheusenshop.ux.adapters.ClientTransactionsRecyclerAdapter;
import intellisysla.com.vanheusenshop.ux.adapters.PaymentsHistoryRecyclerAdapter;
import intellisysla.com.vanheusenshop.ux.dialogs.LoginExpiredDialogFragment;
import timber.log.Timber;

import static intellisysla.com.vanheusenshop.SettingsMy.PREF_CLIENT_CARD_CODE_SELECTED;
import static intellisysla.com.vanheusenshop.SettingsMy.getSettings;

/**
 * Fragment shows the user's order history.
 */
public class ClientTransactionsFragment extends Fragment {

    private ProgressDialog progressDialog;

    // Fields referencing complex screen layouts.
    private View empty;
    private View content;
    private EditText beginEdit, endEdit;
    private Button searchButton;
    private Calendar beginCalendar, endCalendar;

    /**
     * Request metadata containing urls for endlessScroll.
     */
    private Metadata paymentsMetadata;

    private ClientTransactionsRecyclerAdapter transactionsRecyclerAdapter;
    private EndlessRecyclerScrollListener endlessRecyclerScrollListener;

    /**
     * Field for recovering scroll position.
     */
    private RecyclerView transactionsRecycler;

    public static ClientTransactionsFragment newInstance() {
        Bundle args = new Bundle();
        ClientTransactionsFragment fragment = new ClientTransactionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        MainActivity.setActionBarTitle(getString(R.string.ClientTransactions));

        View view = inflater.inflate(R.layout.fragment_client_transactions, container, false);

        progressDialog = Utils.generateProgressDialog(getActivity(), false);

        empty = view.findViewById(R.id.client_transaction_empty);
        content = view.findViewById(R.id.client_transaction_content);
        beginEdit = (EditText) view.findViewById(R.id.client_transaction_begin);
        endEdit = (EditText) view.findViewById(R.id.client_transaction_end);
        searchButton = (Button) view.findViewById(R.id.client_transaction_ok_button);

        String myFormat = "yyyy/MM/dd";
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        endCalendar = Calendar.getInstance();
        beginCalendar = Calendar.getInstance();
        beginCalendar.add(Calendar.DAY_OF_MONTH, -2);
        beginEdit.setText(sdf.format(endCalendar.getTime()));

        beginEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(),
                        R.style.MyDatePicker,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year,int monthOfYear, int dayOfMonth) {
                                beginCalendar.set(Calendar.YEAR, year);
                                beginCalendar.set(Calendar.MONTH, monthOfYear);
                                beginCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                beginEdit.setText(sdf.format(beginCalendar.getTime()));
                            }
                        },
                        beginCalendar.get(Calendar.YEAR),
                        beginCalendar.get(Calendar.MONTH),
                        beginCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        endCalendar.add(Calendar.DAY_OF_MONTH, 15);
        endEdit.setText(sdf.format(endCalendar.getTime()));

        endEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(),
                        R.style.MyDatePicker,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                endCalendar.set(Calendar.YEAR, year);
                                endCalendar.set(Calendar.MONTH, monthOfYear);
                                endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                endEdit.setText(sdf.format(endCalendar.getTime()));
                            }
                        },
                        endCalendar.get(Calendar.YEAR),
                        endCalendar.get(Calendar.MONTH),
                        endCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        searchButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                loadTransactions(null);
            }
        });

        preparePaymentsHistoryRecycler(view);

        loadTransactions(null);
        return view;
    }

    /**
     * Prepare content recycler. Create custom adapter and endless scroll.
     *
     * @param view root fragment view.
     */
    private void preparePaymentsHistoryRecycler(View view) {
        transactionsRecycler = (RecyclerView) view.findViewById(R.id.client_transaction_history_recycler);
        //fix here
        transactionsRecyclerAdapter = new ClientTransactionsRecyclerAdapter(null);
        transactionsRecycler.setAdapter(transactionsRecyclerAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(transactionsRecycler.getContext());
        transactionsRecycler.setLayoutManager(layoutManager);
        transactionsRecycler.setItemAnimator(new DefaultItemAnimator());
        transactionsRecycler.setHasFixedSize(true);
        transactionsRecycler.addItemDecoration(new RecyclerMarginDecorator(getResources().getDimensionPixelSize(R.dimen.base_margin)));

        endlessRecyclerScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                /*if (paymentsMetadata != null && paymentsMetadata.getLinks() != null && paymentsMetadata.getLinks().getNext() != null) {
                    loadPayments(paymentsMetadata.getLinks().getNext());
                } else {
                    Timber.d("CustomLoadMoreDataFromApi NO MORE DATA");
                }*/
            }
        };
        transactionsRecycler.addOnScrollListener(endlessRecyclerScrollListener);
    }

    /**
     * Endless content loader. Should be used after views inflated.
     *
     * @param url null for fresh load. Otherwise use URLs from response metadata.
     */
    private void loadTransactions(String url) {
        User user = SettingsMy.getActiveUser();
        if (user != null) {
            progressDialog.show();
            SharedPreferences prefs = getSettings();
            String card_code = prefs.getString(PREF_CLIENT_CARD_CODE_SELECTED, "");
            if (url == null) {
                url = String.format(EndPoints.CLIENT_TRANSACTIONS, card_code, beginEdit.getText().toString(),endEdit.getText().toString());
            }
            transactionsRecyclerAdapter.clear();
            GsonRequest<ClientTransactionsResponse> req = new GsonRequest<>(Request.Method.GET, url, null, ClientTransactionsResponse.class, new Response.Listener<ClientTransactionsResponse>() {
                @Override
                public void onResponse(ClientTransactionsResponse response) {
                    //paymentsMetadata = response.getMetadata();
                    transactionsRecyclerAdapter.addPayments(response.getTransactions());

                    if (progressDialog != null) progressDialog.cancel();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog != null) progressDialog.cancel();
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);
                }
            }, getFragmentManager(), user.getAccessToken());

            req.setRetryPolicy(MyApplication.getSimpleRetryPolice());
            req.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(req, CONST.CLIENT_TRANSACTIONS_REQUESTS_TAG);
        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), "loginExpiredDialogFragment");
        }
    }

    @Override
    public void onStop() {
        if (progressDialog != null) {
            // Hide progress dialog if exist.
            if (progressDialog.isShowing() && endlessRecyclerScrollListener != null) {
                // Fragment stopped during loading data. Allow new loading on return.
                endlessRecyclerScrollListener.resetLoading();
            }
            progressDialog.cancel();
        }
        MyApplication.getInstance().cancelPendingRequests(CONST.PAYMENTS_HISTORY_REQUESTS_TAG);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (transactionsRecycler != null) transactionsRecycler.clearOnScrollListeners();
        super.onDestroyView();
    }
}
