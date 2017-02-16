package intellisysla.com.vanheusenshop.ux.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import intellisysla.com.vanheusenshop.CONST;
import intellisysla.com.vanheusenshop.MyApplication;
import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.SettingsMy;
import intellisysla.com.vanheusenshop.api.EndPoints;
import intellisysla.com.vanheusenshop.api.GsonRequest;
import intellisysla.com.vanheusenshop.entities.User;
import intellisysla.com.vanheusenshop.entities.cart.Cart;
import intellisysla.com.vanheusenshop.entities.cart.CartProductItem;
import intellisysla.com.vanheusenshop.entities.delivery.Delivery;
import intellisysla.com.vanheusenshop.entities.delivery.DeliveryRequest;
import intellisysla.com.vanheusenshop.entities.delivery.Payment;
import intellisysla.com.vanheusenshop.entities.delivery.Shipping;
import intellisysla.com.vanheusenshop.entities.order.Order;
import intellisysla.com.vanheusenshop.listeners.OnSingleClickListener;
import intellisysla.com.vanheusenshop.utils.JsonUtils;
import intellisysla.com.vanheusenshop.utils.MsgUtils;
import intellisysla.com.vanheusenshop.utils.Utils;
import intellisysla.com.vanheusenshop.ux.MainActivity;
import intellisysla.com.vanheusenshop.ux.dialogs.LoginExpiredDialogFragment;
import intellisysla.com.vanheusenshop.ux.dialogs.OrderCreateSuccessDialogFragment;
import timber.log.Timber;

import static intellisysla.com.vanheusenshop.SettingsMy.PREF_CLIENT_CARD_CODE_SELECTED;
import static intellisysla.com.vanheusenshop.SettingsMy.getSettings;

/**
 * Fragment allowing the user to create order.
 */
public class OrderCreateFragment extends Fragment {

    public static final String MSG_LOGIN_EXPIRED_DIALOG_FRAGMENT = "loginExpiredDialogFragment";
    private ProgressDialog progressDialog;

    private ScrollView scrollLayout;
    private LinearLayout cartItemsLayout;

    private Cart cart;
    private double orderTotalPrice;
    private TextView cartItemsTotalPrice;
    private TextView orderTotalPriceTv;
    private EditText commentEditText;


    // View with user information used to create order
    private TextInputLayout nameInputWrapper;
    private TextInputLayout streetInputWrapper;
    private TextInputLayout houseNumberInputWrapper;
    private TextInputLayout cityInputWrapper;
    private TextInputLayout zipInputWrapper;
    private TextInputLayout phoneInputWrapper;
    private TextInputLayout emailInputWrapper;
    private TextInputLayout noteInputWrapper;

    // Shipping and payment
    private Delivery delivery;
    private Payment selectedPayment;
    private Shipping selectedShipping;
    private ProgressBar deliveryProgressBar;
    private View deliveryShippingLayout;
    private View deliveryPaymentLayout;
    private TextView selectedShippingNameTv;
    private TextView selectedShippingPriceTv;
    private TextView selectedPaymentNameTv;
    private TextView selectedPaymentPriceTv;
    private GsonRequest<Order> postOrderRequest;

    private TextView subtotalTextView, totalTextView, discountTextView, isvTexView;

    private Spinner sellerSpinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        MainActivity.setActionBarTitle(getString(R.string.Order_summary));

        View view = inflater.inflate(R.layout.fragment_order_create, container, false);

        progressDialog = Utils.generateProgressDialog(getActivity(), false);

        scrollLayout = (ScrollView) view.findViewById(R.id.order_create_scroll_layout);
        cartItemsLayout = (LinearLayout) view.findViewById(R.id.order_create_cart_items_layout);
        commentEditText = (EditText) view.findViewById(R.id.order_comment);
        subtotalTextView = (TextView) view.findViewById(R.id.order_create_subtotal);
        totalTextView = (TextView) view.findViewById(R.id.order_create_total_price);
        discountTextView = (TextView) view.findViewById(R.id.order_create_discount);
        isvTexView = (TextView) view.findViewById(R.id.order_create_isv);

