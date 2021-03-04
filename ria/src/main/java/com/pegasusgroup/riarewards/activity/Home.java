package com.pegasusgroup.riarewards.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.fragments.AboutUs;
import com.pegasusgroup.riarewards.fragments.AroundMe;
import com.pegasusgroup.riarewards.fragments.Cart;
import com.pegasusgroup.riarewards.fragments.Categories;
import com.pegasusgroup.riarewards.fragments.ContactUs;
import com.pegasusgroup.riarewards.fragments.CurrentPromotion;
import com.pegasusgroup.riarewards.fragments.MyOrders;
import com.pegasusgroup.riarewards.fragments.PointHistory;
import com.pegasusgroup.riarewards.fragments.Profile;
import com.pegasusgroup.riarewards.fragments.Review;
import com.pegasusgroup.riarewards.interfaces.FragmentChanger;
import com.pegasusgroup.riarewards.interfaces.FragmentReplacer;
import com.pegasusgroup.riarewards.interfaces.PromotionListener;
import com.pegasusgroup.riarewards.utils.EndDrawerToggle;

import java.util.Objects;

public class Home extends BaseAppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        PromotionListener,
        FragmentChanger,
        FragmentReplacer {

    private DrawerLayout drawer;
    //    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private AppCompatTextView cart_counter;
    //    private com.pegasusgroup.riarewards.fragments.Home homeFragment;
    private AroundMe aroundMe;
    private EndDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initComponents() {
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
//        toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
//            public void onDrawerClosed(View view) {
//                super.onDrawerClosed(view);
//                if (cart_counter != null)
//                    cart_counter.setText(sessionManager.getCartCount());
//            }
//
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                if (cart_counter != null)
//                    cart_counter.setText(sessionManager.getCartCount());
//            }
//        };
        drawerToggle = new EndDrawerToggle(
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            public void onDrawerClosed(@NonNull View view) {
                super.onDrawerClosed(view);
                cart_counter.setText(sessionManager.getCartCount());
            }

            public void onDrawerOpened(@NonNull View drawerView) {
                super.onDrawerOpened(drawerView);
                cart_counter.setText(sessionManager.getCartCount());
            }
        };
        cart_counter = (AppCompatTextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_my_cart));
//        homeFragment = new com.pegasusgroup.riarewards.fragments.Home();
        aroundMe = new AroundMe(this);
    }

    @Override
    protected void setListeners() {
        setSupportActionBar(toolbar);
        drawer.addDrawerListener(drawerToggle);
//        drawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        replaceFragment(new com.pegasusgroup.riarewards.fragments.Home());
        cart_counter.setText(sessionManager.getCartCount());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                    drawer.closeDrawer(Gravity.RIGHT);
                } else {
                    drawer.openDrawer(Gravity.RIGHT);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cart_counter.setText(sessionManager.getCartCount());
    }

    private void replaceFragment(Fragment fragment) {
        try {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

//        if (getSupportFragmentManager().getBackStackEntryCount() > 1)
//            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();

//        displayBackImage = !(fragment instanceof com.pegasusgroup.riarewards.fragments.Home);

            if (displayBackImage) {
                imgBack.setVisibility(View.VISIBLE);
            } else {
                imgBack.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commitAllowingStateLoss();

        try {
            if (Objects.requireNonNull(fragment.getArguments()).getBoolean("displayBackImage", false)) {
                displayBackImage = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (displayBackImage) {
            imgBack.setVisibility(View.VISIBLE);
        } else {
            imgBack.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) {
            aroundMe.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        drawerToggle.setToggleOnMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return true for ActionBarToggle to handle the touch event
//        if (toggle.onOptionsItemSelected(item)) {
        if (llPointsBalanceWithImage.getVisibility() == View.VISIBLE)
            llPointsBalanceWithImage.performClick();
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    private void logDetails(String page) {
        putDetails(sessionManager.getClientId(), sessionManager.getUserId(), "android", androidId,
                "", "", "", "", page, reportDate, "", "From Side Menu");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.nav_home:
                displayBackImage = false;
                replaceFragment(new com.pegasusgroup.riarewards.fragments.Home());
                break;
            case R.id.nav_my_account:
                displayBackImage = true;
                //startNextActivity(Home.this, MyAccount.class);
                replaceFragment(new Profile());
                logDetails("My Account");
                break;
            case R.id.nav_customer_card:
                displayBackImage = true;
                startNextActivity(Home.this, CustomerCard.class);
                logDetails("Customer Card");
                break;
            case R.id.nav_current_promotion:
                displayBackImage = true;
                //startNextActivity(Home.this, CurrentPromotion.class);
                replaceFragment(new CurrentPromotion());
                logDetails("Current Promotion");
                break;
            case R.id.nav_points_history:
                displayBackImage = true;
//                startNextActivity(Home.this, PointHistory.class);
                replaceFragment(new PointHistory());
                logDetails("Point History");
                break;
            case R.id.nav_reviews:
                displayBackImage = true;
                replaceFragment(new Review());
                //startNextActivity(Home.this, Review.class);
                logDetails("Review");
                break;
            case R.id.nav_my_orders:
                displayBackImage = true;
                replaceFragment(new MyOrders());
//                startNextActivity(Home.this, MyOrders.class);
                logDetails("My Orders");
                break;
            case R.id.nav_my_cart:
                displayBackImage = true;
//                startNextActivity(Home.this, Cart.class);
                replaceFragment(new Cart());
                logDetails("Cart");
                break;
            case R.id.nav_categories:
                displayBackImage = true;
//                startNextActivity(Home.this, Categories.class);
                replaceFragment(new Categories());
                logDetails("Categories");
                break;
            case R.id.nav_around_me:
                displayBackImage = true;
                //content_home.setVisibility(View.GONE);
                //addFragmentINQueue(new AroundMe());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //startNextActivity(Home.this, Map.class);
                        replaceFragment(aroundMe);
                    }
                }, 350);
                logDetails("Around Me");
                break;
            case R.id.nav_favourite:
//                displayBackImage = true;
//                updateFragment(new Favourite());
                logDetails("Favourite");
                break;
            case R.id.nav_contact:
                displayBackImage = true;
//                startNextActivity(Home.this, ContactUs.class);
                replaceFragment(new ContactUs());
                break;
            case R.id.nav_about:
                displayBackImage = true;
//                startNextActivity(Home.this, AboutUs.class);
                replaceFragment(new AboutUs());
                break;
            case R.id.nav_logout: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Sure you want to log out?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sessionManager.clearSharedPreferenceData();
                                startActivity(new Intent(Home.this, Login.class));
                                finish();
                                logDetails("Logout - Yes");
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                logDetails("Logout - No");
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                Button negative = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                Button positive = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                negative.setTextColor(Color.parseColor("#FFFF0400"));
                positive.setTextColor(Color.parseColor("#FFFF0400"));
            }
            break;
        }
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    @Override
    public void refreshPromotion() {
        pointApi();
    }

    @Override
    public void change(Fragment fragment) {
        updateFragment(fragment);
    }

    @Override
    public void change(Fragment fragment, boolean displayBackImage) {
        Bundle bundle = fragment.getArguments();
        Objects.requireNonNull(bundle).putBoolean("displayBackImage", displayBackImage);
        fragment.setArguments(bundle);
        updateFragment(fragment);
    }

    @Override
    public void onBackPressed() {
//        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
//            getSupportFragmentManager().popBackStack();
//        } else {
//            if (Objects.requireNonNull(getSupportFragmentManager().getBackStackEntryAt(0).getName()).equalsIgnoreCase("Home")) {
//                super.onBackPressed();
//                super.onBackPressed();
//            } else {
//                imgBack.setVisibility(View.INVISIBLE);
//                replaceFragment(new com.pegasusgroup.riarewards.fragments.Home());
//            }
//        }

        if (llPointsBalanceWithImage.getVisibility() == View.VISIBLE) {
            llPointsBalanceWithImage.performClick();
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            imgBack.setVisibility(View.INVISIBLE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, new com.pegasusgroup.riarewards.fragments.Home())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void replace(Fragment fragment) {
        replaceFragment(fragment);
        if (fragment.getClass().getSimpleName().equalsIgnoreCase("Home")) {
            imgBack.setVisibility(View.INVISIBLE);
        } else {
            imgBack.setVisibility(View.VISIBLE);
        }
    }
}