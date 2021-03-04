package com.pegasusgroup.riarewards.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.model.PointMd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

@SuppressLint("SimpleDateFormat")
public class PointAdapter extends RecyclerView.Adapter<PointAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<PointMd> pointMds;
    private SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat appFormat = new SimpleDateFormat("dd/MM/yyyy");

    public PointAdapter(Context context, ArrayList<PointMd> pointMds) {
        mContext = context;
        this.pointMds = pointMds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.point_history_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.setData(pointMds.get(position));
    }

    @Override
    public int getItemCount() {
        return pointMds.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView txtDate;
        private AppCompatTextView txtPointsAwarded;
        private AppCompatTextView txtReason;
        private AppCompatTextView txtPointBalance;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtPointsAwarded = itemView.findViewById(R.id.txtPointsAwarded);
            txtReason = itemView.findViewById(R.id.txtReason);
            txtPointBalance = itemView.findViewById(R.id.txtPointBalance);
        }

        void setData(PointMd pointMd) {
            try {
                //txtDate.setText(appFormat.format(serverFormat.parse(pointMd.getTransaction_date())));
                txtDate.setText(pointMd.getTransaction_date());
            } catch (Exception e) {
                e.printStackTrace();
            }

            txtPointsAwarded.setText(pointMd.getUpdate_points());
            txtReason.setText(pointMd.getReason());
            txtPointBalance.setText(pointMd.getPoints());
        }
    }
}