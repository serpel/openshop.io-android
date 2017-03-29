package intellisysla.com.vanheusenshop.ux.fragments.payment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PaymentCheckFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PaymentCheckFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

class ChecksAdapter extends ArrayAdapter<CheckPayment> {
    private ArrayList<CheckPayment> checks;

    public ChecksAdapter(Context context, ArrayList<CheckPayment> users) {
        super(context, 0, users);
    }

    public ChecksAdapter(Context context) {
        super(context, 0);
        checks = new ArrayList<>();
    }

    public void addCheck(CheckPayment check){
        this.checks.add(check);
        notifyDataSetChanged();
    }

    public ArrayList<CheckPayment> getChecks() {
        return checks;
    }

    public void setChecks(ArrayList<CheckPayment> checks) {
        this.checks = checks;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CheckPayment check_payment = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_check, parent, false);
        }
        TextView amount = (TextView) convertView.findViewById(R.id.amount);
        TextView number = (TextView) convertView.findViewById(R.id.number);
        TextView bank = (TextView) convertView.findViewById(R.id.bank);

        amount.setText(getContext().getString(R.string.Amount) + ": " + check_payment.getAmount());
        bank.setText(getContext().getString(R.string.Bank) + ": " + check_payment.getBank());
        number.setText(getContext().getString(R.string.CheckNumber) + ": " + check_payment.getCheckNumber());

        return convertView;
    }
}

public class PaymentCheckFragment extends Fragment {

    private static final String ARG_BANK_LIST = "bank-list";
    private OnFragmentInteractionListener mListener;
    private ChecksAdapter checksAdapter;
    ArrayList<CheckPayment> checks_array;
    ListView my_listview;
    LinearLayout theLayout;

    ArrayList<Bank> banks;

    public PaymentCheckFragment() {
        // Required empty public constructor
    }

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
        final View popup = inflater.inflate(R.layout.popup_add_check,container,false);

        //final PaymentCheckFragment payment_check_fragment = this;

        checks_array = new ArrayList<>();
        checksAdapter = new ChecksAdapter(getContext());
        my_listview = (ListView)view.findViewById(R.id.check_list_view);
        my_listview.setAdapter(checksAdapter);

        my_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
        });

        return view;
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
