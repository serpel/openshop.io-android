package grintsys.com.vanshop.ux.dialogs;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import grintsys.com.vanshop.R;
import grintsys.com.vanshop.entities.Bank;
import grintsys.com.vanshop.entities.filtr.DeserializerFilters;
import grintsys.com.vanshop.entities.filtr.FilterType;
import grintsys.com.vanshop.entities.filtr.FilterTypeColor;
import grintsys.com.vanshop.entities.filtr.FilterTypeRange;
import grintsys.com.vanshop.entities.filtr.FilterTypeSelect;
import grintsys.com.vanshop.entities.filtr.FilterValueSelect;
import grintsys.com.vanshop.entities.filtr.Filters;
import grintsys.com.vanshop.entities.payment.CheckPayment;
import grintsys.com.vanshop.interfaces.BankDialogInterface;
import grintsys.com.vanshop.interfaces.FilterDialogInterface;
import grintsys.com.vanshop.listeners.OnSingleClickListener;
import grintsys.com.vanshop.ux.adapters.BankSpinnerAdapter;
import grintsys.com.vanshop.ux.adapters.SelectSpinnerAdapter;
import grintsys.com.vanshop.ux.fragments.payment.PaymentCheckFragment;
import timber.log.Timber;

/**
 * Created by alienware on 4/2/2017.
 */

public class FilterDialogCustomFragment extends DialogFragment {
    private Spinner categorySpinner, subcategorySpinner, familySpinner, subfamilySpinner, brandSpinner;
    private Filters filterData;
    private FilterDialogInterface filterDialogInterface;

    public static FilterDialogCustomFragment newInstance(Filters filter, FilterDialogInterface filterDialogInterface) {
        FilterDialogCustomFragment frag = new FilterDialogCustomFragment();

        if (filter == null || filterDialogInterface == null) {
            Timber.e(new RuntimeException(), "Created filterDialog with null parameters.");
            return null;
        }

        frag.filterData = filter;
        frag.filterDialogInterface = filterDialogInterface;


        return frag;
    }

