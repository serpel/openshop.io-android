package intellisysla.com.vanheusenshop.ux.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import intellisysla.com.vanheusenshop.CONST;
import intellisysla.com.vanheusenshop.MyApplication;
import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.SettingsMy;
import intellisysla.com.vanheusenshop.api.EndPoints;
import intellisysla.com.vanheusenshop.api.GsonRequest;
import intellisysla.com.vanheusenshop.entities.User;
import intellisysla.com.vanheusenshop.entities.mainMenu.MainMenu;
import intellisysla.com.vanheusenshop.interfaces.LoginDialogInterface;
import intellisysla.com.vanheusenshop.utils.MsgUtils;
import intellisysla.com.vanheusenshop.ux.MainActivity;
import intellisysla.com.vanheusenshop.ux.dialogs.LoginDialogFragment;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GridMenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GridMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GridMenuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private Button clientsButton, inventoryButton, profileButton, signoutButton, reportsButton;
    private TextView clientCountText, inventoryCountText, reportsCountText;

    public GridMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GridMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GridMenuFragment newInstance(String param1, String param2) {
        GridMenuFragment fragment = new GridMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_grid_menu, container, false);
        MainActivity.setActionBarTitle(getString(R.string.MainMenu));

        clientsButton = (Button) view.findViewById(R.id.main_menu_clients);
        inventoryButton = (Button) view.findViewById(R.id.main_menu_inventory);
        profileButton = (Button) view.findViewById(R.id.main_menu_profile);
        signoutButton = (Button) view.findViewById(R.id.main_menu_sign_out);
        reportsButton = (Button) view.findViewById(R.id.main_menu_reports);

        clientCountText = (TextView) view.findViewById(R.id.main_menu_clients_badge);
        inventoryCountText = (TextView) view.findViewById(R.id.main_menu_intentory_badge);
        reportsCountText = (TextView) view.findViewById(R.id.main_menu_reports_badge);

        clientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: validate if user is valid login

                Timber.d("onClientButonClick");

                Fragment fragment = ClientsFragment.newInstance();
                if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    fragment.setReturnTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.fade));
                }
                replaceFragment(fragment, ClientsFragment.class.getSimpleName());
            }
        });

        inventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO: validate if user is valid login
                Timber.d("onInventoryButonClick");

                Fragment fragment = new BannersFragment();
                if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    fragment.setReturnTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.fade));
                }
                replaceFragment(fragment, BannersFragment.class.getSimpleName());
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO: validate if user is valid login
                Timber.d("onProfileButonClick");

                Fragment fragment = new AccountFragment();
                if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    fragment.setReturnTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.fade));
                }
                replaceFragment(fragment, AccountFragment.class.getSimpleName());
            }
        });

        reportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.d("onReportsButonClick");
            }
        });

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: set user to null before continue
                Timber.d("onLogoutButonClick");

                if (SettingsMy.getActiveUser() != null) {
                    LoginDialogFragment.logoutUser();
                }

                LoginDialogFragment loginDialogFragment = LoginDialogFragment.newInstance(new LoginDialogInterface() {
                    @Override
                    public void successfulLoginOrRegistration(User user) {
                        MainActivity.updateCartCountNotification();

                        Fragment fragment = new GridMenuFragment();
                        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                            fragment.setReturnTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.fade));
                        }
                        replaceFragment(fragment, GridMenuFragment.class.getSimpleName());
                    }
                });
                loginDialogFragment.show(getFragmentManager(), LoginDialogFragment.class.getSimpleName());
            }
        });

        getBadgeCount();

        return view;
    }

    private void replaceFragment(Fragment newFragment, String transactionTag) {
        if (newFragment != null) {
            FragmentManager frgManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = frgManager.beginTransaction();
            fragmentTransaction.addToBackStack(transactionTag);
            fragmentTransaction.replace(R.id.main_content_frame, newFragment).commit();
            frgManager.executePendingTransactions();
        } else {
            Timber.e(new RuntimeException(), "Replace fragments with null newFragment parameter.");
        }
    }

    public void getBadgeCount(){
        GsonRequest<MainMenu> getProductRequest = new GsonRequest<>(Request.Method.GET, EndPoints.MAIN_MENU_BADGE_COUNT, null, MainMenu.class,
                new Response.Listener<MainMenu>() {
                    @Override
                    public void onResponse(@NonNull MainMenu response) {
                        setBadgeCount(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        getProductRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getProductRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getProductRequest, CONST.MAIN_MENU_REQUESTS_TAG);
    }

    public void setBadgeCount(MainMenu mainMenu){

        if(mainMenu != null){
            clientCountText.setText(String.valueOf(mainMenu.getClientsCount()));
            inventoryCountText.setText(String.valueOf(mainMenu.getProductsCount()));
            reportsCountText.setText(String.valueOf(mainMenu.getReportsCount()));
        }
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

    @Override
    public void onStop() {
        MyApplication.getInstance().getRequestQueue().cancelAll(CONST.MAIN_MENU_REQUESTS_TAG);
        super.onStop();
    }
}
