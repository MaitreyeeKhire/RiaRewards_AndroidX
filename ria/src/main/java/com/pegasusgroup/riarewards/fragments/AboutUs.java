package com.pegasusgroup.riarewards.fragments;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.pegasusgroup.riarewards.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUs extends BaseFragment {

    private AppCompatTextView txtAboutUs;
    private AppCompatTextView txtTermsConditions;
    private AppCompatTextView txtFaq;

    public AboutUs() {
        // Required empty public constructor
    }


    @Override
    protected View getLayoutResource() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    protected void initComponents(View view) {
        txtAboutUs = findViewById(R.id.txtAboutUs);
        txtTermsConditions = findViewById(R.id.txtTermsConditions);
        txtFaq = findViewById(R.id.txtFaq);
    }

    @Override
    protected void setListeners() {
        String about_me_new = "<p><b><font size=\"26\" color=\"black\">Welcome to the </font></b>" + sessionManager.getClientName() + "</p>" + "\n" +
                "\n" +
                "<p><strong><font style = \"bold\" size=\"6\" color=\"black\">how it works...</font>\n</strong></p>\n" +
                "\n" +
                sessionManager.getClientName() + " has 1000's of offers from all around Australia and even more overseas. Because there are so many suppliers there are different ways to access / redeem each benefit. It's easy to follow by reading the notes that are included with each offer, but if you get stuck here are a few pointers to assist you.\n" +

                "\n\n" +
                "<p><b><font size=\"6\" color=\"black\">tips on searching...</font></b></p>\n" +

                "<p>searching is easy... use the key words of what you are searching for to get rolling. If unsuccessful on that term, try some variations on the item you are seeking. You will then be able to refine your search by category, country, state and suburb. Look for the filters on the left of the page. If you still cannot find what you’re looking for pop us an email or give us a call on: 1300 857 787.</p>\n" +
                "\n\n";

        Spanned sp = Html.fromHtml(about_me_new);
        txtAboutUs.setText(sp);

        txtTermsConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentChanger.change(new TermsConditions());
            }
        });

        txtFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentChanger.change(new Faq());
            }
        });
    }
}