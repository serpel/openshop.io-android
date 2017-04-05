package intellisysla.com.vanheusenshop.ux.fragments.payment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.entities.Bank;
import intellisysla.com.vanheusenshop.interfaces.BankDialogInterface;
import intellisysla.com.vanheusenshop.listeners.OnSingleClickListener;
import intellisysla.com.vanheusenshop.ux.MainActivity;
import intellisysla.com.vanheusenshop.ux.adapters.BankSpinnerAdapter;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PaymentTransferFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PaymentTransferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentTransferFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_BANK_LIST = "bank-list";

    // TODO: Rename and change types of parameters
    private EditText amountEdit;
    private EditText referenceNumberEdit;

    private ArrayList<Bank> banks;
    private Spinner bankSpinner;
    private Bank selectedBank;
    private BankDialogInterface bankDialogInterface;

    private OnFragmentInteractionListener mListener;

    public PaymentTransferFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PaymentTransferFragment newInstance(ArrayList<Bank> banks) {
        PaymentTransferFragment fragment = new PaymentTransferFragment();
        Bundle args = new Bundle();

        args.putSerializable(ARG_BANK_LIST, banks);
        fragment.bankDialogInterface = new BankDialogInterface() {
            @Override
            public void onBankSelected(Bank bank) {

            }
        };
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
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_payment_transfer, container, false);

        if (getArguments() != null) {
            banks = (ArrayList<Bank>) getArguments().getSerializable(ARG_BANK_LIST);
        }

        referenceNumberEdit = (EditText) view.findViewById(R.id.payment_transfer_reference_number);
        referenceNumberEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String number = referenceNumberEdit.getText().toString();
                ((MainActivity)getActivity()).UpdateTransferReferenceNumber(number);
            }
        });

        amountEdit = (EditText) view.findViewById(R.id.payment_transfer_amount);
        amountEdit.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String amount_string = amountEdit.getText().toString();
                if(!amount_string.isEmpty()) {
                    double amount = Double.parseDouble(amount_string);
                    ((MainActivity)getActivity()).UpdateTransfer(amount);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        final EditText dateEdit = (EditText) view.findViewById(R.id.payment_transfer_date);
        final Calendar myCalendar = Calendar.getInstance();

        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateEdit.setText(sdf.format(myCalendar.getTime()));
        ((MainActivity)getActivity()).UpdateTransferDate(sdf.format(myCalendar.getTime()));


        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(),
                        R.style.MyDatePicker,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year,int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                String myFormat = "yyyy/MM/dd";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                dateEdit.setText(sdf.format(myCalendar.getTime()));
                                ((MainActivity)getActivity()).UpdateTransferDate(sdf.format(myCalendar.getTime()));
                            }
                        },
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH)-1,
                        myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        prepareSpinner(view);

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void prepareSpinner(View view){
        bankSpinner = (Spinner) view.findViewById(R.id.payment_transfer_banks);
        final BankSpinnerAdapter bankSpinnerAdapter = new BankSpinnerAdapter(getActivity(), this.banks);
        bankSpinner.setAdapter(bankSpinnerAdapter);
        bankSpinner.setOnItemSelectedListener(null);
        bankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedBank = bankSpinnerAdapter.getItem(i);

                ((MainActivity)getActivity()).UpdateTransferBank(selectedBank);

                if (bankDialogInterface != null)
                    bankDialogInterface.onBankSelected(selectedBank);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Timber.d("OnNothingSelected - no change");
            }
        });
    }
}
