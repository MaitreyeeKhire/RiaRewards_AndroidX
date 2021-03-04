package com.pegasusgroup.riarewards.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.model.ReviewMd;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ReviewMd> reviewMds;

    public ReviewAdapter(Context context, ArrayList<ReviewMd> reviewMds) {
        mContext = context;
        this.reviewMds = reviewMds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.setData(reviewMds.get(position));
    }

    @Override
    public int getItemCount() {
        return reviewMds.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView txtUserName;
        private AppCompatRatingBar ratingBar;
        private AppCompatTextView txtReview;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            txtReview = itemView.findViewById(R.id.txtReview);
        }

        void setData(ReviewMd reviewMd) {
            txtUserName.setText(reviewMd.getCommentedBy());
            txtReview.setText(reviewMd.getComment());
            ratingBar.setRating(Float.parseFloat(reviewMd.getRating()));
        }
    }
}