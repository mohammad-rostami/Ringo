package com.isatel.app.ringo;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



public class ActivityDescriptions extends AppCompatActivity {
    private TextView mTextView_Header;
    private LinearLayout mLayout_ToolBar;

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    protected void initStatusBar() {
        int statusBar = getStatusBarHeight();
        int a = mLayout_ToolBar.getHeight();
        int b = mLayout_ToolBar.getWidth();

        mLayout_ToolBar.getLayoutParams().height = a + statusBar;
        mLayout_ToolBar.getLayoutParams().width = b;
        mLayout_ToolBar.requestLayout();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptions);

        // defining font library for this class
        Typeface type = Typeface.createFromAsset(getAssets(), "iran_sans_regular.ttf");
        Typeface type_medium = Typeface.createFromAsset(getAssets(), "iran_sans_medium.ttf");
        Typeface type_roboto = Typeface.createFromAsset(getAssets(), "roboto_light.ttf");
        Typeface type_roboto_regular = Typeface.createFromAsset(getAssets(), "roboto_regular.ttf");

        mLayout_ToolBar = (LinearLayout) findViewById(R.id.toolbar_main_tb_main);

        // Toolbar Buttons
        ImageView mImageViewBack = (ImageView) findViewById(R.id.btnNavigation);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTextView_Header = (TextView) findViewById(R.id.toolbar_main_tv_header);
        mTextView_Header.setTypeface(type_medium);
        TextView mTextView_Address = (TextView) findViewById(R.id.address);
        TextView mTextView_About = (TextView) findViewById(R.id.about);
        TextView mTextView_About1 = (TextView) findViewById(R.id.about1);
        TextView mTextView_About2 = (TextView) findViewById(R.id.about2);
        TextView mTextView_About3 = (TextView) findViewById(R.id.about3);
        TextView mTextView_About4 = (TextView) findViewById(R.id.textView);
        mTextView_Address.setTypeface(type);
        mTextView_About.setTypeface(type_medium);
        mTextView_About1.setTypeface(type);
        mTextView_About2.setTypeface(type);
        mTextView_About3.setTypeface(type_medium);
        mTextView_About4.setTypeface(type_roboto_regular);


        // Defining Views
        LinearLayout mLinearLayout_About = (LinearLayout) findViewById(R.id.activity_description_ll_about);
        LinearLayout mLinearLayout_Contact = (LinearLayout) findViewById(R.id.activity_description_ll_contact);

        LinearLayout mLinearLayout_Call = (LinearLayout) findViewById(R.id.about_call);
        LinearLayout mLinearLayout_Mail = (LinearLayout) findViewById(R.id.about_mail);
        LinearLayout mLinearLayout_Web = (LinearLayout) findViewById(R.id.web);
        TextView mLinearLayout_WebSite = (TextView) findViewById(R.id.website);
        mLinearLayout_WebSite.setTypeface(type);


        mLinearLayout_Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:02122549559"));
                startActivity(intent);
            }
        });
        mLinearLayout_Mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "info@isatelco.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));

            }
        });
        mLinearLayout_Web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.isatelco.com"));
                startActivity(browserIntent);

            }
        });
        mLinearLayout_WebSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.isatelco.com"));
                startActivity(browserIntent);

            }
        });


        // Getting data from intent and decide which layout to show
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String mStrData = bundle.getString("CONTENT");

            switch (mStrData) {
                case "about":
                    mLinearLayout_About.setVisibility(View.VISIBLE);
                    mLinearLayout_Contact.setVisibility(View.GONE);
                    mTextView_Header.setText("درباره ما");
                    break;
                case "laws":
                    mLinearLayout_About.setVisibility(View.GONE);
                    mLinearLayout_Contact.setVisibility(View.GONE);
                    mTextView_Header.setText("قوانین");
                    break;
                case "contact":
                    mLinearLayout_About.setVisibility(View.GONE);
                    mLinearLayout_Contact.setVisibility(View.VISIBLE);
                    mTextView_Header.setText("ارتباط با ما");
                    break;
            }
        }
    }
}
