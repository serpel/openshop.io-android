package grintsys.com.vanshop.ux.fragments;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import grintsys.com.vanshop.BuildConfig;
import grintsys.com.vanshop.CONST;
import grintsys.com.vanshop.MyApplication;
import grintsys.com.vanshop.R;
import grintsys.com.vanshop.SettingsMy;
import grintsys.com.vanshop.api.EndPoints;
import grintsys.com.vanshop.api.GsonRequest;
import grintsys.com.vanshop.api.JsonRequest;
import grintsys.com.vanshop.entities.User.User;
import grintsys.com.vanshop.entities.product.Product;
import grintsys.com.vanshop.entities.product.ProductColor;
import grintsys.com.vanshop.entities.product.ProductMatrixView;
import grintsys.com.vanshop.entities.product.ProductSize;
import grintsys.com.vanshop.entities.product.ProductVariant;
import grintsys.com.vanshop.utils.Analytics;
import grintsys.com.vanshop.utils.MsgUtils;
import grintsys.com.vanshop.ux.MainActivity;
import timber.log.Timber;

import static grintsys.com.vanshop.SettingsMy.PREF_CLIENT_CARD_CODE_SELECTED;
import static grintsys.com.vanshop.SettingsMy.getSettings;

/**
 * Created by alienware on 2/10/2017.
 */

public class ProductMatrixTableFragment extends Fragment {

    private long productId = -1;
    private Product product;
    private static String ARG_PRODUCT_ID = "product-matrix-product-id";

    private ProgressBar progressView;
    private List<String> mWarehouseList = new ArrayList<>();

    private TextView SKUDescriptionText;
    private TextView ProductBrand;
    private Spinner mWarehouseSpinner;
    public ImageView productImage;
    private TableLayout productContainer;
    private View productLine;

    private boolean loadHighRes = false;

