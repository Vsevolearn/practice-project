package com.uclab.everytree.ui.TreeRecordTabs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.uclab.everytree.R;
import com.uclab.everytree.models.GenericTextWatcher;
import com.uclab.everytree.models.SetterField;
import com.uclab.everytree.models.serializers.Record;
import com.uclab.everytree.models.serializers.spinner.CommonName;
import com.uclab.everytree.models.serializers.spinner.ScientificName;
import com.uclab.everytree.services.AppService;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class Tab1 extends Fragment implements View.OnClickListener {
    private static final String TAG = Tab1.class.getSimpleName();
    private CheckBox convertToDiameterBox;
    private EditText trunk_diameterTxt, tree_heightTxt, skeletal_branchesTxt, conditionTxt;
    private TextView scientific_nameTxt;
    private Spinner commonNamesSpin;
    private List<String> commonNamesList;
    private AppService appService;
    private Context mContext;

    public Tab1() {
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

        setUpCommonNamesList();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab1, container, false);

        scientific_nameTxt = v.findViewById(R.id.scientific_nameTxt);

        trunk_diameterTxt = v.findViewById(R.id.trunk_diameterTxt);

        trunk_diameterTxt.addTextChangedListener(new GenericTextWatcher(trunk_diameterTxt));

        tree_heightTxt = v.findViewById(R.id.tree_heightTxt);

        tree_heightTxt.addTextChangedListener(new GenericTextWatcher(tree_heightTxt));

        skeletal_branchesTxt = v.findViewById(R.id.skeletal_branchesTxt);

        skeletal_branchesTxt.addTextChangedListener(new GenericTextWatcher(skeletal_branchesTxt));

        conditionTxt = v.findViewById(R.id.conditionTxt);

        conditionTxt.addTextChangedListener(new GenericTextWatcher(conditionTxt));

        convertToDiameterBox = v.findViewById(R.id.convertToDiameterBox);
        convertToDiameterBox.setOnClickListener(this);

        commonNamesSpin = v.findViewById(R.id.listTypesSpin);

        //Спиннер тип участка
        ArrayAdapter<String> listTypesAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, commonNamesList);
        listTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        commonNamesSpin.setAdapter(listTypesAdapter);

        commonNamesSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                setNames(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        setMode();
        return v;
    }

    private void setUpCommonNamesList()
    {
        commonNamesList = new ArrayList<>();

        for (CommonName name : appService.getCommonNames()) {
            commonNamesList.add(name.getName());
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.convertToDiameterBox && convertToDiameterBox.isChecked()) {
            trunk_diameterTxt.setText(convertToDiameter(trunk_diameterTxt.getText().toString()));
        }
    }

    private String convertToDiameter(String value)
    {
        return Double.toString(Math.round(Double.parseDouble(value) / Math.PI));
    }

    private void setNames(Integer pos)
    {
        CommonName commonName = appService.getCommonNames().get(pos);
        ScientificName scientificName = appService.getScientificNames().get(commonName.getScientificNameId() - 1);
        commonNamesSpin.setSelection(commonName.getId() - 1);
        scientific_nameTxt.setText(scientificName.getName());
        AppService.getEditableRecord().setCommonName(commonName.getId());
    }

    //Выводит информацию в форму
    private void showTreeInfoInForm() {
        Record record = appService.getRecord();

        if (record.getCommonName() != null) {
            setNames(record.getCommonName() - 1);
        }

        else
        {
            setNames(0);
        }

        SetterField.setTextField(record.getTrunkDiameter(), trunk_diameterTxt);

        SetterField.setTextField(record.getHeight(), tree_heightTxt);

        SetterField.setTextField(record.getSkeletalBranchesNumber(), skeletal_branchesTxt);

        SetterField.setTextField(record.getCondition(), conditionTxt);
    }

    //Устанавливает режим работы
    private void setMode() {
        //show mode
        if (!appService.isAddMode())
        {
                //Запрет на изменение полей
                scientific_nameTxt.setKeyListener(null);
                trunk_diameterTxt.setKeyListener(null);
                tree_heightTxt.setKeyListener(null);
                skeletal_branchesTxt.setKeyListener(null);
                conditionTxt.setKeyListener(null);
                convertToDiameterBox.setVisibility(View.GONE);
                commonNamesSpin.setEnabled(false);
        }

        showTreeInfoInForm();
    }
}