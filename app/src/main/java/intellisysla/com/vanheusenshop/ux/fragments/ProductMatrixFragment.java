package intellisysla.com.vanheusenshop.ux.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

import intellisysla.com.vanheusenshop.BuildConfig;
import intellisysla.com.vanheusenshop.CONST;
import intellisysla.com.vanheusenshop.MyApplication;
import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.SettingsMy;
import intellisysla.com.vanheusenshop.api.EndPoints;
import intellisysla.com.vanheusenshop.api.GsonRequest;
import intellisysla.com.vanheusenshop.api.JsonRequest;
import intellisysla.com.vanheusenshop.entities.User.User;
import intellisysla.com.vanheusenshop.entities.product.Product;
import intellisysla.com.vanheusenshop.entities.product.ProductElement;
import intellisysla.com.vanheusenshop.entities.product.ProductMatrixView;
import intellisysla.com.vanheusenshop.entities.product.ProductSize;
import intellisysla.com.vanheusenshop.entities.product.ProductVariant;
import intellisysla.com.vanheusenshop.utils.Analytics;
import intellisysla.com.vanheusenshop.utils.MsgUtils;
import intellisysla.com.vanheusenshop.ux.MainActivity;
import intellisysla.com.vanheusenshop.ux.adapters.MyProductRecyclerViewAdapter;
import intellisysla.com.vanheusenshop.ux.fragments.payment.PaymentMainFragment;
import okhttp3.internal.framed.Variant;
import timber.log.Timber;

import static intellisysla.com.vanheusenshop.SettingsMy.PREF_CLIENT_CARD_CODE_SELECTED;
import static intellisysla.com.vanheusenshop.SettingsMy.getSettings;

/**
 * Created by alienware on 2/10/2017.
 */

public class ProductMatrixFragment extends Fragment {

    private long productId = -1;
    private Product product;
    private static String ARG_PRODUCT_ID = "product-matrix-product-id";

    private ProductMatrixFragment.SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ProgressBar progressView;
    private List<Fragment> fragments;
    private List<String> mWarehouseList = new ArrayList<>();

    private TextView SKUDescriptionText;
    private TextView ProductBrand;
    private Spinner mWarehouseSpinner;
    public ImageView productImage;
    private RelativeLayout productContainer;

    private boolean loadHighRes = false;

    public ProductMatrixFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ProductMatrixFragment newInstance(long productId) {
        ProductMatrixFragment fragment = new ProductMatrixFragment();
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
        View view = inflater.inflate(R.layout.fragment_product_matrix, container, false);

        progressView = (ProgressBar) view.findViewById(R.id.product_matrix_progress);
        SKUDescriptionText = (TextView) view.findViewById(R.id.product_matrix_sku);
        //ProductBrand = (TextView) view.findViewById(R.id.product_brand);
        productImage = (ImageView) view.findViewById(R.id.product_matrix_image);
        productContainer = (RelativeLayout) view.findViewById(R.id.product_matrix_main_layout);

        mSectionsPagerAdapter = new ProductMatrixFragment.SectionsPagerAdapter(getFragmentManager());
        mWarehouseSpinner = (Spinner) view.findViewById(R.id.product_matrix_warehouse_spinner);

        //This show the scrollview correctly
       /* NestedScrollView scrollView = (NestedScrollView) view.findViewById (R.id.product_matrix_nested_scroll);
        scrollView.setFillViewport (true);*/

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.product_matrix_view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.product_matrix_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    new FragmentPostToCartAsyncTask().execute();
                } catch (Exception e ){
                    Timber.e("Error on AddtoCard: %s", e.getMessage());
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, e.getMessage(), MsgUtils.ToastLength.SHORT);
                }
            }
        });

        getProduct(productId);

        return view;
    }

    private void addProductToCart(ProductVariant variant, User user, String cardcode) {
            String url = String.format(EndPoints.CART_ADD_ITEM, user.getId(), variant.getId(), variant.getNew_quantity(), cardcode);
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
        // Load product info
        //TODO: multiple companies
        //String url = String.format(EndPoints.PRODUCTS_SINGLE_RELATED, SettingsMy.getActualNonNullShop(getActivity()).getId(), productId);
        String url = String.format(EndPoints.PRODUCTS_SINGLE_RELATED, productId);
        setContentVisible(CONST.VISIBLE.PROGRESS);

        GsonRequest<Product> getProductRequest = new GsonRequest<>(Request.Method.GET, url, null, Product.class,
                new Response.Listener<Product>() {
                    @Override
                    public void onResponse(@NonNull Product response) {
                        MainActivity.setActionBarTitle(response.getName());
                        refreshScreenData(response);
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

    private void renderSizesView(List<ProductSize> productSizes){

        orderSizesAscending(productSizes);

        for(ProductSize size : productSizes){
            ProductMatrixView productMatrixView = new ProductMatrixView(size, product.getVariantsBySize(size));
            mSectionsPagerAdapter.addPageItem(productMatrixView);
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

        if(renderList.size() > 0) {
            mSectionsPagerAdapter.clearPages();
            mSectionsPagerAdapter.setPages(renderList);
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
                                    ((MainActivity) getActivity()).onCartSelected();
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
                    }

                    ((MainActivity)getActivity()).clearElements();
                }
            }
            return null;
        }
    }
}