    public ProductMatrixTableFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ProductMatrixTableFragment newInstance(long productId) {
        ProductMatrixTableFragment fragment = new ProductMatrixTableFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            productId = getArguments().getLong(ARG_PRODUCT_ID);
            Timber.d("onCreate() - productId:%d - ProductMatrixFragment", productId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_matrix_custom, container, false);

        progressView = (ProgressBar) view.findViewById(R.id.product_matrix_progress);
        SKUDescriptionText = (TextView) view.findViewById(R.id.product_matrix_sku);
        ProductBrand = (TextView) view.findViewById(R.id.product_brand);
        productImage = (ImageView) view.findViewById(R.id.product_matrix_image);
        productContainer = (TableLayout) view.findViewById(R.id.product_matrix_table);
        productLine = view.findViewById(R.id.product_matrix_line);

        mWarehouseSpinner = (Spinner) view.findViewById(R.id.product_matrix_warehouse_spinner);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*try {
                    new FragmentPostToCartAsyncTask().execute();
                } catch (Exception e ){
                    Timber.e("Error on AddtoCard: %s", e.getMessage());
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, e.getMessage(), MsgUtils.ToastLength.SHORT);
                }*/
            }
        });

        getProduct(productId);

        return view;
    }

    private void addProductToCart(ProductVariant variant, User user, String cardcode) {
            String url = String.format(EndPoints.CART_ADD_ITEM, user.getId(), variant.getId(), variant.getNew_quantity(), cardcode, 0);
            JsonRequest addToCart = new JsonRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (BuildConfig.DEBUG) Timber.d("AddToCartResponse: %s", response);
                    //TODO: FIX ANALYTIC ADD PRODUCT TO CART CHECHO
                    Analytics.logAddProductToCart(product.getRemoteId(), product.getCode(), 0.0);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);
                }
            }, getFragmentManager(), user.getAccessToken());
            addToCart.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            addToCart.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(addToCart, CONST.PRODUCT_ADD_TO_CART_TAG);
    }

    private void addProductToWishList(ProductVariant variant, User user) {
        String url = String.format(EndPoints.WISHLIST_CREATE, user.getId(), variant.getId());
        JsonRequest addToCart = new JsonRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (BuildConfig.DEBUG) Timber.d("AddToCartResponse: %s", response);
                //TODO: FIX ANALYTIC ADD PRODUCT TO CART CHECHO
                Analytics.logAddProductToCart(product.getRemoteId(), product.getCode(), 0.0);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        }, getFragmentManager(), user.getAccessToken());
        addToCart.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        addToCart.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(addToCart, CONST.PRODUCT_ADD_TO_CART_TAG);
    }



    private void getProduct(final long productId) {
        final User user = SettingsMy.getActiveUser();
        if (user != null) {
            //String url = String.format(EndPoints.PRODUCTS_SINGLE_RELATED, SettingsMy.getActualNonNullShop(getActivity()).getId(), productId);
            String url = String.format(EndPoints.PRODUCTS_SINGLE_RELATED, user.getId(), productId);
            setContentVisible(CONST.VISIBLE.PROGRESS);

            GsonRequest<Product> getProductRequest = new GsonRequest<>(Request.Method.GET, url, null, Product.class,
                    new Response.Listener<Product>() {
                        @Override
                        public void onResponse(@NonNull Product response) {
                            MainActivity.setActionBarTitle(response.getName());
                            refreshScreenData(response);
                            generateMatrix(response);
                            setContentVisible(CONST.VISIBLE.CONTENT);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setContentVisible(CONST.VISIBLE.EMPTY);
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);
                }
            });
            getProductRequest.setRetryPolicy(MyApplication.getSimpleRetryPolice());
            getProductRequest.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(getProductRequest, CONST.PRODUCT_REQUESTS_TAG);
        }
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

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void generateMatrix(Product product){

        String warehouse = "01";
        List<ProductSize> sizes = product.getSizes(warehouse);
        List<ProductColor> colors = product.getColors(warehouse);
        List<ProductVariant> variants = product.getVariantsByWarehouse(warehouse);

        TableRow headerRow = new TableRow(getContext());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(dpToPx(8), dpToPx(4), 0, 0);

        TextView colorHeader = new TextView(getContext());
        colorHeader.setText("");
        headerRow.addView(colorHeader);


        for(int j=0; j<sizes.size(); j++){
            TextView sizeView = new TextView(getContext());
            sizeView.setText(sizes.get(j).getValue() + " | ");
            headerRow.addView(sizeView);
        }

        productContainer.addView(headerRow, layoutParams);

        View productLine = new View(getContext());
        productLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        productLine.setBackgroundColor(getResources().getColor(R.color.background_grey));

        productContainer.addView(productLine);

        for(int i=0; i<colors.size(); i++){

            ProductColor c = colors.get(i);

            TableRow row = new TableRow(getContext());
            TextView colorView = new TextView(getContext());
            colorView.setText(c.getValue());
            //colorView.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(20), LinearLayout.LayoutParams.WRAP_CONTENT));
            row.addView(colorView);

            for(int j=0; j<sizes.size(); j++){

                ProductSize s = sizes.get(j);
                Boolean isFound = false;
                int currentIndex = 0;
                ProductVariant variant = null;

                for(int x=0; x < variants.size(); x++)
                {
                    currentIndex = x;
                    variant = variants.get(x);

                    if(variant.getSize().getValue().equals(s.getValue()) &&
                       variant.getColor().getValue().equals(c.getValue()) &&
                       variant.getWarehouse().equals(warehouse))
                    {
                        isFound = true;
                        continue;
                    }
                }

                if(isFound)
                {
                    int q = variant.getQuantity();
                    EditText sizeEdit = new EditText(getContext());
                    sizeEdit.setText(String.valueOf(q));
                    sizeEdit.setTextColor(getResources().getColor(R.color.colorAccent));
                    sizeEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    sizeEdit.setTextSize(22);
                    row.addView(sizeEdit);

                    variants.remove(currentIndex);
                }else{

                    TextView sizeText = new TextView(getContext());
                    sizeText.setText("-");
                    sizeText.setEnabled(false);
                    row.addView(sizeText);
                }
            }

            productContainer.addView(row, layoutParams);

            View productLine2 = new View(getContext());
            productLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
            productLine.setBackgroundColor(getResources().getColor(R.color.background_grey));

            productContainer.addView(productLine2);
        }
    }

    private void refreshScreenData(Product product) {
        if (product != null) {
            Analytics.logProductView(product.getRemoteId(), product.getName());

            this.product = product;
            SKUDescriptionText.setText(product.getCode() + " - " + product.getName() + " - " + product.getSeason());
            //ProductBrand.setText(product.getBrand());

            if (loadHighRes && product.getMainImageHighRes() != null) {
                Picasso.with(getContext()).load(product.getMainImageHighRes())
                        .fit().centerInside()
                        .placeholder(R.drawable.placeholder_loading)
                        .error(R.drawable.placeholder_error)
                        .into(productImage);
            } else {
                Picasso.with(getContext()).load(product.getMainImage())
                        .fit().centerInside()
                        .placeholder(R.drawable.placeholder_loading)
                        .error(R.drawable.placeholder_error)
                        .into(productImage);
            }

            setSpinners(product);
        } else {
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, getString(R.string.Internal_error), MsgUtils.ToastLength.LONG);
            Timber.e(new RuntimeException(), "Refresh product screen with null product");
        }
    }

    private void setSpinners(Product product) {
        if (product != null && product.getVariants() != null && product.getVariants().size() > 0) {
            this.product = product;
            final List<ProductSize> productSizes = new ArrayList<>();

            for (ProductVariant pv : product.getVariants()) {
                ProductSize size = pv.getSize();
                if(!productSizes.contains(size)){
                    productSizes.add(size);
                }

                String ws = pv.getWarehouse();
                if(!mWarehouseList.contains(ws)){
                    mWarehouseList.add(ws);
                }
            }

            if(mWarehouseList.size() > 0){

                orderStringAscending(mWarehouseList);
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, mWarehouseList);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mWarehouseSpinner.setAdapter(spinnerArrayAdapter);

                mWarehouseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String warehouse = (String) mWarehouseSpinner.getSelectedItem();

                        if(warehouse != null) {
                            renderSizesView(productSizes, warehouse);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            }

            //renderSizesView(productSizes);
        }
    }

    private void renderSizesView(List<ProductSize> productSizes, String warehouse){

        List<ProductMatrixView> renderList = new ArrayList<>();
        for(ProductSize size : productSizes){
            ArrayList<ProductVariant> variants = product.getVariantsBySizeAndWarehouse(size, warehouse);
            if(variants.size() > 0){
                renderList.add(new ProductMatrixView(size, variants));
            }
        }
    }

    private void orderSizesAscending(List<ProductSize> productSizes){
       Collections.sort(productSizes, new Comparator<ProductSize>() {
           @Override
           public int compare(ProductSize productSize, ProductSize t1) {
               return productSize.getValue().compareTo(t1.getValue());
           }
       });
    }

    private void orderStringAscending(List<String> wareshouses){
        Collections.sort(wareshouses, new Comparator<String>() {
            @Override
            public int compare(String t1, String t2) {
                return t1.compareTo(t2);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getInstance().cancelPendingRequests(CONST.PRODUCT_REQUESTS_TAG);
        setContentVisible(CONST.VISIBLE.CONTENT);
        Timber.d("onStop - ProductMatrixFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("onResume - ProductMatrixFragment");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainActivity.setActionBarTitle("");
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private List<ProductMatrixView> pages;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            pages = new ArrayList<>();
        }

        public void setPages(List<ProductMatrixView> pages) {
            this.pages = pages;
            notifyDataSetChanged();
        }

        public void clearPages() {
            this.pages.clear();
        }

        public void addPageItem(ProductMatrixView productMatrixView){
            this.pages.add(productMatrixView);
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            ProductMatrixView productMatrixView = pages.get(position);
            return ProductColorFragment.newInstance(productMatrixView.getSize(), productMatrixView.getVariants());
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pages.get(position).getSize().getValue();
        }
    }


    private class FragmentPostToCartAsyncTask extends AsyncTask<Void, Void, Void>{

        String card_code = null;

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Timber.d("Finished FragmentPostToCartTask");

            if(card_code != null) {
                String result = getString(R.string.Product) + " " + getString(R.string.added_to_cart);
                Snackbar snackbar = Snackbar.make(productContainer, result, Snackbar.LENGTH_LONG)
                        .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent))
                        .setAction(R.string.Go_to_cart, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (getActivity() instanceof MainActivity)
                                    ((MainActivity) getActivity()).onCartSelected(0);
                            }
                        });
                TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();

                MainActivity.updateCartCountNotification();
            }else{
                messageDialog("Debes seleccionar cliente antes de usar el carrito");
            }
        }

        private void messageDialog(String message){

            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, message, MsgUtils.ToastLength.LONG);

            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage(message);
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    getString(R.string.Ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            SharedPreferences prefs = getSettings();
            card_code = prefs.getString(PREF_CLIENT_CARD_CODE_SELECTED, null);

            if(card_code != null){

                User user = SettingsMy.getActiveUser();
                if (user != null) {
                    List<ProductVariant> elements = ((MainActivity)getActivity()).getElements();

                    for(ProductVariant element: elements){
                        addProductToCart(element, user, card_code);
                        //addProductToWishList(element, user);
                    }

                    ((MainActivity)getActivity()).clearElements();
                }
            }
            return null;
        }
    }
}
