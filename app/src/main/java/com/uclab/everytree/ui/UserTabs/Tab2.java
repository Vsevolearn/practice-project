package com.uclab.everytree.ui.UserTabs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uclab.everytree.R;
import com.uclab.everytree.adapters.ImageGalleryAdapter;
import com.uclab.everytree.models.LoadingDialog;
import com.uclab.everytree.models.serializers.Photo;
import com.uclab.everytree.services.NetworkService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tab2  extends Fragment implements View.OnClickListener
{
    private LoadingDialog progressDialog = null;
    private Context mContext;
    private static final String TAG = Tab2.class.getSimpleName();
    private List<Photo> downloadedPhotos = new ArrayList<>();
    private ImageGalleryAdapter adapter;

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public Tab2() {
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
        View v = inflater.inflate(R.layout.user_fragment_tab2, container, false);
        getRecordPhotos();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        RecyclerView recyclerView = v.findViewById(R.id.imagesView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ImageGalleryAdapter(mContext, downloadedPhotos);
        recyclerView.setAdapter(adapter);
        return v;
    }

    private void getRecordPhotos()
    {
        progressDialog.show();

        NetworkService.getInstance(mContext)
                .getEveryTreeAPI()
                .getUserPhotos()
                .enqueue(new Callback<List<Photo>>() {
                    @Override
                    public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                        progressDialog.hide();

                        if (response.body() != null)
                        {
                            downloadedPhotos = response.body();
                            adapter.updatePhotoList(downloadedPhotos);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Photo>>  call, Throwable t) {
                        System.out.println(t.getMessage());
                        progressDialog.hide();
                        Toast.makeText(mContext, "We've got an error when was getting a photos list!", Toast.LENGTH_LONG).show();
                    }});
    }
}
