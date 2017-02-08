package bf.io.openshop.ux.fragments;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.w3c.dom.Text;

import bf.io.openshop.CONST;
import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.SettingsMy;
import bf.io.openshop.api.EndPoints;
import bf.io.openshop.api.GsonRequest;
import bf.io.openshop.entities.client.Client;
import bf.io.openshop.entities.client.ClientListResponse;
import bf.io.openshop.entities.client.Document;
import bf.io.openshop.entities.client.DocumentListResponse;
import bf.io.openshop.interfaces.ClientRecyclerInterface;
import bf.io.openshop.interfaces.DocumentRecyclerInterface;
import bf.io.openshop.utils.EndlessRecyclerScrollListener;
import bf.io.openshop.utils.MsgUtils;
import bf.io.openshop.utils.RecyclerMarginDecorator;
import bf.io.openshop.ux.MainActivity;
import bf.io.openshop.ux.adapters.ClientsRecyclerAdapter;
import bf.io.openshop.ux.adapters.DocumentsRecyclerAdapter;
import timber.log.Timber;

import static bf.io.openshop.SettingsMy.getSettings;

/**
 * Created by alienware on 2/2/2017.
 */

public class DocumentsFragment extends Fragment {
    private static final String SEARCH_QUERY = "search_query";
    private static final String CARD_CODE = "cardCode";
    private String searchQuery = null;

    private RecyclerView documentsRecycler;
    private GridLayoutManager documentsRecyclerLayoutManager;
    private DocumentsRecyclerAdapter documentsRecyclerAdapter;
    private EndlessRecyclerScrollListener endlessRecyclerScrollListener;
    private View loadMoreProgress;

