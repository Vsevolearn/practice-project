package com.uclab.everytree.ui.TreeRecordTabs;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.uclab.everytree.R;
import com.uclab.everytree.adapters.ImageGalleryAdapter;
import com.uclab.everytree.models.LoadingDialog;
import com.uclab.everytree.models.serializers.Photo;
import com.uclab.everytree.services.AppConfig;
import com.uclab.everytree.services.AppService;
import com.uclab.everytree.services.NetworkService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.appcompat.app.AppCompatActivity.RESULT_OK;

public class Tab4 extends Fragment implements View.OnClickListener {
    private static final String TAG = Tab4.class.getSimpleName();
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_GALLERY = 2;
    private Button photoBtn, fromGalleryBtn;
    private String mCurrentPhotoPath = null; //имя только что отснятого фото
    private AppService appService;
    private List<Photo> photosToUpload = new ArrayList<>();
    private LoadingDialog progressDialog = null;
    private Context mContext;
    private List<Photo> downloadedPhotos = new ArrayList<>();
    private ImageGalleryAdapter adapter;

    public Tab4() {
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

        progressDialog = new LoadingDialog(mContext);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab4, container, false);

        appService = new AppService(mContext);

        photoBtn = v.findViewById(R.id.photoBtn);
        fromGalleryBtn = v.findViewById(R.id.fromGalleryBtn);

        photoBtn.setOnClickListener(this);
        fromGalleryBtn.setOnClickListener(this);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        RecyclerView recyclerView = v.findViewById(R.id.imagesView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ImageGalleryAdapter(mContext, downloadedPhotos);
        recyclerView.setAdapter(adapter);

        setMode();

        getRecordPhotos();
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.photoBtn):
                if (photosToUpload.size() <= AppConfig.getMaxCountPhotos()) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(mContext, "You can't upload more than 10 photos at once!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.fromGalleryBtn:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_GALLERY);
                break;
        }
    }

    //Calling camera intent
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(mContext, "Can't create a file!", Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mContext,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void getRecordPhotos()
    {
        if (appService.getTree() == null)
        {
              return;
        }

        progressDialog.show();

        NetworkService.getInstance(mContext)
                .getEveryTreeAPI()
                .getRecordPhotos(appService.getTree().getId())
                .enqueue(new Callback<List<Photo>>() {
                    @Override
                    public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                        if (response.body() != null)
                        {
                            downloadedPhotos = response.body();
                            adapter.updatePhotoList(downloadedPhotos);
                        }

                        progressDialog.hide();
                    }

                    @Override
                    public void onFailure(Call<List<Photo>>  call, Throwable t) {
                        System.out.println(t.getMessage());
                        progressDialog.hide();
                        Toast.makeText(mContext, "We've got an error when was getting a revision list!", Toast.LENGTH_LONG).show();
                    }});
    }


    private void setPicFromStorage() {
        Photo photo = new Photo(mCurrentPhotoPath);
        photosToUpload.add(photo);
        downloadedPhotos.add(photo);
        adapter.updatePhotoList(downloadedPhotos);
        appService.setPhotosToUpload(photosToUpload);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPicFromStorage();
        } else if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            mCurrentPhotoPath = getRealPathFromURI(selectedImage);
            setPicFromStorage();
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String path = null;
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = mContext.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor!=null && cursor.getCount()>0 && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
            cursor.close();
        }
        return path;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat(AppConfig.getDateFormat(), Locale.getDefault()).format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Устанавливает режим работы
    private void setMode() {
        //show mode
        if (!appService.isAddMode()) {
            photoBtn.setVisibility(View.GONE);
            fromGalleryBtn.setVisibility(View.GONE);
        }
    }
}