package com.uclab.everytree.ui.UserTabs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.uclab.everytree.R;
import com.uclab.everytree.models.LoadingDialog;
import com.uclab.everytree.models.SetterField;
import com.uclab.everytree.models.serializers.auth.User;
import com.uclab.everytree.services.AuthService;
import com.uclab.everytree.services.NetworkService;
import com.uclab.everytree.ui.MainActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tab1 extends Fragment implements View.OnClickListener {
    private static final String TAG = Tab1.class.getSimpleName();
    private Button signOutBtn;
    private AuthService authService;
    private LoadingDialog progressDialog = null;
    private EditText emailTxt, firstNameTxt, lastNameTxt, userNameTxt;
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

        authService = new AuthService(mContext);
        progressDialog = new LoadingDialog(mContext);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.user_fragment_tab1, container, false);

        signOutBtn = v.findViewById(R.id.signOutBtn);
        firstNameTxt = v.findViewById(R.id.firstNameTxt);
        lastNameTxt = v.findViewById(R.id.lastNameTxt);
        emailTxt = v.findViewById(R.id.emailTxt);
        userNameTxt = v.findViewById(R.id.userNameTxt);

        signOutBtn.setOnClickListener(this);

        getUser();

        return v;
    }

    private void showUserInForm()
    {
        SetterField.setTextField(authService.getUser().getFirstName(), firstNameTxt);
        SetterField.setTextField(authService.getUser().getLastName(), lastNameTxt);
        SetterField.setTextField(authService.getUser().getEmail(), emailTxt);
        SetterField.setTextField(authService.getUser().getUsername(), userNameTxt);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.signOutBtn:
                redirectToMainActivity();
                break;
        }
    }

    private void redirectToMainActivity()
    {
            authService.Clear();
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
    }

    private void getUser()
    {
        progressDialog.show();

        NetworkService.getInstance(mContext)
                .getEveryTreeAPI()
                .getUser()
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        progressDialog.hide();

                        if (response.isSuccessful() && response.body() != null) {
                            authService.setUser(response.body());
                            showUserInForm();
                        }

                        else
                        {
                            redirectToMainActivity();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        System.out.println(t.getMessage());
                        progressDialog.hide();
                        Toast.makeText(mContext, "We've got an error when was getting user", Toast.LENGTH_LONG).show();
                        redirectToMainActivity();
                    }
                });
    }
}