    public FilterDialogCustomFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialogFullscreen);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Window window = d.getWindow();
            window.setLayout(width, height);
            window.setWindowAnimations(R.style.alertDialogAnimation);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.dialog_filters_custom, container, false);

        //prepareFilterRecycler(view);
        prepareSpinner(view);

        Button btnApply = (Button) view.findViewById(R.id.filter_btn_apply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filterUrl = "";
                if(subfamilySpinner.getSelectedItem() != null  && subfamilySpinner.getSelectedItemId() > 0)
                {
                    filterUrl+="category=" + ((FilterValueSelect)subfamilySpinner.getSelectedItem()).getId();
                }
                if(brandSpinner.getSelectedItem() != null && brandSpinner.getSelectedItemId() > 0)
                {
                    filterUrl+="&brand=" + ((FilterValueSelect)brandSpinner.getSelectedItem()).getId();
                }

                filterDialogInterface.onFilterSelected(filterUrl);

                dismiss();
            }
        });

        Button btnCancel = (Button) view.findViewById(R.id.filter_btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear all selected values
                if (filterData != null) {
                    categorySpinner.setSelection(0);
                    subcategorySpinner.setAdapter(null);
                    familySpinner.setAdapter(null);
                    subfamilySpinner.setAdapter(null);
                }
                //filterDialogInterface.onFilterCancelled();
                //dismiss();
            }
        });
        return view;
    }

    public void prepareSpinner(View view){


        final ArrayList<FilterValueSelect> filteredSubCategoryItems  = new ArrayList<>();
        final ArrayList<FilterValueSelect> filteredFamilyItems  = new ArrayList<>();
        final ArrayList<FilterValueSelect> filteredSubFamilyItems  = new ArrayList<>();

        categorySpinner = (Spinner) view.findViewById(R.id.dialog_filter_custom_category);
        subcategorySpinner = (Spinner) view.findViewById(R.id.dialog_filter_custom_subcategory);
        familySpinner = (Spinner) view.findViewById(R.id.dialog_filter_custom_family);
        subfamilySpinner = (Spinner) view.findViewById(R.id.dialog_filter_custom_subfamily);
        brandSpinner = (Spinner) view.findViewById(R.id.dialog_filter_custom_brand);

        FilterTypeSelect filterTypeSelect = (FilterTypeSelect) this.filterData.getFilters().get(0);
        final ArrayList<FilterValueSelect> categoryItems = new ArrayList<>(filterTypeSelect.getValues());
        FilterValueSelect select_item_category = new FilterValueSelect();
        select_item_category.setValue("Seleccionar");
        categoryItems.add(0, select_item_category);
        final SelectSpinnerAdapter selectSpinnerAdapter = new SelectSpinnerAdapter(getActivity(), categoryItems);
        categorySpinner.setAdapter(selectSpinnerAdapter);

        if(this.filterData.getFilters().size() == 4) {
            filterTypeSelect = (FilterTypeSelect) this.filterData.getFilters().get(4);
            final ArrayList<FilterValueSelect> brandItems = new ArrayList<>(filterTypeSelect.getValues());
            FilterValueSelect select_item_brand = new FilterValueSelect();
            select_item_brand.setValue("Seleccionar");
            brandItems.add(0, select_item_brand);
            final SelectSpinnerAdapter selectSpinnerAdapterBrand = new SelectSpinnerAdapter(getActivity(), brandItems);
            brandSpinner.setAdapter(selectSpinnerAdapterBrand);
        }

        categorySpinner.setOnItemSelectedListener(null);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0)
                {
                    long selected_id = categoryItems.get(i).getId();

                    FilterTypeSelect filterTypeSelectSubCategory = (FilterTypeSelect) filterData.getFilters().get(1);
                    List<FilterValueSelect> subCategoryItems = filterTypeSelectSubCategory.getValues();
                    filteredSubCategoryItems.clear();

                    for (FilterValueSelect subCategoryItem : subCategoryItems) {
                        if (subCategoryItem.getParent() == selected_id) {
                            filteredSubCategoryItems.add(subCategoryItem);
                        }
                    }
                    FilterValueSelect select_item = new FilterValueSelect();
                    select_item.setValue("Seleccionar");
                    filteredSubCategoryItems.add(0,select_item);
                    final SelectSpinnerAdapter selectSpinnerAdapter = new SelectSpinnerAdapter(getActivity(), filteredSubCategoryItems);
                    subcategorySpinner.setAdapter(selectSpinnerAdapter);
                    familySpinner.setAdapter(null);
                    subfamilySpinner.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Timber.d("OnNothingSelected - no change");
            }
        });

        if(this.filterData.getFilters().size() >= 1) {

            subcategorySpinner.setOnItemSelectedListener(null);
            subcategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(i>0)
                    {
                        long selected_id = filteredSubCategoryItems.get(i).getId();

                        FilterTypeSelect filterTypeSelectFamily = (FilterTypeSelect) filterData.getFilters().get(2);
                        List<FilterValueSelect> familyItems = filterTypeSelectFamily.getValues();
                        filteredFamilyItems.clear();

                        for (FilterValueSelect familyItem : familyItems) {
                            if (familyItem.getParent() == selected_id) {
                                filteredFamilyItems.add(familyItem);
                            }
                        }
                        FilterValueSelect select_item = new FilterValueSelect();
                        select_item.setValue("Seleccionar");
                        filteredFamilyItems.add(0,select_item);
                        final SelectSpinnerAdapter selectSpinnerAdapter = new SelectSpinnerAdapter(getActivity(), filteredFamilyItems);
                        familySpinner.setAdapter(selectSpinnerAdapter);
                        subfamilySpinner.setAdapter(null);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Timber.d("OnNothingSelected - no change");
                }
            });

            familySpinner.setOnItemSelectedListener(null);
            familySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(i>0)
                    {
                        long selected_id = filteredFamilyItems.get(i).getId();

                        FilterTypeSelect filterTypeSubFamily = (FilterTypeSelect) filterData.getFilters().get(3);
                        List<FilterValueSelect> subFamilyItems = filterTypeSubFamily.getValues();
                        filteredSubFamilyItems.clear();

                        for (FilterValueSelect subFamilyItem : subFamilyItems) {
                            if (subFamilyItem.getParent() == selected_id) {
                                filteredSubFamilyItems.add(subFamilyItem);
                            }
                        }
                        FilterValueSelect select_item = new FilterValueSelect();
                        select_item.setValue("Seleccionar");
                        filteredSubFamilyItems.add(0,select_item);
                        final SelectSpinnerAdapter selectSpinnerAdapter = new SelectSpinnerAdapter(getActivity(), filteredSubFamilyItems);
                        subfamilySpinner.setAdapter(selectSpinnerAdapter);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Timber.d("OnNothingSelected - no change");
                }
            });
        }
    }
}
