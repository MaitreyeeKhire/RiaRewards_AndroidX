package com.pegasusgroup.riarewards.interfaces;

import androidx.fragment.app.Fragment;

public interface FragmentChanger {
    void change(Fragment fragment);

    void change(Fragment fragment, boolean displayBackImage);
}