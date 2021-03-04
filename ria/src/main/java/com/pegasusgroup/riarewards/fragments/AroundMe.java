package com.pegasusgroup.riarewards.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.activity.Home;
import com.pegasusgroup.riarewards.interfaces.FragmentChanger;
import com.pegasusgroup.riarewards.utils.CommonMethods;
import com.pegasusgroup.riarewards.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AroundMe extends Fragment implements GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback, FragmentChanger {

    private static final int LOCATION_REQUEST = 101;
    private GoogleMap mMap;
    private int REQ_PERMISSION = 8787;
    private SessionManager sessionManager;
    private AppCompatEditText edtPostcode;
    //    protected LocationManager locationManager;
    private Location mLocation;
    private Status status;
    private Home homeActivity;
    private FragmentChanger fragmentChanger;

    public AroundMe() {
        // Required empty public constructor
    }

    public AroundMe(FragmentChanger fragmentChanger) {
        this.fragmentChanger = fragmentChanger;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(Objects.requireNonNull(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_around_me, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        homeActivity = (Home) getActivity();
        edtPostcode = view.findViewById(R.id.edtPostcode);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtPostcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchOfferByLocation(Objects.requireNonNull(edtPostcode.getText()).toString());
                    // Hide Keyboard
                    ((Home) Objects.requireNonNull(getActivity())).hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String id = (String) marker.getTag();
        if (!Objects.requireNonNull(id).isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putString("type", "banner");
            bundle.putString("productId", id);
//            homeActivity.startNextActivity(homeActivity, OfferDetail.class, bundle);
            OfferDetail offerDetail = new OfferDetail();
            offerDetail.setArguments(bundle);
            fragmentChanger.change(offerDetail);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnInfoWindowClickListener(this);
            mMap.setMyLocationEnabled(true);
            get_products_by_loc();
        } else {
            // Show rationale and request permission.
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQ_PERMISSION
            );
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void get_products_by_loc() {
        try {
            if (mLocation != null && mMap != null) {
                homeActivity.progressDialog.show();
                LatLng latlng1 = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
//                LatLng latlng1 = new LatLng(-37.583210, 145.127960);
                drawMarkerWithCircle(latlng1);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng1, 11));

//                String countURL = "https://www.myrewards.com.au/newapp/get_products_by_loc.php?lat="
//                        + location.getLatitude() + "&lng=" + location.getLongitude() + "&cid=" + sessionManager.getClientId()
//                        + "&b=0.100000&c=" + "Australia" + "&response_type=json";

                String countURL = "https://www.myrewards.com.au/newapp/get_products_by_loc.php?lat="
                        + latlng1.latitude + "&lng=" + latlng1.longitude + "&cid=" + sessionManager.getClientId()
                        + "&b=0.100000&c=" + "Australia" + "&response_type=json";

                CommonMethods.printLog("Around Me : " + countURL);

                RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));

                StringRequest postReq = new StringRequest(Request.Method.GET, countURL, new Response.Listener<String>() {
                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("error_msg")) {
//                                    String error_msg = jsonObject.getString("error_msg");
                                    Toast.makeText(getActivity(), getString(R.string.not_products_available), Toast.LENGTH_SHORT).show();
                                    //showAlert(error_msg);
                                }
                                if (jsonObject.has("product")) {
                                    JSONArray product = jsonObject.getJSONArray("product");
                                    for (int i = 0; i < product.length(); i++) {
                                        JSONObject jsonObject1 = product.getJSONObject(i);
                                        String id = jsonObject1.getString("id");
                                        String name = jsonObject1.getString("name");
                                        String latitude = jsonObject1.getString("latitude");
                                        String longitude = jsonObject1.getString("longitude");
                                        String pin_type = jsonObject1.getString("pin_type");

                                        MarkerOptions markerOptions;
                                        if (pin_type.equalsIgnoreCase("0")) {
                                            markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.defaulg));
                                        } else if (pin_type.equalsIgnoreCase("1")) {
                                            markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.automotiv));
                                        } else if (pin_type.equalsIgnoreCase("2")) {
                                            markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.dining));
                                        } else if (pin_type.equalsIgnoreCase("3")) {
                                            markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.healthandbauty1));
                                        } else if (pin_type.equalsIgnoreCase("4")) {
                                            markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.home));
                                        } else if (pin_type.equalsIgnoreCase("5")) {
                                            markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.defaulg));
                                        } else if (pin_type.equalsIgnoreCase("6")) {
                                            markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.travel));
                                        } else if (pin_type.equalsIgnoreCase("7")) {
                                            markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.entertainment));
                                        } else if (pin_type.equalsIgnoreCase("8")) {
                                            markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.defaulg));
                                        } else if (pin_type.equalsIgnoreCase("9")) {
                                            markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.defaulg));
                                        } else {
                                            markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.defaulg));
                                        }

                                        Double latitudeD = Double.parseDouble(latitude);
                                        Double longitudeD = Double.parseDouble(longitude);
                                        if (latitudeD != null && longitudeD != null) {
                                            LatLng latlng = new LatLng(latitudeD, longitudeD);
                                            markerOptions.position(latlng).title(name);
                                            mMap.addMarker(markerOptions).setTag(id);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                homeActivity.progressDialog.dismiss();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("AroundMe", "Error: " + error.getMessage());
                        homeActivity.progressDialog.dismiss();
                    }

                });

                requestQueue.add(postReq);
            } else {
                fetchLocation();
            }
        } catch (Exception e) {
            e.printStackTrace();
            homeActivity.progressDialog.dismiss();
        }
    }

    /**
     * @param position Position
     * @description This method is used to Draw Marker
     */
    private void drawMarkerWithCircle(LatLng position) {
        if (mMap != null) {
            double radiusInMeters = 10000.0; // 1 KM changed to 20 KM Ref : 2nd December Email
            int strokeColor = 0xff4285F4; //red outline
            int shadeColor = 0x444285F4; //opaque red fill

            CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(4);
            mMap.addCircle(circleOptions);

            /*MarkerOptions markerOptions = new MarkerOptions().position(position);
            mMap.addMarker(markerOptions);*/
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQ_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setOnInfoWindowClickListener(this);
                mMap.setMyLocationEnabled(true);
                get_products_by_loc();
            } else {
                // Permission was denied. Display an error message.
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("To find around you, you need to give location permission!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                dialog.dismiss();
                                requestPermissions(
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQ_PERMISSION
                                );

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                Button positive = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                positive.setTextColor(Color.parseColor("#FFFF0400"));
            }
        }
    }

    /**
     * @description This method is used to call Api
     */
    private void searchOfferByLocation(String postCode) {
        try {
            ((Home) Objects.requireNonNull(getActivity())).progressDialog.setMessage("Loading");
            ((Home) getActivity()).progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));

            String url = "https://www.myrewards.com.au/newapp/search_offer_by_location.php?post_code=" + postCode + "&client_id=2207";
            CommonMethods.printLog("URL : " + url);

            StringRequest otpReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                JSONObject jsonObject;

                @SuppressWarnings("ConstantConditions")
                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("post code response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.has("status")) {
                                String status = jsonObject.getString("status");
                                if (status.equals("200")) {
                                    mMap.clear();
                                    if (jsonObject.has("data")) {
                                        double lat = 0.0;
                                        double lng = 0.0;
                                        JSONArray product = jsonObject.getJSONArray("data");
                                        for (int i = 0; i < product.length(); i++) {
                                            JSONObject jsonObject1 = product.getJSONObject(i);
                                            String id = jsonObject1.getString("id");
                                            String name = jsonObject1.getString("name");
                                            String latitude = jsonObject1.getString("latitude");
                                            String longitude = jsonObject1.getString("longitude");
                                            String pin_type = jsonObject1.getString("pin_type");

                                            MarkerOptions markerOptions;
                                            if (pin_type.equalsIgnoreCase("0")) {
                                                markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.defaulg));
                                            } else if (pin_type.equalsIgnoreCase("1")) {
                                                markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.automotiv));
                                            } else if (pin_type.equalsIgnoreCase("2")) {
                                                markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.dining));
                                            } else if (pin_type.equalsIgnoreCase("3")) {
                                                markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.healthandbauty1));
                                            } else if (pin_type.equalsIgnoreCase("4")) {
                                                markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.home));
                                            } else if (pin_type.equalsIgnoreCase("5")) {
                                                markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.defaulg));
                                            } else if (pin_type.equalsIgnoreCase("6")) {
                                                markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.travel));
                                            } else if (pin_type.equalsIgnoreCase("7")) {
                                                markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.entertainment));
                                            } else if (pin_type.equalsIgnoreCase("8")) {
                                                markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.defaulg));
                                            } else if (pin_type.equalsIgnoreCase("9")) {
                                                markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.defaulg));
                                            } else {
                                                markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.defaulg));
                                            }

                                            Double latitudeD = Double.parseDouble(latitude);
                                            Double longitudeD = Double.parseDouble(longitude);
                                            if (latitudeD != null && longitudeD != null) {
                                                LatLng latlng = new LatLng(latitudeD, longitudeD);
                                                markerOptions.position(latlng).title(name);
                                                mMap.addMarker(markerOptions).setTag(id);
                                            }
                                            lat = Double.parseDouble(latitude);
                                            lng = Double.parseDouble(longitude);
                                        }
                                        LatLng latlng1 = new LatLng(lat, lng);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng1, 11));
                                    }
                                } else {
                                    Toast.makeText(getActivity(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        ((Home) Objects.requireNonNull(getActivity())).progressDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    ((Home) Objects.requireNonNull(getActivity())).progressDialog.dismiss();
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
            ((Home) Objects.requireNonNull(getActivity())).progressDialog.dismiss();
        }
    }

    /**
     * @description This method is used to fetch current location
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    private void fetchLocation() {
        try {
            GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(Objects.requireNonNull(getActivity()))
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult result) {
                    status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            get_products_by_loc();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(getActivity(), LOCATION_REQUEST);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });

            FusedLocationProviderClient fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(getActivity());
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(),
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mLocation = location;
                                CommonMethods.printLog("Location : " + location.getLatitude() + " , " + location.getLongitude());
                                get_products_by_loc();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ShowToast")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATION_REQUEST) {
            if (resultCode == Activity.RESULT_CANCELED) {
                CommonMethods.printLog("Activity.RESULT_CANCELED");
                try {
                    // Show the dialog by calling
                    // startResolutionForResult(),
                    // and check the result in onActivityResult().
                    Toast.makeText(getActivity(), R.string.location_required, Toast.LENGTH_SHORT).show();
                    status.startResolutionForResult(getActivity(), LOCATION_REQUEST);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getActivity(), "Fetching Location", Toast.LENGTH_SHORT).show();
                fetchLocation();
            }
        }
    }

    @Override
    public void change(Fragment fragment) {

    }

    @Override
    public void change(Fragment fragment, boolean displayBackImage) {

    }
}