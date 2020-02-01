package com.test.mark;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomepageAdapter extends RecyclerView.Adapter {

    private List<HomepageModel> homepageModelList;

    public HomepageAdapter(List<HomepageModel> homepageModelList) {
        this.homepageModelList = homepageModelList;
    }

    @Override
    public int getItemViewType(int position) {

        switch (homepageModelList.get(position).getType()) {

            case 0:
                return HomepageModel.CATEGORY_ITEMS;

            case 1:
                return HomepageModel.HORIZONTAL_TRENDING_ITEMS;

            default:
                return  -1;

        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case HomepageModel.CATEGORY_ITEMS:
                View categoryView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bottom_sheet_category_item_layout, viewGroup, false);
                return new CategoryItemsViewHolder(categoryView);

            case HomepageModel.HORIZONTAL_TRENDING_ITEMS:
                View trendingView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bottom_sheet_trending_item_layout, viewGroup, false);
                return new TrendingItemsViewHolder(trendingView);

            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (homepageModelList.get(position).getType()) {

            case HomepageModel.CATEGORY_ITEMS:
                List<HorizontalCategoryScrollModel> horizontalCategoryScrollModelList = homepageModelList.get(position).getHorizontalCategoryScrollModelList();
                ((CategoryItemsViewHolder)holder).setCategoryItemLayout(horizontalCategoryScrollModelList);
                break;

            case HomepageModel.HORIZONTAL_TRENDING_ITEMS:
                List<HorizontalScrollTrendingModel> horizontalScrollTrendingModelList = homepageModelList.get(position).getHorizontalScrollTrendingModelList();
                ((TrendingItemsViewHolder)holder).setTrendingItemLayout(horizontalScrollTrendingModelList);
                break;

            default:
                return;
        }
    }

    @Override
    public int getItemCount() {
        return homepageModelList.size();
    }


    public class CategoryItemsViewHolder extends RecyclerView.ViewHolder{

        private TextView categoryItemTitle;
        private CircleImageView categoryImage;
        private RecyclerView horizontalCategoryRecyclerView;

        public CategoryItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryItemTitle = itemView.findViewById(R.id.category_name);
            categoryImage = itemView.findViewById(R.id.category_icon);
            horizontalCategoryRecyclerView = itemView.findViewById(R.id.categories_recyclerView);
        }

        private void setCategoryItemLayout(List<HorizontalCategoryScrollModel> horizontalCategoryScrollModelList)
        {
            HorizontalCategoryScrollAdapter horizontalCategoryScrollAdapter = new HorizontalCategoryScrollAdapter(horizontalCategoryScrollModelList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);

            horizontalCategoryRecyclerView.setLayoutManager(linearLayoutManager);
            horizontalCategoryRecyclerView.setAdapter(horizontalCategoryScrollAdapter);
            horizontalCategoryScrollAdapter.notifyDataSetChanged();
        }
    }

    public class TrendingItemsViewHolder extends RecyclerView.ViewHolder{

        private TextView trendingPlaceName, trendingPlaceDesc, distance;
        private ImageView trendingPlaceImage;
        private RecyclerView horizontalTrendingRecyclerView;

        public TrendingItemsViewHolder(@NonNull View itemView) {
            super(itemView);

            trendingPlaceName = itemView.findViewById(R.id.trending_place_name);
            trendingPlaceDesc = itemView.findViewById(R.id.trending_description);
            distance = itemView.findViewById(R.id.trending_distance);
            trendingPlaceImage = itemView.findViewById(R.id.trending_image);
            horizontalTrendingRecyclerView = itemView.findViewById(R.id.trending_recyclerView);;
        }

        private void setTrendingItemLayout(List<HorizontalScrollTrendingModel> horizontalScrollTrendingModelList)
        {
            HorizontalScrollTrendingAdapter horizontalScrollTrendingAdapter = new HorizontalScrollTrendingAdapter(horizontalScrollTrendingModelList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);

            horizontalTrendingRecyclerView.setLayoutManager(linearLayoutManager);
            horizontalTrendingRecyclerView.setAdapter(horizontalScrollTrendingAdapter);
            horizontalScrollTrendingAdapter.notifyDataSetChanged();
        }
    }
}
