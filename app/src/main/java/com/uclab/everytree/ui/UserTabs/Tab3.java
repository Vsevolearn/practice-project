package com.uclab.everytree.ui.UserTabs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.uclab.everytree.R;
import com.uclab.everytree.models.LoadingDialog;
import com.uclab.everytree.models.serializers.UserScore;
import com.uclab.everytree.services.NetworkService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tab3 extends Fragment implements View.OnClickListener {
    private static final String TAG = Tab3.class.getSimpleName();
    private Context mContext;
    private LoadingDialog progressDialog = null;
    private TextView favoriteTreesTxt;

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public Tab3() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new LoadingDialog(mContext);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.user_fragment_tab3, container, false);
        favoriteTreesTxt = v.findViewById(R.id.favoriteTreesTxt);
        getUserScore();

        return v;
    }

    private void showStatsInForm(List<UserScore> scores)
    {
        StringBuilder text = new StringBuilder();

        for (UserScore score : scores)
        {
            text.append(score.getName()).append(": ").append(score.getTotalRecords()).append("\n");
        }

        favoriteTreesTxt.setText(text);
    }

    private void getUserScore()
    {
        progressDialog.show();

        NetworkService.getInstance(mContext)
                .getEveryTreeAPI()
                .getUserScore()
                .enqueue(new Callback<List<UserScore>>() {
                    @Override
                    public void onResponse(Call<List<UserScore>> call, Response<List<UserScore>> response) {
                        progressDialog.hide();

                        if (response.body() != null)
                        {
                            showStatsInForm(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserScore>>  call, Throwable t) {
                        System.out.println(t.getMessage());
                        progressDialog.hide();
                        Toast.makeText(mContext, "We've got an error when was getting a user score list!", Toast.LENGTH_LONG).show();
                    }});
    }
}
