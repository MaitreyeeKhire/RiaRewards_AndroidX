package com.pegasusgroup.riarewards.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.adapter.CategoryAdapter;
import com.pegasusgroup.riarewards.interfaces.AppConstants;
import com.pegasusgroup.riarewards.model.CategoryModel;
import com.pegasusgroup.riarewards.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.pegasusgroup.riarewards.interfaces.AppConstants.TIME_OUT;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class Categories extends BaseFragment {

    private RecyclerView recycler_view;
    private CategoryAdapter categoryAdapter;
    private ArrayList<CategoryModel> categoryModels;

    private AppCompatEditText edtProduct;

    public Categories() {
        // Required empty public constructor
    }


    @Override
    protected View getLayoutResource() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    protected void initComponents(View view) {
        recycler_view = findViewById(R.id.recycler_view);
        categoryModels = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(mContext, categoryModels, fragmentChanger);
        edtProduct = findViewById(R.id.edtProduct);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setListeners() {
        recycler_view.setAdapter(categoryAdapter);
        callCategoryApi();

        edtProduct.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Bundle bundle = new Bundle();
                    bundle.putString("query", getText(edtProduct));
//                    startNextActivity(com.pegasusgroup.riarewards.activity.Categories.this, ProductList.class, bundle);
                    ProductList productList = new ProductList();
                    productList.setArguments(bundle);
                    fragmentChanger.change(productList);
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });

        edtProduct.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                final int DRAWABLE_LEFT = 0;
//                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
//                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtProduct.getRight() - edtProduct.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Bundle bundle = new Bundle();
                        bundle.putString("query", getText(edtProduct));
//                        startNextActivity(com.pegasusgroup.riarewards.activity.Categories.this, ProductList.class, bundle);
                        ProductList productList = new ProductList();
                        productList.setArguments(bundle);
                        fragmentChanger.change(productList);
                        hideSoftKeyboard();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * @description This method is used to call Category API
     */
    private void callCategoryApi() {
        try {
            progressDialog.show();
            CommonMethods.printLog("Category URL : " + AppConstants.CATEGORY);

            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, AppConstants.CATEGORY, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonarray = response.getJSONArray("cat");
                        categoryModels.clear();
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            CategoryModel homeCategoryModel = new CategoryModel();
                            homeCategoryModel.setId(jsonobject.getString("id"));
                            homeCategoryModel.setCategoryName(jsonobject.getString("name"));
                            if (jsonobject.has("new_cat_image")) {
                                homeCategoryModel.setImagePath(jsonobject.getString("new_cat_image"));
                            }
                            categoryModels.add(homeCategoryModel);
                        }
                        categoryAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    } finally {
                        progressDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    progressDialog.dismiss();
                }
            });

            jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return TIME_OUT;
                }

                @Override
                public int getCurrentRetryCount() {
                    return TIME_OUT;
                }

                @Override
                public void retry(VolleyError error) {

                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
        }
    }
}