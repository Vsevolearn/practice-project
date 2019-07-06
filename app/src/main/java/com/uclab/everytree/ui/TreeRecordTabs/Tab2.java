package com.uclab.everytree.ui.TreeRecordTabs;

        import android.app.DatePickerDialog;
        import android.content.Context;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.DatePicker;
        import android.widget.Spinner;
        import android.widget.Toast;

        import com.uclab.everytree.R;
        import com.uclab.everytree.models.LoadingDialog;
        import com.uclab.everytree.models.SetterField;
        import com.uclab.everytree.models.serializers.UserRecord;
        import com.uclab.everytree.services.AppConfig;
        import com.uclab.everytree.services.AppService;
        import com.uclab.everytree.services.NetworkService;

        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.List;
        import java.util.Locale;

        import androidx.annotation.NonNull;
        import androidx.fragment.app.Fragment;

        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;

public class Tab2 extends Fragment{
    private Button input_dateTxt, plant_dateTxt, remove_dateTxt;
    private Calendar calendar = Calendar.getInstance();
    private Spinner revisionListSpin;
    private static final String TAG = Tab2.class.getSimpleName();
    private AppService appService;
    private LoadingDialog progressDialog = null;
    private Context mContext;

    public Tab2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new LoadingDialog(mContext);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab2, container, false);

        appService = new AppService(mContext);

        input_dateTxt = v.findViewById(R.id.input_dateTxt);

        plant_dateTxt = v.findViewById(R.id.plant_dateTxt);

        remove_dateTxt = v.findViewById(R.id.remove_dateTxt);

        revisionListSpin = v.findViewById(R.id.listChangesSpin);

        plant_dateTxt.setOnClickListener(new DateOnClickListener(plant_dateTxt));
        remove_dateTxt.setOnClickListener(new DateOnClickListener(remove_dateTxt));

        setMode();
        return v;
    }

    //Date listener
    class DateOnClickListener implements View.OnClickListener {
        private Button dateTxt;

        DateOnClickListener(Button _dateTxt)
        {
            dateTxt = _dateTxt;
        }

        private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel(dateTxt);
            }
        };

        @Override
        public void onClick(View v) {
            new DatePickerDialog(mContext, date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        }

        //Устанавливает дату
        private void updateDateLabel(Button dateText) {
            switch (dateText.getId()){
                case R.id.plant_dateTxt:
                    if (validateDate(calendar.getTime(), AppService.getEditableRecord().getDateRemoved()))
                    {
                        AppService.getEditableRecord().setDatePlanted(calendar.getTime());
                        dateText.setText(new SimpleDateFormat(AppConfig.getDateFormat(), Locale.getDefault()).format(calendar.getTime()));
                    }
                    break;

                case R.id.remove_dateTxt:
                    if (validateDate(AppService.getEditableRecord().getDatePlanted(), calendar.getTime()))
                    {
                        AppService.getEditableRecord().setDateRemoved(calendar.getTime());
                        dateText.setText(new SimpleDateFormat(AppConfig.getDateFormat(), Locale.getDefault()).format(calendar.getTime()));
                    }
                    break;
            }
        }
    }

    //if current date is greater than after date return false
    private boolean validateDate(Date currentDate, Date afterDate)
    {
        if (afterDate == null || currentDate == null)
        {
            return true;
        }

        if (currentDate.after(afterDate))
        {
            Toast.makeText(mContext, "Date planted cannot be more than date removed!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    //Выводит информацию в форму
    private void showTreeInfoInForm() {
        if (appService.getRecord() != null) {
            SetterField.setDateField(appService.getRecord().getDatePlanted(), plant_dateTxt);
            SetterField.setDateField(appService.getRecord().getDateRemoved(), remove_dateTxt);
        }

        getUsersForRecord();
    }

    private void setRevisionList(List<String> list)
    {
        if (list != null) {
            String[] revisionList = list.toArray(new String[0]);
            ArrayAdapter<String> revisionListAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, revisionList);
            revisionListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            revisionListSpin.setAdapter(revisionListAdapter);
        }
    }

    //Устанавливает режим работы
    private void setMode()
    {
        //show mode
        if (!appService.isAddMode())
        {
            //Запрет на изменение полей
            plant_dateTxt.setOnClickListener(null);
            remove_dateTxt.setOnClickListener(null);

            SetterField.setDateField(appService.getRecord().getDateInput(), input_dateTxt);
        }

        //edit, add mode
        else
        {
            Date currentDate = new Date();
            input_dateTxt.setText(new SimpleDateFormat(AppConfig.getDateFormat(), Locale.getDefault()).format(currentDate));
            AppService.getEditableRecord().setDateInput(currentDate);
        }

        showTreeInfoInForm();
    }

    private void getUsersForRecord()
    {
        if (appService.getTree() == null)
        {
            return;
        }

        NetworkService.getInstance(mContext)
                .getEveryTreeAPI()
                .getRecordUsers(appService.getTree().getId())
                .enqueue(new Callback<List<UserRecord>>() {
                    @Override
                    public void onResponse(Call<List<UserRecord>> call, Response<List<UserRecord>> response) {
                        if (response.body() != null)
                        {
                            List<String> usersRecord = new ArrayList<>();

                            for(UserRecord user: response.body())
                            {
                                usersRecord.add(user.getUsername() + ": " + new SimpleDateFormat(AppConfig.getDateFormat(), Locale.getDefault()).format(user.getDateInput()));
                            }

                            setRevisionList(usersRecord);
                        }

                        progressDialog.hide();
                    }

                    @Override
                    public void onFailure(Call<List<UserRecord>>  call, Throwable t) {
                        System.out.println(t.getMessage());
                        progressDialog.hide();
                        Toast.makeText(mContext, "We've got an error when was getting a revision list!", Toast.LENGTH_LONG).show();
                    }});
    }
}