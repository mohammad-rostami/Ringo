package com.isatel.app.ringo;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



public class ActivityGoPremium extends AppCompatActivity {
    private TextView mTextView_Header;
    private LinearLayout mLayout_ToolBar;
    private Dialog dialog_premium;

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
        setContentView(R.layout.activity_go_premium);

        // defining font library for this class
        Typeface type = Typeface.createFromAsset(getAssets(), "iran_sans_regular.ttf");
        Typeface type_roboto = Typeface.createFromAsset(getAssets(), "iran_sans_medium.ttf");
        mLayout_ToolBar = (LinearLayout) findViewById(R.id.toolbar_main_tb_main);

        // premium Dialog
        dialog_premium = new Dialog(ActivityGoPremium.this);
        dialog_premium.setContentView(R.layout.dialog_premium);
        dialog_premium.setTitle("Custom Dialog");
        TextView dialog_txt = (TextView) dialog_premium.findViewById(R.id.dialog_txt);
        TextView dialog_txt1 = (TextView) dialog_premium.findViewById(R.id.dialog_txt1);
        TextView dialog_txt2 = (TextView) dialog_premium.findViewById(R.id.dialog_txt2);
        TextView dialog_txt3 = (TextView) dialog_premium.findViewById(R.id.dialog_txt3);
        TextView dialog_txt4 = (TextView) dialog_premium.findViewById(R.id.dialog_txt4);
        TextView cancel = (TextView) dialog_premium.findViewById(R.id.cancel);
        TextView get_code = (TextView) dialog_premium.findViewById(R.id.get_code);

        dialog_txt.setTypeface(type);
        dialog_txt1.setTypeface(type);
        dialog_txt2.setTypeface(type);
        dialog_txt3.setTypeface(type);
        dialog_txt4.setTypeface(type);
        cancel.setTypeface(type);
        get_code.setTypeface(type);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_premium.dismiss();
            }
        });
        get_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Global.isLimited = false;
                dialog_premium.dismiss();
                Toast.makeText(getApplicationContext(), "تبریک! شما کاربر ویژه شدید", Toast.LENGTH_SHORT).show();
                G.isLimited = false;
//                Intent intent = new Intent(ActivityGoPremium.this,ActivityStore.class);
//                ActivityGoPremium.this.startActivity(intent);
                finish();
//                if (G.isLimited) {
//                    houseTemperatureCard.setVisibility(View.GONE);
//                    houseToolsCard.setVisibility(View.GONE);
//                    houseBuildCard.setVisibility(View.GONE);
//                    houseUnitCard.setVisibility(View.GONE);
//                    houseUnitDesc.setVisibility(View.GONE);
//                } else {
//                    houseTemperatureCard.setVisibility(View.VISIBLE);
//                    houseToolsCard.setVisibility(View.VISIBLE);
//                    houseBuildCard.setVisibility(View.VISIBLE);
//                    houseUnitCard.setVisibility(View.VISIBLE);
//                    houseUnitDesc.setVisibility(View.VISIBLE);
//                }
            }
        });


        // Toolbar Buttons
        ImageView mImageViewBack = (ImageView) findViewById(R.id.btnNavigation);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTextView_Header = (TextView) findViewById(R.id.toolbar_main_tv_header);
        mTextView_Header.setTypeface(type);
        mTextView_Header.setText("ورود کد ارتقا");
        TextView txt = (TextView) findViewById(R.id.txt);
        TextView txt1 = (TextView) findViewById(R.id.txt1);
        EditText txt2 = (EditText) findViewById(R.id.txt2);
        TextView enter = (TextView) findViewById(R.id.enter);
        TextView no_code = (TextView) findViewById(R.id.no_code);

        txt.setTypeface(type);
        txt1.setTypeface(type);
        txt2.setTypeface(type);
        enter.setTypeface(type_roboto);
        no_code.setTypeface(type_roboto);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "کد وارد شده صحیح نیست", Toast.LENGTH_SHORT).show();
            }
        });
        no_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_premium.show();
            }
        });


    }
}
