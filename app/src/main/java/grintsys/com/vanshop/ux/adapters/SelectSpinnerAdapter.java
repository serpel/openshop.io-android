package grintsys.com.vanshop.ux.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import grintsys.com.vanshop.R;
import grintsys.com.vanshop.entities.Bank;
import grintsys.com.vanshop.entities.filtr.FilterValueSelect;

/**
 * Created by alienware on 4/2/2017.
 */

public class SelectSpinnerAdapter extends ArrayAdapter<FilterValueSelect> {

    private static final int layoutID = R.layout.spinner_item_bank_dropdown;
    private final LayoutInflater layoutInflater;
    private List<FilterValueSelect> list = new ArrayList<>();

    public SelectSpinnerAdapter(Activity activity, List<FilterValueSelect> list) {
        super(activity, R.layout.spinner_item_sort);
        this.setDropDownViewResource(layoutID);
        this.list = list;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return list.size();
    }

    public FilterValueSelect getItem(int position) {
        return list.get(position);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        SelectSpinnerAdapter.ListItemHolder holder;

        if (v == null) {
            v = layoutInflater.inflate(layoutID, parent, false);
            holder = new SelectSpinnerAdapter.ListItemHolder();
            holder.ValueText = (TextView) v.findViewById(R.id.bank_text);
            v.setTag(holder);
        } else {
            holder = (SelectSpinnerAdapter.ListItemHolder) v.getTag();
        }

        FilterValueSelect filter = getItem(position);

        if (filter != null) {
            holder.ValueText.setText(filter.getValue());
        }

        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public static class ListItemHolder {
        TextView ValueText;
    }
}
