package grintsys.com.vanshop.ux.dialogs;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import grintsys.com.vanshop.R;
import grintsys.com.vanshop.ux.MainActivity;
import timber.log.Timber;


/**
 * Dialog display "Thank you" screen after order is finished.
 */
public class OrderCreateSuccessDialogFragment extends DialogFragment {

    private boolean sampleApplication = false;

    /**
     * Dialog display "Thank you" screen after order is finished.
     */
    public static OrderCreateSuccessDialogFragment newInstance(boolean sampleApplication) {
        OrderCreateSuccessDialogFragment orderCreateSuccessDialogFragment = new OrderCreateSuccessDialogFragment();
        orderCreateSuccessDialogFragment.sampleApplication = sampleApplication;
        return orderCreateSuccessDialogFragment;
    }

    public static OrderCreateSuccessDialogFragment newInstance(boolean sampleApplication, int orderId) {
        OrderCreateSuccessDialogFragment orderCreateSuccessDialogFragment = new OrderCreateSuccessDialogFragment();
        orderCreateSuccessDialogFragment.sampleApplication = sampleApplication;
        return orderCreateSuccessDialogFragment;


/*
        Bundle args = new Bundle();
        args.putString(CLIENT_PARAM, searchQuery);

        ClientsFragment fragment = new ClientsFragment();
        fragment.setArguments(args);
        return fragment;
        */
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.dialogFragmentAnimation);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.dialog_order_create_success, container, false);

        Button okBtn = (Button) view.findViewById(R.id.order_create_success_continue);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity)
                    ((MainActivity) getActivity()).onDrawerBannersSelected();
                dismiss();
            }
        });

        Button printBtn = (Button) view.findViewById(R.id.order_create_success_print);
        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                //String url = String.format(EndPoints.ORDERS_SINGLE, SettingsMy.getActualNonNullShop(getActivity()).getId(), orderId);
                String url = String.format(EndPoints.ORDERS_SINGLE, orderId);

                GsonRequest<Order> req = new GsonRequest<>(Request.Method.GET, url, null, Order.class, new Response.Listener<Order>() {
                    @Override
                    public void onResponse(Order response) {
                        //
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        MsgUtils.logAndShowErrorMessage(getActivity(), error);
                    }
                }, getFragmentManager(), null);
                */
            }
        });

        TextView title = (TextView) view.findViewById(R.id.order_create_success_title);
        TextView description = (TextView) view.findViewById(R.id.order_create_success_description);

        if (sampleApplication) {
            title.setText(R.string.This_is_a_sample_app);
            description.setTextColor(ContextCompat.getColor(getContext(), R.color.textSecondary));
            description.setText(R.string.Sample_app_description);
        } else {
            title.setText(R.string.Thank_you_for_your_order);
            description.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            description.setText(Html.fromHtml(getString(R.string.Wait_for_sms_or_email_order_confirmation)));
        }

        return view;
    }
}