        orderTotalPriceTv = (TextView) view.findViewById(R.id.order_create_summary_total_price);
        TextView termsAndConditionsTv = (TextView) view.findViewById(R.id.order_create_summary_terms_and_condition);
        termsAndConditionsTv.setText(Html.fromHtml(getString(R.string.Click_on_Order_to_allow_our_Terms_and_Conditions)));
        termsAndConditionsTv.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (getActivity() instanceof MainActivity)
                    ((MainActivity) getActivity()).onTermsAndConditionsSelected();
            }
        });

        sellerSpinner = (Spinner) view.findViewById(R.id.order_seller_spinner);
        prepareSellerSpinner(view);

        prepareFields(view);
        prepareDeliveryLayout(view);

        Button finishOrder = (Button) view.findViewById(R.id.order_create_finish);
        finishOrder.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                //TODO: Fix Seller Selection
                Order order = new Order();
                order.setComment(commentEditText.getText().toString());
                v.clearFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                postOrder(order);

               /*if (isRequiredFieldsOk()) {
                    // Prepare data
                    Order order = new Order();
                    //order.setPhone(Utils.getTextFromInputLayout(phoneInputWrapper));
                    //order.setNote(Utils.getTextFromInputLayout(noteInputWrapper));

                    // Hide keyboard
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    postOrder(order);
                }*/
            }
        });

        //showSelectedShipping(selectedShipping);
        //showSelectedPayment(selectedPayment);
        getUserCart();
        return view;
    }

    //TODO: ARREGLAR CHANCHADA
    private void prepareSellerSpinner(View view){
        Spinner sellerSpinner = (Spinner) view.findViewById(R.id.order_seller_spinner);
        String sellers[] = {"Sergio Peralta", "Vendedor Institucional"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, sellers);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sellerSpinner.setAdapter(spinnerArrayAdapter);
    }


    /**
     * Prepare content views, adapters and listeners.
     *
     * @param view fragment base view.
     */
    private void prepareFields(View view) {
        /*nameInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_name_wrapper);
        streetInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_street_wrapper);
        houseNumberInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_houseNumber_wrapper);
        cityInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_city_wrapper);
        zipInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_zip_wrapper);
        phoneInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_phone_wrapper);
        emailInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_email_wrapper);
        noteInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_note_wrapper);

        User user = SettingsMy.getActiveUser();
        if (user != null) {
            Utils.setTextToInputLayout(nameInputWrapper, user.getName());
            Utils.setTextToInputLayout(streetInputWrapper, user.getStreet());
            Utils.setTextToInputLayout(houseNumberInputWrapper, user.getHouseNumber());
            Utils.setTextToInputLayout(cityInputWrapper, user.getCity());
            Utils.setTextToInputLayout(zipInputWrapper, user.getZip());
            Utils.setTextToInputLayout(emailInputWrapper, user.getEmail());
            Utils.setTextToInputLayout(phoneInputWrapper, user.getPhone());
        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), MSG_LOGIN_EXPIRED_DIALOG_FRAGMENT);
        }*/
    }

    /**
     * Check if all input fields are filled and also that is selected shipping and payment.
     * Method highlights all unfilled input fields.
     *
     * @return true if everything is Ok.
     */
    private boolean isRequiredFieldsOk() {
        // Check and show all missing values
        String fieldRequired = getString(R.string.Required_field);
        boolean nameCheck = Utils.checkTextInputLayoutValueRequirement(nameInputWrapper, fieldRequired);
        boolean streetCheck = Utils.checkTextInputLayoutValueRequirement(streetInputWrapper, fieldRequired);
        boolean houseNumberCheck = Utils.checkTextInputLayoutValueRequirement(houseNumberInputWrapper, fieldRequired);
        boolean cityCheck = Utils.checkTextInputLayoutValueRequirement(cityInputWrapper, fieldRequired);
        boolean zipCheck = Utils.checkTextInputLayoutValueRequirement(zipInputWrapper, fieldRequired);
        boolean phoneCheck = Utils.checkTextInputLayoutValueRequirement(phoneInputWrapper, fieldRequired);
        boolean emailCheck = Utils.checkTextInputLayoutValueRequirement(emailInputWrapper, fieldRequired);

        if (nameCheck && streetCheck && houseNumberCheck && cityCheck && zipCheck && phoneCheck && emailCheck) {
            // Check if shipping and payment is selected
            if (selectedShipping == null) {
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Choose_shipping_method), MsgUtils.ToastLength.SHORT);
                scrollLayout.smoothScrollTo(0, deliveryShippingLayout.getTop());
                return false;
            }

            if (selectedPayment == null) {
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Choose_payment_method), MsgUtils.ToastLength.SHORT);
                scrollLayout.smoothScrollTo(0, deliveryShippingLayout.getTop());
                return false;
            }
            return true;
        } else {
            return false;
        }
    }


    private void prepareDeliveryLayout(View view) {
        deliveryProgressBar = (ProgressBar) view.findViewById(R.id.delivery_progress);

//        final View deliveryShippingBtn = view.findViewById(R.id.order_create_delivery_shipping_button);
//        final View deliveryPaymentBtn = view.findViewById(R.id.order_create_delivery_payment_button);

        this.deliveryShippingLayout = view.findViewById(R.id.order_create_delivery_shipping_layout);
        //this.deliveryPaymentLayout = view.findViewById(R.id.order_create_delivery_payment_layout);

        //selectedShippingNameTv = (TextView) view.findViewById(R.id.order_create_delivery_shipping_name);
        //selectedShippingPriceTv = (TextView) view.findViewById(R.id.order_create_delivery_shipping_price);
        //selectedPaymentNameTv = (TextView) view.findViewById(R.id.order_create_delivery_payment_name);
        //selectedPaymentPriceTv = (TextView) view.findViewById(R.id.order_create_delivery_payment_price);

        /*deliveryShippingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShippingDialogFragment shippingDialogFragment = ShippingDialogFragment.newInstance(delivery, selectedShipping, new ShippingDialogInterface() {
                    @Override
                    public void onShippingSelected(Shipping shipping) {
                        // Save selected value
                        selectedShipping = shipping;

                        // Update shipping related values
                        showSelectedShipping(shipping);

                        // Continue for payment
                        selectedPayment = null;
                        selectedPaymentNameTv.setText(getString(R.string.Choose_payment_method));
                        selectedPaymentPriceTv.setText("");
                        deliveryPaymentLayout.performClick();
                    }
                });
                shippingDialogFragment.show(getFragmentManager(), ShippingDialogFragment.class.getSimpleName());
            }
        });*/

        /*deliveryPaymentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentDialogFragment paymentDialogFragment = PaymentDialogFragment.newInstance(selectedShipping, selectedPayment, new PaymentDialogInterface() {
                    @Override
                    public void onPaymentSelected(Payment payment) {
                        selectedPayment = payment;
                        showSelectedPayment(payment);
                    }
                });
                paymentDialogFragment.show(getFragmentManager(), "PaymentDialog");
            }
        });*/
    }

    /**
     * Show and update shipping related values.
     *
     * @param shipping values to show.
     */
    private void showSelectedShipping(Shipping shipping) {
        if (shipping != null && selectedShippingNameTv != null && selectedShippingPriceTv != null) {
            selectedShippingNameTv.setText(shipping.getName());
            if (shipping.getPrice() != 0) {
                selectedShippingPriceTv.setText(shipping.getPriceFormatted());
            } else {
                selectedShippingPriceTv.setText(getText(R.string.free));
            }

            // Set total order price
            orderTotalPrice = shipping.getTotalPrice();
            orderTotalPriceTv.setText(shipping.getTotalPriceFormatted());
            deliveryPaymentLayout.setVisibility(View.VISIBLE);
        } else {
            Timber.e("Showing selected shipping with null values.");
        }
    }


    /**
     * Show and update payment related values.
     *
     * @param payment values to show.
     */
    private void showSelectedPayment(Payment payment) {
        if (payment != null && selectedPaymentNameTv != null && selectedPaymentPriceTv != null) {
            selectedPaymentNameTv.setText(payment.getName());
            if (payment.getPrice() != 0) {
                selectedPaymentPriceTv.setText(payment.getPriceFormatted());
            } else {
                selectedPaymentPriceTv.setText(getText(R.string.free));
            }

            // Set total order price
            orderTotalPrice = payment.getTotalPrice();
            orderTotalPriceTv.setText(payment.getTotalPriceFormatted());
        } else {
            Timber.e("Showing selected payment with null values.");
        }
    }

    private void getUserCart() {
        final User user = SettingsMy.getActiveUser();
        if (user != null) {
            //String url = String.format(EndPoints.CART, SettingsMy.getActualNonNullShop(getActivity()).getId());
            String url = String.format(EndPoints.CART, user.getId());

            progressDialog.show();
            GsonRequest<Cart> getCart = new GsonRequest<>(Request.Method.GET, url, null, Cart.class,
                    new Response.Listener<Cart>() {
                        @Override
                        public void onResponse(@NonNull Cart cart) {
                            if (progressDialog != null) progressDialog.cancel();
                            refreshScreenContent(cart, user);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog != null) progressDialog.cancel();
                    Timber.e("Get request cart error: %s", error.getMessage());
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);
                    if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).onDrawerBannersSelected();
                }
            }, getFragmentManager(), user.getAccessToken());
            getCart.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            getCart.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(getCart, CONST.ORDER_CREATE_REQUESTS_TAG);
        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), MSG_LOGIN_EXPIRED_DIALOG_FRAGMENT);
        }
    }

    private void refreshScreenContent(@NonNull Cart cart, User user) {
        this.cart = cart;
        List<CartProductItem> cartProductItems = cart.getItems();
        if (cartProductItems == null || cartProductItems.isEmpty()) {
            Timber.e(new RuntimeException(), "Received null cart during order creation.");
            if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).onDrawerBannersSelected();
        } else {

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < cartProductItems.size(); i++) {
                LinearLayout llRow = (LinearLayout) inflater.inflate(R.layout.order_create_cart_item, cartItemsLayout, false);
                TextView tvItemName = (TextView) llRow.findViewById(R.id.order_create_cart_item_name);
                tvItemName.setText(cartProductItems.get(i).getVariant().getName());
                TextView tvItemPrice = (TextView) llRow.findViewById(R.id.order_create_cart_item_price);
                tvItemPrice.setText(cartProductItems.get(i).getTotalItemPriceFormatted());
                TextView tvItemQuantity = (TextView) llRow.findViewById(R.id.order_create_cart_item_quantity);
                tvItemQuantity.setText(getString(R.string.format_quantity, cartProductItems.get(i).getQuantity()));
                TextView tvItemDetails = (TextView) llRow.findViewById(R.id.order_create_cart_item_details);
                tvItemDetails.setText(getString(R.string.format_string_division, cartProductItems.get(i).getVariant().getColor().getValue(),
                        cartProductItems.get(i).getVariant().getSize().getValue()));
                cartItemsLayout.addView(llRow);
            }
            if (cart.getDiscounts() != null) {
                for (int i = 0; i < cart.getDiscounts().size(); i++) {
                    LinearLayout llRow = (LinearLayout) inflater.inflate(R.layout.order_create_cart_item, cartItemsLayout, false);
                    TextView tvItemName = (TextView) llRow.findViewById(R.id.order_create_cart_item_name);
                    TextView tvItemPrice = (TextView) llRow.findViewById(R.id.order_create_cart_item_price);
                    tvItemName.setText(cart.getDiscounts().get(i).getDiscount().getName());
                    tvItemPrice.setText(cart.getDiscounts().get(i).getDiscount().getValueFormatted());
                    tvItemPrice.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    cartItemsLayout.addView(llRow);
                }
            }

            //TODO: verify this calculation
            double subtotal = cart.getTotalPrice();
            subtotalTextView.setText(String.format("%s %s", getString(R.string.SubTotal), subtotal));
            double discount = 0;
            discountTextView.setText(String.format("%s %s", getString(R.string.Discount), String.valueOf(discount)));
            double isv = (subtotal * 15)/100;
            isvTexView.setText(String.format("%s %s", getString(R.string.ISV), String.valueOf(isv)));
            double total = (subtotal + discount) +  isv;
            totalTextView.setText(String.format("%s %s", getString(R.string.Total_colon), total));

            // TODO pull to scroll could be cool here
            String url = String.format(EndPoints.CART_DELIVERY_INFO, SettingsMy.getActualNonNullShop(getActivity()).getId());

            deliveryProgressBar.setVisibility(View.VISIBLE);
            GsonRequest<DeliveryRequest> getDelivery = new GsonRequest<>(Request.Method.GET, url, null, DeliveryRequest.class,
                    new Response.Listener<DeliveryRequest>() {
                        @Override
                        public void onResponse(@NonNull DeliveryRequest deliveryResp) {
                            Timber.d("GetDelivery: %s", deliveryResp.toString());
                            delivery = deliveryResp.getDelivery();
                            deliveryProgressBar.setVisibility(View.GONE);
                            deliveryShippingLayout.setVisibility(View.VISIBLE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Timber.e("Get request cart error: %s", error.getMessage());
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);

                    deliveryProgressBar.setVisibility(View.GONE);
                    if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).onDrawerBannersSelected();
                }
            }, getFragmentManager(), user.getAccessToken());
            getDelivery.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            getDelivery.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(getDelivery, CONST.ORDER_CREATE_REQUESTS_TAG);
        }
    }

    private void postOrder(final Order order) {
        final User user = SettingsMy.getActiveUser();
        if (user != null) {

            //order.setSalesPersonCode(user.getSalesPersonId());

            SharedPreferences prefs = getSettings();
            String card_code = prefs.getString(PREF_CLIENT_CARD_CODE_SELECTED, "C0001");

            order.setSalesPersonCode(1);
            order.setSeries(71);
            order.setCardCode(card_code);

            JSONObject jo;
            try {
                jo = JsonUtils.createOrderJson(order);
            } catch (JSONException e) {
                Timber.e(e, "Post order Json exception.");
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                return;
            }

            Timber.d("Post order jo: %s", jo.toString());
            //String url = String.format(EndPoints.ORDERS, SettingsMy.getActualNonNullShop(getActivity()).getId());
            String url = String.format(EndPoints.ORDERS_CREATE, user.getId(), cart.getId(), jo.toString());

            progressDialog.show();
            postOrderRequest = new GsonRequest<>(Request.Method.GET, url, null, Order.class, new Response.Listener<Order>() {
                @Override
                public void onResponse(Order order) {
                    Timber.d("response: %s", order.toString());
                    progressDialog.cancel();

                    //Analytics.logOrderCreatedEvent(cart, order.getRemoteId(), orderTotalPrice, selectedShipping);

                    //updateUserData(user, order);
                    MainActivity.updateCartCountNotification();

                    DialogFragment thankYouDF = OrderCreateSuccessDialogFragment.newInstance(false);
                    thankYouDF.show(getFragmentManager(), OrderCreateSuccessDialogFragment.class.getSimpleName());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.cancel();
                    // Return 501 for sample application.
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);
                }
            }, getFragmentManager(), user.getAccessToken());
            postOrderRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            postOrderRequest.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(postOrderRequest, CONST.ORDER_CREATE_REQUESTS_TAG);
        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), MSG_LOGIN_EXPIRED_DIALOG_FRAGMENT);
        }
    }

    /**
     * Update user information after successful order.
     *
     * @param user  actual user which will be updated
     * @param order order response for obtain user information
     */
    private void updateUserData(User user, Order order) {
        if (user != null) {
            user.setEmail(order.getEmail());
            user.setPhone(order.getPhone());
            SettingsMy.setActiveUser(user);
        } else {
            Timber.e(new NullPointerException(), "Null user after successful order.");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getInstance().cancelPendingRequests(CONST.ORDER_CREATE_REQUESTS_TAG);
        if (progressDialog != null) progressDialog.cancel();
        if (deliveryProgressBar != null) deliveryProgressBar.setVisibility(View.GONE);
    }
}
