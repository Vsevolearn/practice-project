package com.uclab.everytree.ui.TreeRecordTabs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.uclab.everytree.R;
import com.uclab.everytree.models.GenericTextWatcher;
import com.uclab.everytree.models.SetterField;
import com.uclab.everytree.models.serializers.Record;
import com.uclab.everytree.models.serializers.spinner.SiteType;
import com.uclab.everytree.services.AppService;

import java.util.ArrayList;
import java.util.List;

public class Tab3 extends Fragment{
    private static final String TAG = Tab3.class.getSimpleName();
    private EditText nearestAddressTxt;
    private Spinner siteTypeSpin;
    private AppService appService;
    private List<String> siteTypesList;
    private Context mContext;

    public Tab3() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appService = new AppService(mContext);
        setUpSiteTypesList();
    }

    private void setUpSiteTypesList()
    {
        siteTypesList = new ArrayList<>();

        for (SiteType name : appService.getSiteTypes()) {
            siteTypesList.add(name.getName());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab3, container, false);

        nearestAddressTxt = v.findViewById(R.id.nearestAddressTxt);

        nearestAddressTxt.addTextChangedListener(new GenericTextWatcher(nearestAddressTxt));

        siteTypeSpin = v.findViewById(R.id.siteTypeSpin);

        //Спиннер тип участка
        ArrayAdapter<String> siteTypeAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, siteTypesList);
        siteTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        siteTypeSpin.setAdapter(siteTypeAdapter);

        siteTypeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                    AppService.getEditableRecord().setSiteType(position + 1);

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        setMode();

        return v;
    }

    //Выводит информацию в форму
    private void showTreeInfoInForm()
    {
        Record record = appService.getRecord();

        SetterField.setTextField(record.getNearestAddress(), nearestAddressTxt);

        if (record.getSiteType() != null) {
            siteTypeSpin.setSelection(record.getSiteType() - 1);
        }
    }

    //Устанавливает режим работы
    private void setMode()
    {
        //show mode
        if(!appService.isAddMode())
        {
            nearestAddressTxt.setKeyListener(null);
            siteTypeSpin.setEnabled(false);
        }

        showTreeInfoInForm();
    }
}