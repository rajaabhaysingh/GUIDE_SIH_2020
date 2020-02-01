package com.test.mark;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class HorizontalCategoryScrollAdapter extends RecyclerView.Adapter<HorizontalCategoryScrollAdapter.ViewHolder> {

    private List<HorizontalCategoryScrollModel> horizontalCategoryScrollModelList;

    public HorizontalCategoryScrollAdapter(List<HorizontalCategoryScrollModel> horizontalCategoryScrollModelList) {
        this.horizontalCategoryScrollModelList = horizontalCategoryScrollModelList;
    }

    @NonNull
    @Override
    public HorizontalCategoryScrollAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_sheet_category_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalCategoryScrollAdapter.ViewHolder viewHolder, int position) {

        int imageResource = horizontalCategoryScrollModelList.get(position).getImageResource();
        String categoryName = horizontalCategoryScrollModelList.get(position).getCategoryName();

        viewHolder.setCategoryIcon(imageResource);
        viewHolder.setCategoryName(categoryName);
    }

    @Override
    public int getItemCount() {
        return horizontalCategoryScrollModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView categoryIcon;
        private TextView categoryName;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            categoryIcon = itemView.findViewById(R.id.category_icon);
            categoryName = itemView.findViewById(R.id.category_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent selectedCategoryIntent = new Intent(itemView.getContext(), SearchCategoryActivity.class);
                    itemView.getContext().startActivity(selectedCategoryIntent);
                }
            });
        }

        private void setCategoryIcon (int resource)
        {
            categoryIcon.setImageResource(resource);
        }

        private void setCategoryName (String name)
        {
            categoryName.setText(name);
        }
    }
}
