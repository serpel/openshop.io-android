package intellisysla.com.vanheusenshop.ux.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import intellisysla.com.vanheusenshop.CONST;
import intellisysla.com.vanheusenshop.MyApplication;
import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.SettingsMy;
import intellisysla.com.vanheusenshop.api.EndPoints;
import intellisysla.com.vanheusenshop.api.GsonRequest;
import intellisysla.com.vanheusenshop.entities.User.User;
import intellisysla.com.vanheusenshop.entities.order.Order;
import intellisysla.com.vanheusenshop.utils.JsonUtils;
import intellisysla.com.vanheusenshop.utils.MsgUtils;
import intellisysla.com.vanheusenshop.utils.Utils;
import intellisysla.com.vanheusenshop.ux.MainActivity;
import intellisysla.com.vanheusenshop.ux.dialogs.LoginExpiredDialogFragment;
import intellisysla.com.vanheusenshop.ux.dialogs.OrderCreateSuccessDialogFragment;
import intellisysla.com.vanheusenshop.views.ResizableImageView;
import timber.log.Timber;

import static intellisysla.com.vanheusenshop.SettingsMy.PREF_CLIENT_CARD_CODE_SELECTED;
import static intellisysla.com.vanheusenshop.SettingsMy.getSettings;

/**
 * Adapter handling list of order items.
 */
public class OrderRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM_ORDER = 1;

    private LayoutInflater layoutInflater;
    private Context context;
    private Order order;
    private static long orderId;

    /**
     * Creates an adapter that handles a list of order items.
     *
     * @param context activity context.
     */
    public OrderRecyclerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_ITEM_ORDER) {
            View view = layoutInflater.inflate(R.layout.list_item_order_product_image, parent, false);
            return new ViewHolderOrderProduct(view);
        } else {
            View view = layoutInflater.inflate(R.layout.list_item_order_header, parent, false);
            return new ViewHolderHeader(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderOrderProduct) {
            ViewHolderOrderProduct viewHolderOrderProduct = (ViewHolderOrderProduct) holder;

            Picasso.with(context).load(order.getProducts().get(position - 1).getVariant().getMainImage())
                    .fit().centerInside()
                    .placeholder(R.drawable.placeholder_loading)
                    .error(R.drawable.placeholder_error)
                    .into(viewHolderOrderProduct.productImage);

        } else if (holder instanceof ViewHolderHeader) {
            ViewHolderHeader viewHolderHeader = (ViewHolderHeader) holder;

            viewHolderHeader.orderId.setText(String.valueOf(order.getId()));
            viewHolderHeader.orderDateCreated.setText(order.getDateCreated());
            viewHolderHeader.orderTotal.setText(order.getTotalFormatted());
            viewHolderHeader.orderName.setText(order.getComment());
            viewHolderHeader.orderStatus.setText(order.getStatus());
            orderId = order.getId();

            if(order.getRemoteId() != null){
                viewHolderHeader.orderResend.setVisibility(View.GONE);
            }else{
                viewHolderHeader.orderResend.setVisibility(View.VISIBLE);
            }

        } else {
            Timber.e(new RuntimeException(), "Unknown holder type.");
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        if (order != null) {
            if (order.getProducts() != null && order.getProducts().size() > 0) {
                return order.getProducts().size() + 1; // the number of items in the list, +1 for header view.
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else
            return TYPE_ITEM_ORDER;
    }


    /**
     * Add item to list, and notify dataSet changed.
     *
     * @param order item to add.
     */
    public void addOrder(Order order) {
        if (order != null) {
            this.order = order;
            notifyDataSetChanged();
        } else {
            Timber.e("Setting null order object.");
        }
    }

    // Provide a reference to the views for each data item
    public static class ViewHolderOrderProduct extends RecyclerView.ViewHolder {
        ResizableImageView productImage;

        public ViewHolderOrderProduct(View itemView) {
            super(itemView);
            productImage = (ResizableImageView) itemView.findViewById(R.id.list_item_product_images_view);
        }
    }

    public static class ViewHolderHeader extends RecyclerView.ViewHolder {

        public TextView orderId;
        public TextView orderName;
        public TextView orderDateCreated;
        public TextView orderTotal;
        public TextView orderStatus;
        public Button orderResend;

        public ViewHolderHeader(View headerView) {
            super(headerView);
            orderId = (TextView) headerView.findViewById(R.id.list_item_order_header_id);
            orderName = (TextView) headerView.findViewById(R.id.list_item_order_header_name);
            orderDateCreated = (TextView) headerView.findViewById(R.id.list_item_order_header_dateCreated);
            orderTotal = (TextView) headerView.findViewById(R.id.list_item_order_header_total);
            orderStatus = (TextView) headerView.findViewById(R.id.list_item_order_status);
            orderResend = (Button) headerView.findViewById(R.id.order_resend);

            orderResend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //MsgUtils.showToast(this.context, MsgUtils.TOAST_TYPE_MESSAGE, "Se ha reenviado pedido", MsgUtils.ToastLength.LONG);
                }
            });
        }
    }
/*
    private void postOrder(final long orderId) {

        final User user = SettingsMy.getActiveUser();
        if (user != null) {
            String url = String.format(EndPoints.ORDERS_RECREATE, order.getId());
            postOrderRequest = new GsonRequest<>(Request.Method.GET, url, null, Order.class, new Response.Listener<Order>() {
                @Override
                public void onResponse(Order order) {
                    Timber.d("response: %s", order.toString());
                    progressDialog.cancel();
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
        }
    }
*/
}