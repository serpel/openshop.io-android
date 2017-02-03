package bf.io.openshop.ux.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import bf.io.openshop.CONST;
import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.SettingsMy;
import bf.io.openshop.api.EndPoints;
import bf.io.openshop.api.GsonRequest;
import bf.io.openshop.entities.SortItem;
import bf.io.openshop.entities.client.Client;
import bf.io.openshop.entities.client.ClientListResponse;
import bf.io.openshop.entities.product.Product;
import bf.io.openshop.entities.product.ProductListResponse;
import bf.io.openshop.interfaces.CategoryRecyclerInterface;
import bf.io.openshop.interfaces.ClientRecyclerInterface;
import bf.io.openshop.interfaces.FilterDialogInterface;
import bf.io.openshop.listeners.OnSingleClickListener;
import bf.io.openshop.utils.Analytics;
import bf.io.openshop.utils.EndlessRecyclerScrollListener;
import bf.io.openshop.utils.MsgUtils;
import bf.io.openshop.utils.RecyclerMarginDecorator;
import bf.io.openshop.ux.MainActivity;
import bf.io.openshop.ux.adapters.ClientsRecyclerAdapter;
import bf.io.openshop.ux.adapters.ProductsRecyclerAdapter;
import bf.io.openshop.ux.adapters.SortSpinnerAdapter;
import bf.io.openshop.ux.dialogs.FilterDialogFragment;
import timber.log.Timber;

/**
 * Created by alienware on 2/2/2017.
 */

public class ClientsFragment extends Fragment {
    private static final String SEARCH_QUERY = "search_query";
    private String searchQuery = null;

    private RecyclerView clientsRecycler;
    private GridLayoutManager clientsRecyclerLayoutManager;
    private ClientsRecyclerAdapter clientsRecyclerAdapter;
    private EndlessRecyclerScrollListener endlessRecyclerScrollListener;
    private View loadMoreProgress;

    public static ClientsFragment newInstance() {
        Bundle args = new Bundle();
        args.putString(SEARCH_QUERY, null);

        ClientsFragment fragment = new ClientsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ClientsFragment newInstance(String searchQuery) {
        Bundle args = new Bundle();
        args.putString(SEARCH_QUERY, searchQuery);

        ClientsFragment fragment = new ClientsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.fragment_clients, container, false);

        this.loadMoreProgress = view.findViewById(R.id.client_load_more_progress);

        Bundle startBundle = getArguments();
        if (startBundle != null) {
            searchQuery = startBundle.getString(SEARCH_QUERY, null);
            boolean isSearch = false;
            if (searchQuery != null && !searchQuery.isEmpty()) {
                isSearch = true;
            }

            //Timber.d("Client type: %s. CategoryId: %d. FilterUrl: %s.", categoryType, categoryId, filterParameters);

            MainActivity.setActionBarTitle("Clients");

            // Opened first time (not form backstack)
            if (clientsRecyclerAdapter == null || clientsRecyclerAdapter.getItemCount() == 0) {
                prepareRecyclerAdapter();
                prepareClientRecycler(view);
                getClients();

                //Analytics.logCategoryView(categoryId, categoryName, isSearch);
            } else {
                prepareClientRecycler(view);
                //prepareSortSpinner();
                Timber.d("Restore previous client state. (Clients already loaded) ");
            }
        } else {
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, getString(R.string.Internal_error), MsgUtils.ToastLength.LONG);
            Timber.e(new RuntimeException(), "Run category fragment without arguments.");
        }
        return view;
    }

    private void prepareClientRecycler(View view) {
        this.clientsRecycler = (RecyclerView) view.findViewById(R.id.clients_recycler);
        clientsRecycler.addItemDecoration(new RecyclerMarginDecorator(getActivity(), RecyclerMarginDecorator.ORIENTATION.BOTH));
        clientsRecycler.setItemAnimator(new DefaultItemAnimator());
        clientsRecycler.setHasFixedSize(true);
/*        switchLayoutManager.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new ImageView(getContext());
            }
        });*/

        clientsRecyclerLayoutManager = new GridLayoutManager(getActivity(), 1);

        clientsRecycler.setLayoutManager(clientsRecyclerLayoutManager);
        endlessRecyclerScrollListener = new EndlessRecyclerScrollListener(clientsRecyclerLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                Timber.e("Load more");
                /*if (productsMetadata != null && productsMetadata.getLinks() != null && productsMetadata.getLinks().getNext() != null) {
                    getProducts(productsMetadata.getLinks().getNext());
                } else {
                    Timber.d("CustomLoadMoreDataFromApi NO MORE DATA");
                }*/
            }
        };
        clientsRecycler.addOnScrollListener(endlessRecyclerScrollListener);
        clientsRecycler.setAdapter(clientsRecyclerAdapter);
    }

    private void prepareRecyclerAdapter() {
        clientsRecyclerAdapter = new ClientsRecyclerAdapter(getActivity(), new ClientRecyclerInterface() {
            @Override
            public void onClientSelected(View caller, Client client) {
                if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    setReenterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
                }
                ((MainActivity) getActivity()).onClientSelected(client.getCardCode());
            }
        });
    }

    /**
     * Animate change of rows in products recycler LayoutManager.
     *
     * @param layoutSpanCount number of rows to display.
     */
    private void animateRecyclerLayoutChange(final int layoutSpanCount) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new DecelerateInterpolator());
        fadeOut.setDuration(400);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clientsRecyclerLayoutManager.setSpanCount(layoutSpanCount);
                clientsRecyclerLayoutManager.requestLayout();
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new AccelerateInterpolator());
                fadeIn.setDuration(400);
                clientsRecycler.startAnimation(fadeIn);
            }
        });
        clientsRecycler.startAnimation(fadeOut);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Animation in = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_slowed);
        Animation out = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
        //switchLayoutManager.setInAnimation(in);
        //switchLayoutManager.setOutAnimation(out);
    }

    private void getClients() {
        loadMoreProgress.setVisibility(View.VISIBLE);

        GsonRequest<ClientListResponse> getClientsRequest = new GsonRequest<>(Request.Method.GET, EndPoints.CLIENTS, null, ClientListResponse.class,
                new Response.Listener<ClientListResponse>() {
                    @Override
                    public void onResponse(@NonNull ClientListResponse response) {
//                        Timber.d("response:" + response.toString());
                        clientsRecyclerAdapter.addClients(response.getClients());
                        checkEmptyContent();
                        loadMoreProgress.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (loadMoreProgress != null) loadMoreProgress.setVisibility(View.GONE);
                checkEmptyContent();
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        getClientsRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getClientsRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getClientsRequest, CONST.CLIENT_REQUESTS_TAG);
    }

    private void checkEmptyContent() {
        if (clientsRecyclerAdapter != null && clientsRecyclerAdapter.getItemCount() > 0) {
            clientsRecycler.setVisibility(View.VISIBLE);
        } else {
            clientsRecycler.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStop() {
        if (loadMoreProgress != null) {
            // Hide progress dialog if exist.
            if (loadMoreProgress.getVisibility() == View.VISIBLE && endlessRecyclerScrollListener != null) {
                // Fragment stopped during loading data. Allow new loading on return.
                endlessRecyclerScrollListener.resetLoading();
            }
            loadMoreProgress.setVisibility(View.GONE);
        }
        MyApplication.getInstance().cancelPendingRequests(CONST.CLIENT_REQUESTS_TAG);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (clientsRecycler != null) clientsRecycler.clearOnScrollListeners();
        super.onDestroyView();
    }
}
