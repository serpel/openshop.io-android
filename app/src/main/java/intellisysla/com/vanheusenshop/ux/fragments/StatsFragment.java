package intellisysla.com.vanheusenshop.ux.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.ux.MainActivity;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private ProgressBar progressView;
    private TextView invoicedTextView;
    private TextView quotaTextView;
    private TextView quotaAcumTextView;
    private PieChart chart;

    private OnFragmentInteractionListener mListener;

    public StatsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance() {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        MainActivity.setActionBarTitle(getString(R.string.Stats));

        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        progressView = (ProgressBar) view.findViewById(R.id.stats_progress);
        quotaTextView = (TextView) view.findViewById(R.id.stats_quota);
        quotaAcumTextView = (TextView) view.findViewById(R.id.stats_quota_acum);
        chart = (PieChart) view.findViewById(R.id.stats_chart);

        FillChart();

        return view;
    }

    private void FillChart(){

        List<PieEntry> entries = new ArrayList<>();
        PieEntry x = new PieEntry(120000);
        x.setLabel(getString(R.string.Invoiced));

        PieEntry y = new PieEntry(155000);
        y.setLabel(getString(R.string.Quota));

        entries.add(x);
        entries.add(y);

        PieDataSet dataSet = new PieDataSet(entries, getString(R.string.Chart));

        PieData d = new PieData();
        d.addDataSet(dataSet);
        chart.setData(d);
        chart.invalidate();

        progressView.setVisibility(View.GONE);

      /*  String url = String.format(EndPoints.PRODUCTS_SINGLE_RELATED, productId);
        setContentVisible(CONST.VISIBLE.PROGRESS);

        GsonRequest<QuotaChart> getProductRequest = new GsonRequest<>(Request.Method.GET, url, null, Product.class,
                new Response.Listener<Product>() {
                    @Override
                    public void onResponse(@NonNull Product response) {
                        MainActivity.setActionBarTitle(response.getName());
                        if (response.getVariants() != null && response.getVariants().size() > 0) {
                            //getWishListInfo(productId);
                        }
                        addRecommendedProducts(response.getRelated());
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
        MyApplication.getInstance().addToRequestQueue(getProductRequest, CONST.PRODUCT_REQUESTS_TAG);*/
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
