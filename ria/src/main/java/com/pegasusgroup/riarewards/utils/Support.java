package com.pegasusgroup.riarewards.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.pegasusgroup.riarewards.interfaces.AppConstants;
import com.pegasusgroup.riarewards.sharedpreference.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;


/**
 * Created by deepak on 20-02-2016.
 */

/**
 * This class used to maintain all common utill methods which all are used often across many activities
 */
public class Support {
    private static Toast t;

    public static boolean isEmptyOrNot(CharSequence str) {
        if (str == null || str.length() == 0 || str.equals("null") || str.equals(" ") || str.equals("none") || str.equals("0") || str.equals(""))
            return true;
        else
            return false;
    }

    public static boolean addPermission(Context con, List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(con, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) con, permission))
                return false;
        }
        return true;
    }

    public static void hideKeyboard(Context con, View view) {
        InputMethodManager imm = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Context con, View view) {
        InputMethodManager imm = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public void showToast(Context context, String msg) {
        if (t == null) {
            t = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            t.setText(msg);
        }
        t.show();
    }

    // check is email valid
    public boolean isEmailValid(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z.]+";

        return email.matches(emailPattern);
    }

    public boolean isNetworkOnline(Context con) {
        boolean status;
        try {

            ConnectivityManager cm = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);

            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);

                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    status = true;
                } else {
                    status = false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return status;
    }

    public boolean isResultValid(String result) {
        return result != null && !result.equals("");
    }

    public String getCurrentDate(String format) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date d = new Date(c.getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        try {
            String s = sdf.format(d);
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getCurrentDate(Calendar c, String format) {
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date d = new Date(c.getTimeInMillis());

        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        try {
            String s = sdf.format(d);
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getDate(String dateText, String fromDatFormat, String toDateFormat) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(fromDatFormat, Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d = sdf.parse(dateText);
            sdf = new SimpleDateFormat(toDateFormat, Locale.getDefault());
            // sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateText;
    }

    public File getTempFile(Context con, String fileName) {
        File mFileTemp;

        mFileTemp = new File(getBaseFolderPath(con), fileName);

        return mFileTemp;
    }

    public String getBaseFolderPath(Context con) {
        String folderName = AppConstants.BASE_FOLDER_PATH;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            folderName = Environment.getExternalStorageDirectory() + "/"
                    + folderName;
        } else {
            folderName = con.getFilesDir() + "/" + folderName;
        }
        File f = new File(folderName);
        if (!f.exists()) {
            f.mkdirs();
            File nomedia = new File(folderName + "/" + ".NOMEDIA");
            try {
                nomedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return folderName;
    }

    public void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }

    }

    public void setTypeface(EditText et, String font) {
        Typeface face = Typeface.createFromAsset(et.getContext().getAssets(), font);
        et.setTypeface(face);
    }

    public void setTypeface(TextView tv, String font) {
        Typeface face = Typeface.createFromAsset(tv.getContext().getAssets(), font);
        tv.setTypeface(face);
    }

    public void setMarginRight(TextView tv, int r) {
        Toolbar.LayoutParams lp = (Toolbar.LayoutParams) tv.getLayoutParams();
        r = convertDpToPixel(tv.getContext(), r);
        lp.setMargins(0, 0, r, 0);

    }

    public int convertDpToPixel(Context context, int r) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(r * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public void startActivity(Context con, Class<?> className) {
        con.startActivity(new Intent(con, className));
    }

    public Properties getHeader(Context con) {
        SharedPreference sp = new SharedPreference(con);
        Properties header = new Properties();
        header.put(AppConstants.AUTHORIZATION, sp.getValueString(AppConstants.AUTHORIZATION));
        return header;
    }

    private String get3digitCount(int count) {
        if (count <= 9) {
            return "00" + count;
        }
        if (count <= 99) {
            return "0" + count;
        }
        return count + "";
    }

    public String getFileExtension(String path) {
        if (path != null && !path.equals("")) {
            int index = path.lastIndexOf(".");
            if (index > 0) {
                String ext = path.substring(index + 1);
                return ext;
            }
        }
        return null;
    }

    public String getCDNUrl(String imageKey, Context con) {
        String url = AppConstants.CDN_IMG_URL + imageKey + "?authorization=" + new SharedPreference(con).getValueString(AppConstants.AUTHORIZATION);
        return url;
    }

    public String getCDNUrl(String imageKey, Context con, String type) {
        String url = AppConstants.CDN_IMG_URL + imageKey + "?sourceType=" + type + "&authorization=" + new SharedPreference(con).getValueString(AppConstants.AUTHORIZATION);
        return url;
    }

    public void showToastMessageFromJSON(Context con, String data) throws JSONException {
        JSONObject obj = new JSONObject(data);
        String message = obj.getString(AppConstants.MESSAGE);
        showToast(con, message);
    }

    public void setToolbarFontAndAlignment(Toolbar toolbar) {
        TextView tv = null;

        try {
            int w = 0;
            if (toolbar.getNavigationIcon() != null) {
                w = toolbar.getNavigationIcon().getIntrinsicWidth();
            }
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            tv = (TextView) f.get(toolbar);
            Typeface tf = Typeface.createFromAsset(toolbar.getContext().getAssets(), AppConstants.FONT_AVENIR_MEDIUM);
            tv.setTypeface(tf);
            tv.setGravity(Gravity.CENTER);
            Toolbar.LayoutParams lp = (Toolbar.LayoutParams) tv.getLayoutParams();
            lp.width = LinearLayoutCompat.LayoutParams.MATCH_PARENT;
            lp.setMargins(0, 0, w, 0);
            //tv.setPadding(0, 0, w, 0);

        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }

    }

    public boolean isBackgroundMonitoringServiceRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningService : am.getRunningServices(Integer.MAX_VALUE)) {
          /*  if (RecoBackgroundMonitoringService.class.getName().equals(runningService.service.getClassName())) {
                return true;
            }*/
        }
        return false;
    }

    public void saveBitmap(Bitmap bitmap, String savePath) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(savePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (FileNotFoundException e) {
            throw e;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}