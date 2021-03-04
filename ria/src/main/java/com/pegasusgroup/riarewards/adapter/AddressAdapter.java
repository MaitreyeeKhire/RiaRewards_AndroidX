package com.pegasusgroup.riarewards.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.activity.AddressList;
import com.pegasusgroup.riarewards.fragments.AddAddress;
import com.pegasusgroup.riarewards.fragments.BaseFragment;
import com.pegasusgroup.riarewards.interfaces.FragmentChanger;
import com.pegasusgroup.riarewards.model.AddressMd;
import com.pegasusgroup.riarewards.utils.CommonMethods;
import com.pegasusgroup.riarewards.utils.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<AddressMd> addressMds;
    private SessionManager sessionManager;
    private FragmentChanger fragmentChanger;
    private BaseFragment baseFragment;

    public AddressAdapter(Context context, ArrayList<AddressMd> addressMds) {
        mContext = context;
        this.addressMds = addressMds;
        sessionManager = new SessionManager(context);
    }

    public AddressAdapter(Context context, ArrayList<AddressMd> addressMds, FragmentChanger fragmentChanger, BaseFragment baseFragment) {
        mContext = context;
        this.addressMds = addressMds;
        this.fragmentChanger = fragmentChanger;
        this.baseFragment = baseFragment;
        sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.address_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.setData(addressMds.get(position));
    }

    @Override
    public int getItemCount() {
        return addressMds.size();
    }

    /**
     * @description This method is used to call API
     */
    private void callDeleteAddressApi(final AddressMd addressMd, final int position) {
        try {
            String url = "https://www.myrewards.com.au/newapp/" + "user_shipping_details.php?"
                    + "user_id=" + sessionManager.getUserId()
                    + "&address_id=" + addressMd.getId()
                    + "&action=delete";

            url = url.replaceAll(" ", "%20");
            CommonMethods.printLog(url);
            ((AddressList) mContext).progressDialog.setMessage("Please Wait...");
            ((AddressList) mContext).progressDialog.show();

            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            StringRequest otpReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);
                            //{"response":"success","address_id":"21618"}
                            if (jsonObject.getString("response").equals("success")) {
                                String address = addressMd.getMember_name() + " \n" + addressMd.getAddress1() + "," + addressMd.getAddress2()
                                        + ",\n" + addressMd.getCity() + "," + addressMd.getState() + "," + addressMd.getCountry() + "-" + addressMd.getZipcode()
                                        + "\nMobile : " + addressMd.getMobile();
                                sessionManager.setShippingAddress(address);
                                addressMds.remove(position);
                                notifyDataSetChanged();
                            } else {
                                ((AddressList) mContext).showToast(jsonObject.getString("response"));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        ((AddressList) mContext).progressDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }) {
                @Override
                protected java.util.Map<String, String> getParams() {
                    return new HashMap<>();
                }
            };

            requestQueue.add(otpReq);
        } catch (Exception e) {
            e.printStackTrace();
            ((AddressList) mContext).progressDialog.dismiss();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private AppCompatTextView txtAddress;
        private AppCompatImageView imgEdit;
        private AppCompatImageView imgDelete;
        private AddressMd addressMd;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            itemView.setOnClickListener(this);
        }

        void setData(final AddressMd addressMd) {
            this.addressMd = addressMd;
            String address = addressMd.getMember_name() + " \n" + addressMd.getAddress1() + "," + addressMd.getAddress2()
                    + ",\n" + addressMd.getCity() + "," + addressMd.getState() + "," + addressMd.getCountry() + "-" + addressMd.getZipcode()
                    + "\nMobile : " + addressMd.getMobile();
            txtAddress.setText(address);

            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("addressMd", addressMd);
                    AddAddress addAddress = new AddAddress();
                    addAddress.setArguments(bundle);
                    fragmentChanger.change(addAddress);
                    //((AddressList) mContext).startNextActivity(mContext, AddAddress.class, bundle);
                }
            });

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Sure you want to delete?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    callDeleteAddressApi(addressMd, getAdapterPosition());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    Button negative = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                    Button positive = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    negative.setTextColor(Color.parseColor("#FFFF0400"));
                    positive.setTextColor(Color.parseColor("#FFFF0400"));

                }
            });
        }

        @Override
        public void onClick(View v) {
            sessionManager.setShippingAddress(txtAddress.getText().toString());
            sessionManager.setAddressObject(new Gson().toJson(addressMd));
            Objects.requireNonNull(baseFragment.getActivity()).getSupportFragmentManager().popBackStack();
//            ((AddressList) mContext).onBackPressed();
        }
    }
}