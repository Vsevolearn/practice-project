package com.uclab.everytree.ui;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.uclab.everytree.R;
import com.uclab.everytree.models.serializers.Photo;

public class ImageDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PHOTO = "PhotoActivity.PHOTO";
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        mImageView = findViewById(R.id.image);
        Photo photo = getIntent().getParcelableExtra(EXTRA_PHOTO);

        Glide.with(this)
                .asBitmap()
                .load(photo.getUrl())
                .error(R.drawable.ic_cloud_off_black_24dp)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(mImageView);
    }
}
