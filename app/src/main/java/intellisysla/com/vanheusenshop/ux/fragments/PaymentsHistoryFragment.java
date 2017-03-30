package intellisysla.com.vanheusenshop.ux.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import intellisysla.com.vanheusenshop.entities.order.Order;
import intellisysla.com.vanheusenshop.entities.order.OrderResponse;
import intellisysla.com.vanheusenshop.entities.payment.Payment;
import intellisysla.com.vanheusenshop.entities.payment.PaymentResponse;
import intellisysla.com.vanheusenshop.interfaces.OrdersRecyclerInterface;
import intellisysla.com.vanheusenshop.interfaces.PaymentsRecyclerInterface;
import intellisysla.com.vanheusenshop.utils.EndlessRecyclerScrollListener;
import intellisysla.com.vanheusenshop.utils.MsgUtils;
import intellisysla.com.vanheusenshop.utils.RecyclerMarginDecorator;
import intellisysla.com.vanheusenshop.utils.Utils;
import intellisysla.com.vanheusenshop.ux.MainActivity;
import intellisysla.com.vanheusenshop.ux.adapters.OrdersHistoryRecyclerAdapter;
import intellisysla.com.vanheusenshop.ux.adapters.PaymentsHistoryRecyclerAdapter;
import intellisysla.com.vanheusenshop.ux.dialogs.LoginExpiredDialogFragment;
import timber.log.Timber;

/**
 * Fragment shows the user's order history.
 */
public class PaymentsHistoryFragment extends Fragment {

    private ProgressDialog progressDialog;

    // Fields referencing complex screen layouts.
    private View empty;
    private View content;
    private EditText beginEdit, endEdit;
    private Calendar myCalendar = Calendar.getInstance();

    /**
     * Request metadata containing urls for endlessScroll.
     */
    private Metadata paymentsMetadata;

    private PaymentsHistoryRecyclerAdapter paymentsHistoryRecyclerAdapter;
    private EndlessRecyclerScrollListener endlessRecyclerScrollListener;

    /**
     * Field for recovering scroll position.
     */
    private RecyclerView paymentsRecycler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        MainActivity.setActionBarTitle(getString(R.string.PaymentHistory));

        View view = inflater.inflate(R.layout.fragment_payment_history, container, false);

        progressDialog = Utils.generateProgressDialog(getActivity(), false);

        empty = view.findViewById(R.id.payment_history_empty);
        content = view.findViewById(R.id.payment_history_content);
        beginEdit = (EditText) view.findViewById(R.id.payment_history_begin);
        endEdit = (EditText) view.findViewById(R.id.payment_history_end);

        myCalendar = Calendar.getInstance();

        beginEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(),
                        R.style.MyDatePicker,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year,int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                String myFormat = "yyyy/MM/dd";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                beginEdit.setText(sdf.format(myCalendar.getTime()));
                            }
                        },
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH)-1,
                        myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        endEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(),
                        R.style.MyDatePicker,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                String myFormat = "yyyy/MM/dd";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                endEdit.setText(sdf.format(myCalendar.getTime()));
                            }
                        },
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH)+2,
                        myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        preparePaymentsHistoryRecycler(view);

        loadPayments(null);
        return view;
    }

    /**
     * Prepare content recycler. Create custom adapter and endless scroll.
     *
     * @param view root fragment view.
     */
    private void preparePaymentsHistoryRecycler(View view) {
        paymentsRecycler = (RecyclerView) view.findViewById(R.id.payments_history_recycler);
        paymentsHistoryRecyclerAdapter = new PaymentsHistoryRecyclerAdapter(new PaymentsRecyclerInterface() {
            @Override
            public void onPaymentSelected(View v, Payment payment) {
                Activity activity = getActivity();
                if (activity instanceof MainActivity) ((MainActivity) activity).onPaymentSelected(payment);
            }
        });
        paymentsRecycler.setAdapter(paymentsHistoryRecyclerAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(paymentsRecycler.getContext());
        paymentsRecycler.setLayoutManager(layoutManager);
        paymentsRecycler.setItemAnimator(new DefaultItemAnimator());
        paymentsRecycler.setHasFixedSize(true);
        paymentsRecycler.addItemDecoration(new RecyclerMarginDecorator(getResources().getDimensionPixelSize(R.dimen.base_margin)));

        endlessRecyclerScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                if (paymentsMetadata != null && paymentsMetadata.getLinks() != null && paymentsMetadata.getLinks().getNext() != null) {
                    loadPayments(paymentsMetadata.getLinks().getNext());
                } else {
                    Timber.d("CustomLoadMoreDataFromApi NO MORE DATA");
                }
            }
        };
        paymentsRecycler.addOnScrollListener(endlessRecyclerScrollListener);
    }

    /**
     * Endless content loader. Should be used after views inflated.
     *
     * @param url null for fresh load. Otherwise use URLs from response metadata.
     */
    private void loadPayments(String url) {
        User user = SettingsMy.getActiveUser();
        if (user != null) {
            progressDialog.show();
            if (url == null) {
                paymentsHistoryRecyclerAdapter.clear();
                url = String.format(EndPoints.PAYMENTS, user.getId(),"2016/11/11","2018/11/11");
            }
            GsonRequest<PaymentResponse> req = new GsonRequest<>(Request.Method.GET, url, null, PaymentResponse.class, new Response.Listener<PaymentResponse>() {
                @Override
                public void onResponse(PaymentResponse response) {
                    paymentsMetadata = response.getMetadata();
                    paymentsHistoryRecyclerAdapter.addPayments(response.getPayments());

                    if (paymentsHistoryRecyclerAdapter.getItemCount() > 0) {
                        empty.setVisibility(View.GONE);
                        content.setVisibility(View.VISIBLE);
                    } else {
                        empty.setVisibility(View.VISIBLE);
                        content.setVisibility(View.GONE);
                    }
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
            MyApplication.getInstance().addToRequestQueue(req, CONST.PAYMENTS_HISTORY_REQUESTS_TAG);
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
        if (paymentsRecycler != null) paymentsRecycler.clearOnScrollListeners();
        super.onDestroyView();
    }
}
