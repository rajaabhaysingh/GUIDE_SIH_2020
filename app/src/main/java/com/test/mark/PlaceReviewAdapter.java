package com.test.mark;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlaceReviewAdapter extends RecyclerView.Adapter<PlaceReviewAdapter.ViewHolder> {

    List<PlaceReviewModel> placeReviewModelList;

    public PlaceReviewAdapter(List<PlaceReviewModel> placeReviewModelList) {
        this.placeReviewModelList = placeReviewModelList;
    }

    @NonNull
    @Override
    public PlaceReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_review_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceReviewAdapter.ViewHolder holder, int position) {

        int star_1 = placeReviewModelList.get(position).getStar1();
        int star_2 = placeReviewModelList.get(position).getStar2();
        int star_3 = placeReviewModelList.get(position).getStar3();
        int star_4 = placeReviewModelList.get(position).getStar4();
        int star_5 = placeReviewModelList.get(position).getStar5();

        String userName = placeReviewModelList.get(position).getUserName();
        String userReview = placeReviewModelList.get(position).getUserReview();
        String reviewDate = placeReviewModelList.get(position).getReviewDate();

        holder.setStar1(star_1);
        holder.setStar2(star_2);
        holder.setStar3(star_3);
        holder.setStar4(star_4);
        holder.setStar5(star_5);
        holder.setUserName(userName);
        holder.setUserReview(userReview);
        holder.setReviewDate(reviewDate);
    }

    @Override
    public int getItemCount() {
        return placeReviewModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView star1, star2, star3, star4, star5;
        private TextView userName, userReview, reviewDate, userUpvote;
        private ImageButton optionButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            star1 = itemView.findViewById(R.id.user_review_star_1);
            star2 = itemView.findViewById(R.id.user_review_star_2);
            star3 = itemView.findViewById(R.id.user_review_star_3);
            star4 = itemView.findViewById(R.id.user_review_star_4);
            star5 = itemView.findViewById(R.id.user_review_star_5);

            userName = itemView.findViewById(R.id.username_review);
            userReview = itemView.findViewById(R.id.user_review_content);
            reviewDate = itemView.findViewById(R.id.user_review_date);

            userUpvote = itemView.findViewById(R.id.user_review_upvote_txtView);

            optionButton = itemView.findViewById(R.id.user_option_image_button);

            optionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: option button code goes here
                }
            });

            userUpvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: upvote code goes here
                }
            });
        }

        private void setUserName(String name) {
            userName.setText(name);
        }

        private void setUserReview(String review) {
            userReview.setText(review);
        }

        private void setReviewDate(String date) {
            reviewDate.setText(date);
        }

        private void setStar1(int star_1) {
            star1.setImageResource(star_1);
        }
        private void setStar2(int star_2) {
            star2.setImageResource(star_2);
        }
        private void setStar3(int star_3) {
            star3.setImageResource(star_3);
        }
        private void setStar4(int star_4) {
            star4.setImageResource(star_4);
        }
        private void setStar5(int star_5) {
            star5.setImageResource(star_5);
        }

    }
}
