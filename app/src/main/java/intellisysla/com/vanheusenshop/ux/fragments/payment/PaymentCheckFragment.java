package intellisysla.com.vanheusenshop.ux.fragments.payment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;


import java.lang.reflect.Array;
import java.util.ArrayList;

import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.entities.Bank;
import intellisysla.com.vanheusenshop.entities.client.Document;
import intellisysla.com.vanheusenshop.entities.payment.CheckPayment;
import intellisysla.com.vanheusenshop.interfaces.ChecksRecyclerInterface;
import intellisysla.com.vanheusenshop.listeners.OnSingleClickListener;
import intellisysla.com.vanheusenshop.utils.RecyclerMarginDecorator;
import intellisysla.com.vanheusenshop.ux.adapters.ChecksRecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PaymentCheckFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PaymentCheckFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class PaymentCheckFragment extends Fragment {

    private static final String ARG_BANK_LIST = "bank-list";
    private OnFragmentInteractionListener mListener;
    private ChecksRecyclerAdapter checksRecyclerAdapter;
    private GridLayoutManager checksGridLayoutManager;
    private RecyclerView checkRecyclerView;
    private Button addCheckButton;
    private ArrayList<CheckPayment> checks;
    private ArrayList<Bank> banks;

    public PaymentCheckFragment() {}

    // TODO: Rename and change types and number of parameters
    public static PaymentCheckFragment newInstance(ArrayList<Bank> banks) {
        PaymentCheckFragment fragment = new PaymentCheckFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BANK_LIST, banks);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            banks = (ArrayList<Bank>) getArguments().getSerializable(ARG_BANK_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_check, container, false);

        addCheckButton = (Button) view.findViewById(R.id.payment_check_add_button);
        addCheckButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {

            }
        });

        Bundle bundle = getArguments();

        if(bundle != null){
            banks = (ArrayList<Bank>) bundle.getSerializable(ARG_BANK_LIST);

            if(checksRecyclerAdapter !=null && checksRecyclerAdapter.getItemCount() > 0){
                prepareRecyclerAdapter();
                prepareCheckRecycler(view);
            }else{
                prepareCheckRecycler(view);
            }
        }

        return view;

        /*my_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                checks_array.remove(position);
                return true;
            }
        });

        theLayout = (LinearLayout) view.findViewById(R.id.linear_payment_s);

        Button add_check_button = (Button)view.findViewById(R.id.add_check_button);
        add_check_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final PopupWindow mPopupWindow = new PopupWindow(
                        popup,
                        AbsListView.LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                );

                final EditText edit_text_amount = (EditText) popup.findViewById(R.id.amount);
                final EditText edit_text_check_number = (EditText) popup.findViewById(R.id.check_num);
                final Spinner spinner_bank = (Spinner) popup.findViewById(R.id.bank);

                edit_text_amount.setText("");
                edit_text_check_number.setText("");

                if (getArguments() != null) {
                    banks = (ArrayList<Bank>) getArguments().getSerializable(ARG_BANK_LIST);
                }

                if(banks != null) {
                    ArrayList<String> bank_strings = new ArrayList<String>();
                    for(int i=0; i<banks.size();i++)
                        bank_strings.add(banks.get(i).getName());
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(popup.getContext(), android.R.layout.simple_spinner_dropdown_item, bank_strings);
                    spinner_bank.setAdapter(adapter);
                }

                mPopupWindow.setFocusable(true);
                mPopupWindow.update();

                // Set an elevation value for popup window
                // Call requires API level 21
                if(Build.VERSION.SDK_INT>=21){
                    mPopupWindow.setElevation(5.0f);
                }

                Button cancelButton = (Button) popup.findViewById(R.id.button_cancel);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                    }
                });

                Button okButton = (Button) popup.findViewById(R.id.button_ok);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(edit_text_amount.getText().toString().equals(""))
                            return;

                        checks_array.add(new CheckPayment(edit_text_check_number.getText().toString(), spinner_bank.getSelectedItem().toString(),
                                Double.parseDouble(edit_text_amount.getText().toString())));
                        ChecksAdapter adapter = new ChecksAdapter(getContext(), checks_array);
                        my_listview.setAdapter(adapter);

                        mPopupWindow.dismiss();
                    }
                });
                mPopupWindow.showAtLocation(theLayout, Gravity.CENTER,0,0);
            }
        });*/
    }

    private void prepareCheckRecycler(View view) {
        checkRecyclerView = (RecyclerView) view.findViewById(R.id.payment_check_recycler);
        checkRecyclerView.addItemDecoration(new RecyclerMarginDecorator(getActivity(), RecyclerMarginDecorator.ORIENTATION.BOTH));
        checkRecyclerView.setItemAnimator(new DefaultItemAnimator());
        checkRecyclerView.setHasFixedSize(true);

        checksGridLayoutManager = new GridLayoutManager(getActivity(), 1);

        checkRecyclerView.setLayoutManager(checksGridLayoutManager);
        checkRecyclerView.setAdapter(checksRecyclerAdapter);
    }

    public void prepareRecyclerAdapter(){
        checksRecyclerAdapter = new ChecksRecyclerAdapter(getActivity(), new ChecksRecyclerInterface(){
            @Override
            public void onCheckSelected(View view, CheckPayment checkPayment) {

            }
        });
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
