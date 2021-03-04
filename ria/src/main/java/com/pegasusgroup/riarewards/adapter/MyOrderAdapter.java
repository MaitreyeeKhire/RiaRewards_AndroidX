package com.pegasusgroup.riarewards.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.model.OrdersMd;

import java.util.ArrayList;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<OrdersMd> ordersMds;

    public MyOrderAdapter(Context context, ArrayList<OrdersMd> ordersMds) {
        mContext = context;
        this.ordersMds = ordersMds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.my_orders_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.setData(ordersMds.get(position));
    }

    @Override
    public int getItemCount() {
        return ordersMds.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView txtProductName;
        private AppCompatTextView txtOrderNumber;
        private AppCompatTextView txtOrderStatus;
        private AppCompatTextView edtDeliveryTime;
        private AppCompatImageView imgProduct;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtOrderNumber = itemView.findViewById(R.id.txtOrderNumber);
            txtOrderStatus = itemView.findViewById(R.id.txtOrderStatus);
            edtDeliveryTime = itemView.findViewById(R.id.edtDeliveryTime);
            imgProduct = itemView.findViewById(R.id.imgProduct);
        }

        void setData(OrdersMd ordersMd) {
            txtProductName.setText(ordersMd.getProduct_name());
            txtOrderNumber.setText(ordersMd.getCart_id());
            txtOrderStatus.setText(ordersMd.getCart_status());
//            edtDeliveryTime.setText("1st Feb,2020");
            edtDeliveryTime.setText(ordersMd.getMax_delivery_days());

            Glide
                    .with(mContext)
                    .load(ordersMd.getProduct_image())
                    .into(imgProduct);
        }
    }
}