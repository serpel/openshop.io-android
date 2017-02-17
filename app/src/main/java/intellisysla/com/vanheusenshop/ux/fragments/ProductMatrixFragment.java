package intellisysla.com.vanheusenshop.ux.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import intellisysla.com.vanheusenshop.BuildConfig;
import intellisysla.com.vanheusenshop.CONST;
import intellisysla.com.vanheusenshop.MyApplication;
import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.SettingsMy;
import intellisysla.com.vanheusenshop.api.EndPoints;
import intellisysla.com.vanheusenshop.api.GsonRequest;
import intellisysla.com.vanheusenshop.api.JsonRequest;
import intellisysla.com.vanheusenshop.entities.User;
import intellisysla.com.vanheusenshop.entities.product.Product;
import intellisysla.com.vanheusenshop.entities.product.ProductMatrixView;
import intellisysla.com.vanheusenshop.entities.product.ProductSize;
import intellisysla.com.vanheusenshop.entities.product.ProductVariant;
import intellisysla.com.vanheusenshop.utils.Analytics;
import intellisysla.com.vanheusenshop.utils.MsgUtils;
import intellisysla.com.vanheusenshop.ux.MainActivity;
import intellisysla.com.vanheusenshop.ux.adapters.MyProductRecyclerViewAdapter;
import intellisysla.com.vanheusenshop.views.ResizableImageView;
import timber.log.Timber;

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

    private TextView SKUDescriptionText;
    private Spinner mWarehouseSpinner;
    public ImageView productImage;

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
        productImage = (ImageView) view.findViewById(R.id.product_matrix_image);

        mSectionsPagerAdapter = new ProductMatrixFragment.SectionsPagerAdapter(getFragmentManager());
        mWarehouseSpinner = (Spinner) view.findViewById(R.id.product_matrix_warehouse_spinner);

        //This show the scrollview correctly
        NestedScrollView scrollView = (NestedScrollView) view.findViewById (R.id.product_matrix_nested_scroll);
        scrollView.setFillViewport (true);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.product_matrix_view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.product_matrix_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        ((MainActivity)getActivity()).getSupportActionBar().hide();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.product_matrix_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < fragments.size(); i++){
                    ProductColorFragment fragment = (ProductColorFragment) fragments.get(i);
                    MyProductRecyclerViewAdapter adapter = fragment.GetProductAdapter();

                    for (int j = 0; j < adapter.getItemCount(); j++){
                        new PostToCartTask().execute(adapter.getItemAt(j));
                    }
                }
            }
        });

        getProduct(productId);

        return view;
    }


    private void postProductToCart(ProductVariant variant) {
        User user = SettingsMy.getActiveUser();
        if (user != null && variant.getNew_quantity() > 0) {
            String url = String.format(EndPoints.CART_ADD_ITEM, user.getId(), variant.getId(), variant.getNew_quantity());
            JsonRequest addToCart = new JsonRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (BuildConfig.DEBUG) Timber.d("AddToCartResponse: %s", response);
                    //TODO: FIX ANALYTIC ADD PRODUCT TO CART CHECHO
                    Analytics.logAddProductToCart(product.getRemoteId(), product.getName(), 0.0);
                    MainActivity.updateCartCountNotification();

                    String result = getString(R.string.Product) + " " + getString(R.string.added_to_cart);
                    /*Snackbar snackbar = Snackbar.make(productContainer, result, Snackbar.LENGTH_LONG)
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
                    */
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);
                }
            }, getFragmentManager(), user.getAccessToken());
            addToCart.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            addToCart.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(addToCart, CONST.PRODUCT_REQUESTS_TAG);
        }
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
        getProductRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
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
            List<ProductSize> productSizes = new ArrayList<>();
            List<String> warehouses = new ArrayList<>();

            for (ProductVariant pv : product.getVariants()) {
                ProductSize size = pv.getSize();
                String warehouse = pv.getWarehouse();

                if(!productSizes.contains(size)){
                    productSizes.add(size);
                }

                if(!warehouses.contains(warehouse)){
                    warehouses.add(warehouse);
                }
            }

            List<ProductMatrixView> items = new ArrayList<>();
            fragments = new ArrayList<>();
            for(ProductSize size : productSizes){
                ArrayList<ProductVariant> variants = product.getVariantsBySize(size);
                items.add(new ProductMatrixView(size, variants));
                fragments.add(ProductColorFragment.newInstance(size, variants));
            }

            //Create Warehouse Spinner
            if(warehouses.size() > 0){
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, warehouses);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mWarehouseSpinner.setAdapter(spinnerArrayAdapter);
            }

            mSectionsPagerAdapter.setPages(items);
            mSectionsPagerAdapter.setFragments(fragments);
            mSectionsPagerAdapter.updateView();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        //MainActivity.setActionBarVisible(true);
        MyApplication.getInstance().cancelPendingRequests(CONST.PRODUCT_REQUESTS_TAG);
        setContentVisible(CONST.VISIBLE.CONTENT);
        MainActivity.restoreActionBar();
        ((MainActivity)getActivity()).getSupportActionBar().show();
        Timber.d("onStop - ProductMatrixFragment");
        super.onStop();
    }

    @Override
    public void onResume() {
        //MainActivity.setActionBarVisible(false);
        Timber.d("onResume - ProductMatrixFragment");
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragments;
        private List<ProductMatrixView> pages;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            pages = new ArrayList<>();
            fragments = new ArrayList<>();
        }

        public SectionsPagerAdapter(FragmentManager fm, List<ProductMatrixView> pages) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.pages = pages;
            for(ProductMatrixView item: pages){
                fragments.add(ProductColorFragment.newInstance(item.getSize(), item.getVariants()));
            }
        }

        public void setFragments(List<Fragment> fragments) {
            this.fragments = fragments;
        }

        public void setPages(List<ProductMatrixView> pages) {
            this.pages = pages;
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
            return pages.get(position).getSize().getValue();
        }
    }


    private class PostToCartTask extends AsyncTask<ProductVariant, Integer, Integer> {
        @Override
        protected Integer doInBackground(ProductVariant... productVariants) {

            int count = productVariants.length;
            for (int i = 0; i < count; i++) {
                postProductToCart(productVariants[i]);
            }
            return count;
        }

        @Override
        protected void onPostExecute(Integer count) {
            Timber.d("Total postToProductCart: %d", count);
        }
    }
}
