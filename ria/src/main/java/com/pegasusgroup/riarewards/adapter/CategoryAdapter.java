package com.pegasusgroup.riarewards.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.fragments.ProductList;
import com.pegasusgroup.riarewards.interfaces.FragmentChanger;
import com.pegasusgroup.riarewards.model.CategoryModel;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private ArrayList<CategoryModel> homeCategoryModels;
    private FragmentChanger fragmentChanger;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> homeCategoryModels) {
        this.context = context;
        this.homeCategoryModels = homeCategoryModels;
    }

    public CategoryAdapter(Context context, ArrayList<CategoryModel> homeCategoryModels, FragmentChanger fragmentChanger) {
        this.context = context;
        this.homeCategoryModels = homeCategoryModels;
        this.fragmentChanger = fragmentChanger;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_row, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int position) {
        try {
            categoryViewHolder.setData(homeCategoryModels.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return homeCategoryModels.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private AppCompatImageView imageCategory;
        private AppCompatTextView txtTitle;
//        private AppCompatTextView txtItems;
//        private AppCompatImageView imageDetail;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCategory = itemView.findViewById(R.id.imageCategory);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void setData(CategoryModel homeCategoryModel) {
            Glide.with(context).load(homeCategoryModel.getImagePath()).into(imageCategory);
            txtTitle.setText(homeCategoryModel.getCategoryName());
        }

        @Override
        public void onClick(View view) {
            try {
                ProductList productList = new ProductList();
                Bundle bundle = new Bundle();
                bundle.putString("id", homeCategoryModels.get(getAdapterPosition()).getId());
                bundle.putString("name", homeCategoryModels.get(getAdapterPosition()).getCategoryName());
                productList.setArguments(bundle);
                fragmentChanger.change(productList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}