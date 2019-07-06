package com.uclab.everytree.models;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.uclab.everytree.R;
import com.uclab.everytree.services.AppService;

public class GenericTextWatcher implements TextWatcher {
    private View view;

    public GenericTextWatcher(View view) {
        this.view = view;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    public void afterTextChanged(Editable editable) {
        String text = editable.toString();

        if (text.equals(""))
        {
            return;
        }

        switch(view.getId()){
            case R.id.trunk_diameterTxt:
                AppService.getEditableRecord().setTrunkDiameter(Double.parseDouble(text));
                break;
            case R.id.tree_heightTxt:
                AppService.getEditableRecord().setHeight(Double.parseDouble(text));
                break;
            case R.id.skeletal_branchesTxt:
                AppService.getEditableRecord().setSkeletalBranchesNumber(Integer.parseInt(text));
                break;
            case R.id.conditionTxt:
                AppService.getEditableRecord().setCondition(text);
                break;
            case R.id.nearestAddressTxt:
                AppService.getEditableRecord().setNearestAddress(text);
                break;
        }
    }
}