    private TextView clientCode, clientName, clientCreditLimit, clientBalance, clientInOrders, clientPayCondition;
    private Button documentBegin;
    public static DocumentsFragment newInstance() {
        Bundle args = new Bundle();
        args.putString(SEARCH_QUERY, null);

        DocumentsFragment fragment = new DocumentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static DocumentsFragment newInstance(String cardCode) {
        Bundle args = new Bundle();
        args.putString(CARD_CODE, cardCode);
        args.putString(SEARCH_QUERY, null);

        DocumentsFragment fragment = new DocumentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static DocumentsFragment newInstance(String searchQuery, String cardCode) {
        Bundle args = new Bundle();
        args.putString(SEARCH_QUERY, searchQuery);
        args.putString(CARD_CODE, cardCode);

        DocumentsFragment fragment = new DocumentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.fragment_documents, container, false);

        this.loadMoreProgress = view.findViewById(R.id.documents_load_more_progress);

        Bundle startBundle = getArguments();
        if (startBundle != null) {
            searchQuery = startBundle.getString(SEARCH_QUERY, null);
            String card_code = startBundle.getString(CARD_CODE, "");
            boolean isSearch = false;
            if (searchQuery != null && !searchQuery.isEmpty()) {
                isSearch = true;
            }
            //Timber.d("Client type: %s. CategoryId: %d. FilterUrl: %s.", categoryType, categoryId, filterParameters);
            MainActivity.setActionBarTitle("Documents");

            // Opened first time (not form backstack)
            if (documentsRecyclerAdapter == null || documentsRecyclerAdapter.getItemCount() == 0) {
                prepareRecyclerAdapter();
                prepareClientRecycler(view);
                getDocuments(card_code);

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

        clientCode = (TextView) view.findViewById(R.id.document_client_code);
        clientName = (TextView) view.findViewById(R.id.document_client_name);
        clientCreditLimit =  (TextView) view.findViewById(R.id.document_document_credit_limit);
        clientBalance = (TextView) view.findViewById(R.id.document_balance);
        clientInOrders = (TextView) view.findViewById(R.id.document_orders);
        clientPayCondition = (TextView) view.findViewById(R.id.document_document_pay_condition);

        documentBegin = (Button) view.findViewById(R.id.document_begin);
        documentBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String customer = clientCode.getText().toString();

                SharedPreferences prefs = getSettings();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(SettingsMy.PREF_CLIENT_CARD_CODE_SELECTED, customer);
                editor.commit();

                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Customer) + ": " + customer , MsgUtils.ToastLength.SHORT);
            }
        });

        return view;
    }

    private void prepareClientRecycler(View view) {
        this.documentsRecycler = (RecyclerView) view.findViewById(R.id.documents_recycler);
        documentsRecycler.addItemDecoration(new RecyclerMarginDecorator(getActivity(), RecyclerMarginDecorator.ORIENTATION.BOTH));
        documentsRecycler.setItemAnimator(new DefaultItemAnimator());
        documentsRecycler.setHasFixedSize(true);
/*        switchLayoutManager.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new ImageView(getContext());
            }
        });*/

        documentsRecyclerLayoutManager = new GridLayoutManager(getActivity(), 1);

        documentsRecycler.setLayoutManager(documentsRecyclerLayoutManager);
        endlessRecyclerScrollListener = new EndlessRecyclerScrollListener(documentsRecyclerLayoutManager) {
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
        documentsRecycler.addOnScrollListener(endlessRecyclerScrollListener);
        documentsRecycler.setAdapter(documentsRecyclerAdapter);
    }

    private void prepareRecyclerAdapter() {
        //On click event
        documentsRecyclerAdapter = new DocumentsRecyclerAdapter(getActivity(), new DocumentRecyclerInterface() {
            @Override
            public void onDocumentRecyclerInterface(View caller, Document document) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    setReenterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
                }
                ((MainActivity) getActivity()).onDocumentSelected(document.getDocumentCode());
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
                documentsRecyclerLayoutManager.setSpanCount(layoutSpanCount);
                documentsRecyclerLayoutManager.requestLayout();
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new AccelerateInterpolator());
                fadeIn.setDuration(400);
                documentsRecycler.startAnimation(fadeIn);
            }
        });
        documentsRecycler.startAnimation(fadeOut);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Animation in = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_slowed);
        Animation out = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
        //switchLayoutManager.setInAnimation(in);
        //switchLayoutManager.setOutAnimation(out);
    }

    private void getDocuments(String card_code) {
        loadMoreProgress.setVisibility(View.VISIBLE);

        String url = String.format(EndPoints.DOCUMENTS_SINGLE, card_code);

        GsonRequest<DocumentListResponse> getDocumentRequest = new GsonRequest<>(Request.Method.GET, url, null, DocumentListResponse.class,
                new Response.Listener<DocumentListResponse>() {
                    @Override
                    public void onResponse(@NonNull DocumentListResponse response) {
//                        Timber.d("response:" + response.toString());
                        documentsRecyclerAdapter.addDocuments(response.getDocuments());
                        checkEmptyContent();

                        clientCode.setText(response.getClientCardCode());
                        clientName.setText(response.getClientName());
                        clientCreditLimit.setText(String.valueOf(response.getCreaditLimit()));
                        clientBalance.setText(String.valueOf(response.getBalance()));
                        clientInOrders.setText(String.valueOf(response.getInOrders()));
                        clientPayCondition.setText(response.getPayCondition());

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
        getDocumentRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getDocumentRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getDocumentRequest, CONST.DOCUMENT_REQUESTS_TAG);
    }

    private void checkEmptyContent() {
        if (documentsRecyclerAdapter != null && documentsRecyclerAdapter.getItemCount() > 0) {
            documentsRecycler.setVisibility(View.VISIBLE);
        } else {
            documentsRecycler.setVisibility(View.INVISIBLE);
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
        MyApplication.getInstance().cancelPendingRequests(CONST.DOCUMENT_REQUESTS_TAG);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (documentsRecycler != null) documentsRecycler.clearOnScrollListeners();
        super.onDestroyView();
    }
}
