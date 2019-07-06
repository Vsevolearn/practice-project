package com.uclab.everytree.models;

import android.widget.Button;
import android.widget.EditText;

import com.uclab.everytree.services.AppConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SetterField {
    public static void setTextField(Object obj, EditText field)
    {
        if (obj != null)
        {
            field.setText(obj.toString());
        }
    }

    public static void setDateField(Date value, Button field)
    {
        if (value != null) {
            field.setText(new SimpleDateFormat(AppConfig.getDateFormat(), Locale.getDefault()).format(value));
        }
    }
}
