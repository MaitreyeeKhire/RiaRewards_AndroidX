package com.pegasusgroup.riarewards.utils;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class EndDrawerToggle implements DrawerLayout.DrawerListener {

    private final DrawerLayout drawerLayout;
    private final Toolbar toolbar;
    private final DrawerArrowDrawable arrowDrawable;
    private final int openDrawerContentDesc, closeDrawerContentDesc;
    private MenuItem toggleItem;

    public EndDrawerToggle(DrawerLayout drawerLayout, Toolbar toolbar,
                           int openDrawerContentDesc, int closeDrawerContentDesc) {
        this.drawerLayout = drawerLayout;
        this.toolbar = toolbar;

        this.openDrawerContentDesc = openDrawerContentDesc;
        this.closeDrawerContentDesc = closeDrawerContentDesc;

        arrowDrawable = new DrawerArrowDrawable(toolbar.getContext());
        arrowDrawable.setDirection(DrawerArrowDrawable.ARROW_DIRECTION_END);
    }

    public void setToggleOnMenu(Menu menu) {
        toggleItem = menu.add(openDrawerContentDesc);
        toggleItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        toggleItem.setIcon(arrowDrawable);
        toggleItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                                  @Override
                                                  public boolean onMenuItemClick(MenuItem item) {
                                                      toggle();
                                                      return true;
                                                  }
                                              }
        );

        setPosition(drawerLayout.isDrawerOpen(GravityCompat.END) ? 1f : 0f);
    }

    private void toggle() {
        final int drawerLockMode = drawerLayout.getDrawerLockMode(GravityCompat.END);
        if (drawerLayout.isDrawerVisible(GravityCompat.END)
                && (drawerLockMode != DrawerLayout.LOCK_MODE_LOCKED_OPEN)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else if (drawerLockMode != DrawerLayout.LOCK_MODE_LOCKED_CLOSED) {
            drawerLayout.openDrawer(GravityCompat.END);
        }
    }

    private void setPosition(float position) {
        if (position == 1f) {
            arrowDrawable.setVerticalMirror(true);
            toggleItem.setTitle(closeDrawerContentDesc);
        } else if (position == 0f) {
            arrowDrawable.setVerticalMirror(false);
            toggleItem.setTitle(openDrawerContentDesc);
        }
        arrowDrawable.setProgress(position);
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        setPosition(Math.min(1f, Math.max(0f, slideOffset)));
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        setPosition(1f);
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        setPosition(0f);
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }
}