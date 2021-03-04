package com.pegasusgroup.riarewards.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.signature.StringSignature;
import com.google.android.material.textfield.TextInputEditText;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.interfaces.AppConstants;
import com.pegasusgroup.riarewards.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.pegasusgroup.riarewards.interfaces.AppConstants.CLIENT_ID;
import static com.pegasusgroup.riarewards.interfaces.AppConstants.UPLOAD_IMAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends BaseFragment {

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;
    private RelativeLayout editImage;
    private boolean isEditImage = false;
    private AppCompatImageView imgProfile;

    private AppCompatTextView txtCustomerName;
    private AppCompatTextView txtBirthDate;

    private TextInputEditText edtCustomerName;
    private TextInputEditText edtEmail;
    private TextInputEditText edtMobile;
    //    private TextInputEditText edtAddress;
    private AppCompatTextView txtNote;

    private AppCompatButton cmdSave;

    private String newsLetter;
    private String gender;

    private DatePickerDialog datePickerDialog;

    private AppCompatTextView txtContactUs;

    private AppCompatRadioButton optNewsLetterYes;
    private AppCompatRadioButton optNewsLetterNo;
    private AppCompatRadioButton optMale;
    private AppCompatRadioButton optFemale;
    private AppCompatRadioButton optOther;

    private String image_name;

    private String note;


    public Profile() {
        // Required empty public constructor
    }


    @Override
    protected View getLayoutResource() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_profie, container, false);
    }

    @Override
    protected void initComponents(View view) {
        txtCustomerName = findViewById(R.id.txtCustomerName);
        edtCustomerName = findViewById(R.id.edtCustomerName);
        edtEmail = findViewById(R.id.edtEmail);

        txtBirthDate = findViewById(R.id.txtBirthDate);
        imgProfile = findViewById(R.id.imgUserProfile);
        editImage = findViewById(R.id.editImage);
        edtMobile = findViewById(R.id.edtMobile);
//        edtAddress = findViewById(R.id.edtAddress);
        cmdSave = findViewById(R.id.cmdSave);

        optNewsLetterYes = findViewById(R.id.optNewsLetterYes);
        optNewsLetterNo = findViewById(R.id.optNewsLetterNo);
        optMale = findViewById(R.id.optMale);
        optFemale = findViewById(R.id.optFemale);
        optOther = findViewById(R.id.optOther);

        final Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        txtBirthDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
        txtContactUs = findViewById(R.id.txtContactUs);

        txtNote = findViewById(R.id.txtNote);
        note = "<html><body style=\"text-align:justify\"><b>NOTE:</b> To amend the below details, please contact Ria on <b>1800 701 488</b> or " +
                "<b>riarewards@riafinancial.com</b></body></Html>";
//        note = "NOTE: To amend the below details, please contact Ria on 1800 701 488 or riarewards@riafinancial.com";
    }

    @Override
    protected void setListeners() {
        Spanned sp = Html.fromHtml(note);

        SpannableString spannableString = new SpannableString(sp);
        spannableString.setSpan(new NumberClickableSpan(), 56, 68, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new EmailClickableSpan(), 72, 99, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtNote.setText(spannableString);

        txtNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        txtNote.setMovementMethod(LinkMovementMethod.getInstance());

        txtContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentChanger.change(new ContactUs());
            }
        });

        txtBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        if (sessionManager.getProfileImage().isEmpty()) {
            Glide.with(mContext).load(R.drawable.profile_image).asBitmap().centerCrop().into(new BitmapImageViewTarget(imgProfile) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    imgProfile.setImageDrawable(circularBitmapDrawable);
                }
            });
        } else {
            Glide.with(mContext)
                    .load(sessionManager.getProfileImage())
                    .asBitmap().centerCrop()
                    .signature(new StringSignature(Long.toString(System.currentTimeMillis())))
                    .into(new BitmapImageViewTarget(imgProfile) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            imgProfile.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }

        editImage.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (mContext.checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_REQUEST_CODE);
                } else {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(intent, CAMERA_REQUEST);
                }
            }
        });

        if (sessionManager.getProfileImage().isEmpty()) {
            callGetProfileImage();
        }
        callGetUserInfo();

        cmdSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditImage) {
                    callUpdateProfileImage();
                }
                callUpdateUserInfo();
            }
        });

        optNewsLetterYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsLetter = "1";
            }
        });

        optNewsLetterNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsLetter = "0";
            }
        });

        optMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "1";
            }
        });

        optFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "2";
            }
        });

        optOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "3";
            }
        });

        if (optNewsLetterYes.isChecked()) {
            newsLetter = "1";
        } else if (optNewsLetterNo.isChecked()) {
            newsLetter = "0";
        }

        if (optMale.isChecked()) {
            gender = "1";
        } else if (optFemale.isChecked()) {
            gender = "2";
        } else if (optOther.isChecked()) {
            gender = "3";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, CAMERA_REQUEST);
            } else {
                showToast("camera permission denied");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");

            Resources res = getResources();
            RoundedBitmapDrawable dr =
                    RoundedBitmapDrawableFactory.create(res, photo);
//            dr.setCornerRadius(Math.max(photo.getWidth(), photo.getHeight()) / 2.0f);
            dr.setCircular(true);
            imgProfile.setImageDrawable(dr);
            isEditImage = true;

            //converting image to base64 string
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Objects.requireNonNull(photo).compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            image_name = "data:jpeg;" + Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }
    }

    /**
     * @description This method is used to call Set Password Method
     */
    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    private void callGetUserInfo() {
        try {
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            String url = "https://www.myrewards.com.au/newapp/get_user1.php?uname=" + sessionManager.getUserId() + "&client_id=" + CLIENT_ID + "&response_type=json";

            StringRequest otpReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            sessionManager.setFirstName(jsonObject.getString("first_name"));
                            sessionManager.setLastName(jsonObject.getString("last_name"));
                            sessionManager.setClientEmail(jsonObject.getString("client_email"));
                            sessionManager.setClientName(jsonObject.getString("client_name"));
                            sessionManager.setMobile(jsonObject.getString("mobile"));
                            sessionManager.setNewsLetter(jsonObject.getString("newsletter"));
                            sessionManager.setGender(jsonObject.getString("gender"));

                            String dob = jsonObject.getString("dob");
                            SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat appFormat = new SimpleDateFormat("dd/MM/yyyy");
                            dob = appFormat.format(Objects.requireNonNull(serverFormat.parse(dob)));
                            sessionManager.setBirthDate(dob);

                            txtCustomerName.setText("Hi " + sessionManager.getFirstName() + "!");
                            edtCustomerName.setText(sessionManager.getFirstName() + " " + sessionManager.getLastName());
                            edtCustomerName.setEnabled(false);
                            edtEmail.setText(sessionManager.getEmail());
                            edtEmail.setEnabled(false);
                            edtMobile.setText(sessionManager.getMobile());
                            edtMobile.setEnabled(false);
                            txtBirthDate.setText(sessionManager.getBirthDate());
                            txtBirthDate.setEnabled(false);


                            if (sessionManager.getNewsLetter().equals("1")) {
                                optNewsLetterYes.setChecked(true);
                            } else {
                                optNewsLetterNo.setChecked(true);
                            }

                            switch (sessionManager.getGender()) {
                                case "1":
                                    optMale.setChecked(true);
                                    break;
                                case "2":
                                    optFemale.setChecked(true);
                                    break;
                                case "3":
                                    optOther.setChecked(true);
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        progressDialog.dismiss();
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
                    Map<String, String> params = new HashMap<>();
                    CommonMethods.printLog("Get UserInfo Params : " + params);
                    return params;
                }
            };

            requestQueue.add(otpReq);

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @description This method is used to call Set Password Method
     */
    private void callGetProfileImage() {
        try {
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            CommonMethods.printLog("URL : " + AppConstants.GET_IMAGE);

            StringRequest otpReq = new StringRequest(Request.Method.POST, AppConstants.GET_IMAGE, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.has("status")) {
                                String code = jsonObject.getString("status");
                                if (code.equals("200")) {
                                    Glide.with(mContext).load(jsonObject.getString("user_image")).asBitmap().centerCrop().into(new BitmapImageViewTarget(imgProfile) {
                                        @Override
                                        protected void setResource(Bitmap resource) {
                                            RoundedBitmapDrawable circularBitmapDrawable =
                                                    RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                                            circularBitmapDrawable.setCircular(true);
                                            imgProfile.setImageDrawable(circularBitmapDrawable);
                                            try {
                                                sessionManager.setProfileImage(jsonObject.getString("user_image"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        progressDialog.dismiss();
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
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", sessionManager.getUserId());
                    CommonMethods.printLog("Get Profile Image Params : " + params);
                    return params;
                }
            };

            requestQueue.add(otpReq);

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @description This method is used to call Set Password Method
     */
    private void callUpdateUserInfo() {
        try {
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            CommonMethods.printLog("URL : " + AppConstants.UPDATE_USER_INFO);

            StringRequest otpReq = new StringRequest(Request.Method.POST, AppConstants.UPDATE_USER_INFO, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.has("status")) {
                                String code = jsonObject.getString("status");
                                if (code.equals("200")) {
                                    Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        progressDialog.dismiss();
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
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", sessionManager.getUserId());
                    params.put("gender", gender);
                    params.put("newsletter", newsLetter);
                    CommonMethods.printLog("Update UserInfo Params : " + params);
                    return params;
                }
            };

            requestQueue.add(otpReq);

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @description This method is used to call Set Password Method
     */
    private void callUpdateProfileImage() {
        try {
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            CommonMethods.printLog("URL : " + UPLOAD_IMAGE);

            StringRequest otpReq = new StringRequest(Request.Method.POST, UPLOAD_IMAGE, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            JSONObject jsonObject = new JSONObject(response);
                            showToast(jsonObject.getString("msg"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        progressDialog.dismiss();
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
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", sessionManager.getUserId());
                    params.put("image_name", image_name);
                    CommonMethods.printLog("Get UserInfo Params : " + params);
                    return params;
                }
            };

            requestQueue.add(otpReq);

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    private class NumberClickableSpan extends ClickableSpan {

        @Override
        public void onClick(@NonNull View widget) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:1800701488"));
            startActivity(intent);
        }
    }

    private class EmailClickableSpan extends ClickableSpan {

        @Override
        public void onClick(@NonNull View widget) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "riarewards@riafinancial.com", null));
            startActivity(intent);
        }
    }
}