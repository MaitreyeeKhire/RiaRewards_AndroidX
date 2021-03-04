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
import com.pegasusgroup.riarewards.model.TransactionHistoryMd;
import com.pegasusgroup.riarewards.utils.CommonMethods;

import java.util.ArrayList;

public class LastTransactionAdapter extends RecyclerView.Adapter<LastTransactionAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<TransactionHistoryMd> transactionHistoryMds;

    public LastTransactionAdapter(Context context, ArrayList<TransactionHistoryMd> transactionHistoryMds) {
        mContext = context;
        this.transactionHistoryMds = transactionHistoryMds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.last_transaction_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.setData(transactionHistoryMds.get(position));
    }

    @Override
    public int getItemCount() {
        return transactionHistoryMds.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView txtTransactionId;
        private AppCompatTextView txtTransactionDate;
        private AppCompatTextView txtComment;
        private AppCompatRatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTransactionId = itemView.findViewById(R.id.txtTransactionId);
            txtTransactionDate = itemView.findViewById(R.id.txtTransactionDate);
            txtComment = itemView.findViewById(R.id.txtComment);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }

        public void setData(TransactionHistoryMd transactionHistoryMd) {
            txtTransactionId.setText(transactionHistoryMd.getTransaction_id());
            txtTransactionDate.setText(CommonMethods.getFormattedDate(transactionHistoryMd.getCreated()));
            txtComment.setText(transactionHistoryMd.getComment());
            ratingBar.setRating(Float.parseFloat(transactionHistoryMd.getRating()));
        }
    }
}