package com.test.mark;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalScrollTrendingAdapter extends RecyclerView.Adapter<VerticalScrollTrendingAdapter.ViewHolder> {

    private List<HorizontalScrollTrendingModel> horizontalScrollTrendingModelList;

    public VerticalScrollTrendingAdapter(List<HorizontalScrollTrendingModel> horizontalScrollTrendingModelList) {
        this.horizontalScrollTrendingModelList = horizontalScrollTrendingModelList;
    }

    @NonNull
    @Override
    public VerticalScrollTrendingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tending_activity_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalScrollTrendingAdapter.ViewHolder holder, int position) {

        int imageResource = horizontalScrollTrendingModelList.get(position).getTrendingImage();
        String placeName = horizontalScrollTrendingModelList.get(position).getName();
        String placeDesc = horizontalScrollTrendingModelList.get(position).getDescription();
        String placeDist = horizontalScrollTrendingModelList.get(position).getDistance();

        holder.setTrendingImage(imageResource);
        holder.setTrendingPlaceName(placeName);
        holder.setTrendingPlaceDesc(placeDesc);
        holder.setTrendingPlaceDist(placeDist);

    }

    @Override
    public int getItemCount() {
        return horizontalScrollTrendingModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView trendingImage;
        private TextView trendingPlaceName, trendingPlaceDesc, trendingPlaceDist;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            trendingImage = itemView.findViewById(R.id.trending_image);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                trendingImage.setClipToOutline(true);
            }
            trendingPlaceName = itemView.findViewById(R.id.event_name);
            trendingPlaceDesc = itemView.findViewById(R.id.eventDescription);
            trendingPlaceDist = itemView.findViewById(R.id.eventDistance);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent selectedPlace = new Intent(itemView.getContext(), PlaceActivity.class);
                    itemView.getContext().startActivity(selectedPlace);
                }
            });
        }

        private void setTrendingImage(int resource)
        {
            trendingImage.setImageResource(resource);
        }

        private void setTrendingPlaceName(String name)
        {
            trendingPlaceName.setText(name);
        }

        private void setTrendingPlaceDesc(String desc)
        {
            trendingPlaceDesc.setText(desc);
        }

        private void setTrendingPlaceDist(String dist)
        {
            trendingPlaceDist.setText(dist);
        }
    }
}
