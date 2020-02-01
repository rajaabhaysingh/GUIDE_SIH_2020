package com.test.mark;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlacesPictureAdapter extends RecyclerView.Adapter<PlacesPictureAdapter.ViewHolder> {

    private List<PlacesPictureModel> placesPictureModelList;

    public PlacesPictureAdapter(List<PlacesPictureModel> placesPictureModelList) {
        this.placesPictureModelList = placesPictureModelList;
    }

    @NonNull
    @Override
    public PlacesPictureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.places_picture_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesPictureAdapter.ViewHolder holder, int position) {

        int imageResource = placesPictureModelList.get(position).getImageResource();

        holder.setPlacePicture(imageResource);
    }

    @Override
    public int getItemCount() {
        return placesPictureModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView placePicture;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            placePicture = itemView.findViewById(R.id.places_image);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                placePicture.setClipToOutline(true);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent selectedCategoryIntent = new Intent(itemView.getContext(), ImageViewActivity.class);
                    itemView.getContext().startActivity(selectedCategoryIntent);
                }
            });
        }

        private void setPlacePicture (int resource)
        {
            placePicture.setImageResource(resource);
        }
    }
}
