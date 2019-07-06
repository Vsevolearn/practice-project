package com.uclab.everytree.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.uclab.everytree.R;
import com.uclab.everytree.models.serializers.auth.Authorization;
import com.uclab.everytree.models.LoadingDialog;
import com.uclab.everytree.models.serializers.auth.User;
import com.uclab.everytree.services.AuthService;
import com.uclab.everytree.services.NetworkService;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
	private static final String TAG = LoginActivity.class.getSimpleName();
    private LoadingDialog progressDialog = null;
	private Button signInBtn, signUpBtn, createAccountBtn;
	private EditText loginTxt, passwordTxt, pwdTxt, confirmPwdTxt, emailTxt, firstNameTxt, lastNameTxt, userNameTxt;
    private AuthService service;
    private LinearLayout signedTxtLayout, unsignedTxtLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

        progressDialog = new LoadingDialog(this);
        service = new AuthService(this);

        signInBtn = findViewById(R.id.signInBtn);
        signUpBtn =  findViewById(R.id.signUpBtn);
        createAccountBtn = findViewById(R.id.createAccountBtn);

        loginTxt =  findViewById(R.id.loginTxt);
        passwordTxt =  findViewById(R.id.passwordTxt);
        pwdTxt = findViewById(R.id.pwdTxt);
        confirmPwdTxt = findViewById(R.id.confirmPwdTxt);
        firstNameTxt = findViewById(R.id.firstNameTxt);
        lastNameTxt = findViewById(R.id.lastNameTxt);
        emailTxt = findViewById(R.id.emailTxt);
        userNameTxt = findViewById(R.id.userNameTxt);

        signedTxtLayout = findViewById(R.id.signedTxtLayout);
        unsignedTxtLayout = findViewById(R.id.unsignedTxtLayout);

        signInBtn.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
        createAccountBtn.setOnClickListener(this);
	}

	private void signIn() {
        progressDialog.show();

        User user = new User();

        if (userNameTxt.getText() != null && !userNameTxt.getText().toString().isEmpty()) {
            user.setUserName(userNameTxt.getText().toString());
        }
        else if (loginTxt.getText() != null && !loginTxt.getText().toString().isEmpty())
        {
            user.setUserName(loginTxt.getText().toString());
        }

        if (passwordTxt.getText() != null && !passwordTxt.getText().toString().isEmpty()) {
            user.setPassword(passwordTxt.getText().toString());
        }
        else if (confirmPwdTxt.getText() != null && !confirmPwdTxt.getText().toString().isEmpty())
        {
            user.setPassword(confirmPwdTxt.getText().toString());
        }

        NetworkService.getInstance(this)
                .getEveryTreeAPI()
                .signIn(user)
                .enqueue(new Callback<Authorization>() {
                    @Override
                    public void onResponse(Call<Authorization> call, Response<Authorization> response) {
                        progressDialog.hide();

                        if (response.body() != null) {
                            Authorization auth = response.body();

                            service.setIsAuthorized(true);
                            service.setAccessToken(auth.getAccessToken());
                            service.setRefreshToken(auth.getRefreshToken());

                            redirectToUserActivity();
                        }
                    }

                    @Override
                    public void onFailure(Call<Authorization> call, Throwable t) {
                        System.out.println(t.getMessage());
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), "We've got an error when Sign In ", Toast.LENGTH_LONG).show();
                    }
                });
	}

	private void redirectToUserActivity()
    {
        Intent intent = new Intent(getApplicationContext(), UserActivity.class);
        startActivity(intent);
        finish();
    }

    private void signUp() {
        progressDialog.show();

        User user = new User();
        
        user.setFirstName(firstNameTxt.getText().toString());
        user.setLastName(lastNameTxt.getText().toString());
        user.setUserName(userNameTxt.getText().toString());
        user.setEmail(emailTxt.getText().toString());
        user.setPassword(confirmPwdTxt.getText().toString());

        NetworkService.getInstance(this)
                .getEveryTreeAPI()
                .signUp(user)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressDialog.hide();
                        signIn();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        System.out.println(t.getMessage());
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), "We've got an error when Sign Up", Toast.LENGTH_LONG).show();
                    }
                });
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.signInBtn:
				signIn();
				break;

			case R.id.signUpBtn:
			    unsignedTxtLayout.setVisibility(View.GONE);
                signedTxtLayout.setVisibility(View.VISIBLE);
                signInBtn.setVisibility(View.GONE);
                signUpBtn.setVisibility(View.GONE);
                createAccountBtn.setVisibility(View.VISIBLE);
				break;

            case R.id.createAccountBtn:
                signUp();
                break;
		}
	}
}