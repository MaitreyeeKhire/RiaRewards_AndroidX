package com.pegasusgroup.riarewards.activity;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.pegasusgroup.riarewards.R;

import java.util.Objects;

public class AboutUs extends BaseAppCompatActivity {

    private AppCompatTextView txtAboutUs;
    private AppCompatTextView txtTermsConditions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_about_us;
    }

    @Override
    protected void initComponents() {
        txtAboutUs = findViewById(R.id.txtAboutUs);
        txtTermsConditions = findViewById(R.id.txtTermsConditions);
    }

    @Override
    protected void setListeners() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        String aboutme_new = "<p><b><font size=\"26\" color=\"black\">Welcome to the </font></b>" + sessionManager.getClientName() + "</p>" + "\n" +
                "\n" +
                "<p><strong><font style = \"bold\" size=\"6\" color=\"black\">how it works...</font>\n</strong></p>\n" +
                "\n" +
                sessionManager.getClientName() + " has 1000's of offers from all around Australia and even more overseas. Because there are so many suppliers there are different ways to access / redeem each benefit. It's easy to follow by reading the notes that are included with each offer, but if you get stuck here are a few pointers to assist you.\n" +

                "\n\n" +
                "<p><b><font size=\"6\" color=\"black\">tips on searching...</font></b></p>\n" +

                "<p>searching is easy... use the key words of what you are searching for to get rolling. If unsuccessful on that term, try some variations on the item you are seeking. You will then be able to refine your search by category, country, state and suburb. Look for the filters on the left of the page. If you still cannot find what you’re looking for pop us an email or give us a call on: 1300 857 787.</p>\n" +
                "\n\n";

        Spanned sp = Html.fromHtml(aboutme_new);
        txtAboutUs.setText(sp);

        txtTermsConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextActivity(AboutUs.this, TermsConditions.class);
            }
        });
    }
}