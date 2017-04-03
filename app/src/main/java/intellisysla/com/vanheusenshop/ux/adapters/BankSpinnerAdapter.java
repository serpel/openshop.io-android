package intellisysla.com.vanheusenshop.ux.adapters;

import android.app.Activity;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.entities.Bank;
import intellisysla.com.vanheusenshop.entities.SortItem;

/**
 * Created by alienware on 4/2/2017.
 */

public class BankSpinnerAdapter extends ArrayAdapter<Bank> {

    private List<Bank> bankList = new ArrayList<>();

    public BankSpinnerAdapter(Activity activity, ArrayList<Bank> banks) {
        super(activity, R.layout.spinner_item_sort);
        this.setDropDownViewResource(R.layout.spinner_item_bank_dropdown);
        this.bankList = banks;
    }

    public int getCount() {
        return bankList.size();
    }

    public Bank getItem(int position) {
        return bankList.get(position);
    }
}
