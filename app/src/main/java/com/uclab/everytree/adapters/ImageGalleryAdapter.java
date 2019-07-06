package com.uclab.everytree.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uclab.everytree.R;
import com.uclab.everytree.models.serializers.Photo;
import com.uclab.everytree.ui.ImageDetailActivity;

import java.util.List;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>  {

    @NonNull
    @Override
    public ImageGalleryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.image_layout, parent, false);
        return new ImageGalleryAdapter.MyViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageGalleryAdapter.MyViewHolder holder, int position) {

        Photo photo = mPhotos.get(position);
        ImageView imageView = holder.mPhotoImageView;

        Glide.with(mContext)
                .load(photo.getUrl())
                .placeholder(R.drawable.ic_cloud_off_black_24dp)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mPhotoImageView;

        private MyViewHolder(View itemView) {

            super(itemView);
            mPhotoImageView = itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Photo photo = mPhotos.get(position);
                Intent intent = new Intent(mContext, ImageDetailActivity.class);
                intent.putExtra(ImageDetailActivity.EXTRA_PHOTO, photo);
                mContext.startActivity(intent);
            }
        }
    }

    private List<Photo> mPhotos;
    private Context mContext;

    public void updatePhotoList (List<Photo> items) {
        if (items != null && items.size() > 0) {
            mPhotos.clear();
            mPhotos.addAll(items);
            notifyDataSetChanged();
        }
    }

    public ImageGalleryAdapter(Context context, List<Photo> photos) {
        mContext = context;
        mPhotos = photos;
    }
}