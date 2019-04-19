package com.isatel.app.ringo;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;


public class ActivityStore extends AppCompatActivity implements FragmentAll.OnFragmentInteractionListener, FragmentRelax.OnFragmentInteractionListener
        , FragmentHappy.OnFragmentInteractionListener, FragmentFilm.OnFragmentInteractionListener, FragmentReligion.OnFragmentInteractionListener
        , FragmentSms.OnFragmentInteractionListener, FragmentAlarm.OnFragmentInteractionListener, FragmentBrand.OnFragmentInteractionListener {
    public String[] mStr_TabNames;
    public MediaPlayer mediaPlayer = null;
    public boolean playPause = false;
    public boolean intialStage = true;

    public ArrayList<Struct> arrayList = new ArrayList<>();
    public ProgressDialog mProgressDialog;
    private Adapter_Recycler adapter_recycler;
    private TextView mTextView_Header;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        Typeface type_roboto_regular = Typeface.createFromAsset(getAssets(), "roboto_regular.ttf");
        Typeface type = Typeface.createFromAsset(getAssets(), "iran_sans_regular.ttf");

        // Toolbar Buttons
        mTextView_Header = (TextView) findViewById(R.id.toolbar_main_tv_header);
//        mTextView_Header.setTypeface(typeFace_Medium);
        mTextView_Header.setText("بانک رینگو");
        mTextView_Header.setTypeface(type
        );

        ImageView mImageViewBack = (ImageView) findViewById(R.id.btnNavigation);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // View pager Adapter
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // View pager
        mViewPager = (ViewPager) findViewById(R.id.activity_main_vp_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setPageMargin(50);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(mViewPager);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        //Returns a new instance of this fragment for the given section number.

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
    }

    // View pager Adapter
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        //sets tab names
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mStr_TabNames = new String[]{"همه", "آرامش بخش", "شاد", "فیلم", "مذهبی", "اس ام اس", "زنگ هشدار", "برند"};
        }

        // sets tab content (fragment)
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragmentAll();
                case 1:
                    return new FragmentRelax();
                case 2:
                    return new FragmentHappy();
                case 3:
                    return new FragmentFilm();
                case 4:
                    return new FragmentReligion();
                case 5:
                    return new FragmentSms();
                case 6:
                    return new FragmentAlarm();
                case 7:
                    return new FragmentBrand();
            }
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 8;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mStr_TabNames[position];
        }

    }
}
