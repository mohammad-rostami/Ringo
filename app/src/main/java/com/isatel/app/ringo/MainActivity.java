package com.isatel.app.ringo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.ronash.pushe.Pushe;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements FragmentDownload.OnFragmentInteractionListener, FragmentBank.OnFragmentInteractionListener, FragmentArchive.OnFragmentInteractionListener, FragmentNavigation.OnFragmentInteractionListener {
    protected static final int REQUEST_CAMERA = 111;
    protected static final int SELECT_FILE = 222;
    public static Context context;
    private static int[] images = new int[]{R.drawable.banner1, R.drawable.banner2, R.drawable.banner3};
    private static DrawerLayout drawer;
    private static ViewPager mViewPager;
    public String[] mStr_TabNames;
    G mGlobal;
    File f;
    Bitmap m_bitmap1;
    Timer timer;
    int page = 1;
    //    AdView adView;
    private TextView mTextView_Header;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TextView Premium;
    private LinearLayout PremiumLayout;
    private LinearLayout mLinearLayout_Downloads;
    private LinearLayout mLinearLayout_Bank;
    private LinearLayout mLinearLayout_Cut;
    private LinearLayout mLinearLayout_Archive;
    private ImageView mImageView_Downloads;
    private ImageView mImageView_Bank;
    private ImageView mImageView_Cut;
    private ImageView mImageView_Archive;
    private TextView mTextView_Downloads;
    private TextView mTextView_Bank;
    private TextView mTextView_Cut;
    private TextView mTextView_Archive;

    public static void ViewPagerTabSelector(int tabNumber) {
        mViewPager.setCurrentItem(tabNumber);
    }

    public static void tabSwitcher(int num) {
        drawer.closeDrawers();
        mViewPager.setCurrentItem(num);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FragmentDownload.pause();
        FragmentBank.pause();
        FragmentArchive.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!G.isLimited) {
            try {
                PremiumLayout.setVisibility(View.GONE);
                FragmentNavigation.isPremium();
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        context = getApplicationContext();

        Pushe.initialize(this, true);

        Typeface type = Typeface.createFromAsset(getAssets(), "iran_sans_regular.ttf");
        Typeface type_roboto = Typeface.createFromAsset(getAssets(), "roboto_light.ttf");
        Typeface type_roboto_regular = Typeface.createFromAsset(getAssets(), "roboto_regular.ttf");

        TextView bank = (TextView) findViewById(R.id.bank);
        TextView cut = (TextView) findViewById(R.id.cut);
        TextView folder_text = (TextView) findViewById(R.id.folder_text);
        bank.setTypeface(type);
        cut.setTypeface(type);
        folder_text.setTypeface(type);


        PremiumLayout = (LinearLayout) findViewById(R.id.premium_layout);
        Premium = (TextView) findViewById(R.id.premium);
        TextView Premium1 = (TextView) findViewById(R.id.premium1);
        TextView Premium2 = (TextView) findViewById(R.id.premium2);
        Premium.setTypeface(type);
        Premium1.setTypeface(type);
        Premium2.setTypeface(type);

        Premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityGoPremium.class);
                MainActivity.this.startActivity(intent);
            }
        });

        // Toolbar Buttons
        mTextView_Header = (TextView) findViewById(R.id.toolbar_main_tv_header);
//        mTextView_Header.setTypeface(typeFace_Medium);
        mTextView_Header.setText("R I N G O");
        mTextView_Header.setTypeface(type_roboto_regular);
        ImageView btnNavigation = (ImageView) findViewById(R.id.btnNavigation);
        btnNavigation.setImageResource(R.mipmap.menu);
        btnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.RIGHT);
            }
        });


        // Image Slider and its Indicator Implementation
//        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//        mViewPager = (ViewPager) findViewById(R.id.vp_slider);
//        mViewPager.setAdapter(mSectionsPagerAdapter);
//        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
//        indicator.setViewPager(mViewPager);
//
//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
////                if (state < images.length) {
////                    //intro_images is viewpager
////                }else {
////                    mViewPager.setCurrentItem(0, true);
////
////                }
//            }
//        });
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                pageSwitcher(10);
//            }
//        }, 5000);


        //main ViewPager Codes
        // Defining Bottom navigation Views
        mLinearLayout_Downloads = (LinearLayout) findViewById(R.id.toolbar_icons_ll_downloads);
        mLinearLayout_Bank = (LinearLayout) findViewById(R.id.toolbar_icons_ll_bank);
        mLinearLayout_Cut = (LinearLayout) findViewById(R.id.toolbar_icons_ll_cut);
        mLinearLayout_Archive = (LinearLayout) findViewById(R.id.toolbar_icons_ll_archive);

        mImageView_Downloads = (ImageView) findViewById(R.id.toolbar_icons_img_downloads);
        mImageView_Bank = (ImageView) findViewById(R.id.toolbar_icons_img_bank);
        mImageView_Cut = (ImageView) findViewById(R.id.toolbar_icons_img_cut);
        mImageView_Archive = (ImageView) findViewById(R.id.toolbar_icons_img_archive);
        mTextView_Downloads = (TextView) findViewById(R.id.toolbar_icons_txt_downloads);
        mTextView_Bank = (TextView) findViewById(R.id.toolbar_icons_txt_bank);
        mTextView_Cut = (TextView) findViewById(R.id.toolbar_icons_txt_cut);
        mTextView_Archive = (TextView) findViewById(R.id.toolbar_icons_txt_archive);

        // View pager
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.activity_main_vp_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setPageMargin(50);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        //Download Tab Clicked

                        SetBottomNavigationIcon(0);
//                        setBottomNavigationZoom(mLinearLayout_Tour);
////                        setBottomNavigationColor(R.color.lightBlue);
//                        mEditText_Search.setHint("جستجو در تورها");
                        break;
                    case 1:
                        //Bank Tab Clicked


                        SetBottomNavigationIcon(1);
//                        mEditText_Search.setHint("جستجو در مسکن");
//                        setBottomNavigationColor(R.color.lightGreen);
//                        setBottomNavigationZoom(mLinearLayout_House);

                        break;
                    case 2:
                        //Archive Tab Clicked


                        SetBottomNavigationIcon(2);
//                        mEditText_Search.setHint("جستجو در خودرو");
////                        setBottomNavigationColor(R.color.lightOrange);
//                        setBottomNavigationZoom(mLinearLayout_Car);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        // changing tabs when clicking bottom nav items
        mLinearLayout_Downloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });
        mLinearLayout_Bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
            }
        });
        mLinearLayout_Cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RingtoneSelectActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        mLinearLayout_Archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(2);
            }
        });

        // Navigation Drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Fragment squadFragment = new FragmentNavigation();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.nav_view, squadFragment, null);
        fragmentTransaction.commit();

        mGlobal = ((G) getApplication());


//        adView = (AdView) this.findViewById(R.id.ads);


        // Load ads into Banner Ads
//        adView.loadAd(adRequest);

        // Load ads into Interstitial Ads

        // Prepare an Interstitial Ad Listener

//        interstitial = new InterstitialAd(this);
//        interstitial.setAdUnitId(getResources().getString(R.string.admob_intersitials));
//        interstitial.loadAd(adRequest);

        // Load ads into Interstitial Ads
//        interstitial.setAdListener(new AdListener() {
//            public void onAdLoaded() {
//                // Call displayInterstitial() function
//                interstitial.show();
//
//            }
//        });


//        try {
//            adView.loadAd(adRequest);
//        } catch (Exception e) {
//        }
        findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, ActivityStore.class);
                MainActivity.this.startActivity(intent);

//                Intent intent = new Intent(
//                        MediaStore.ACTION_IMAGE_CAPTURE);
//                File f = new File(Environment
//                        .getExternalStorageDirectory(), "temp.jpg");
//                intent.putExtra(MediaStore.EXTRA_OUTPUT,
//                        Uri.fromFile(f));
//                startActivityForResult(intent, REQUEST_CAMERA);

            }
        });

        findViewById(R.id.galery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, RingtoneSelectActivity.class);
                MainActivity.this.startActivity(intent);
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.setType("image/*");
//                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
            }
        });

        findViewById(R.id.folder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, ActivityEditedRings.class);
                MainActivity.this.startActivity(intent);
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.setType("image/*");
//                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // TODO Auto-generated method stub
//
//        // /==========================
//
//        if (resultCode == RESULT_OK) {
//
//            final Intent i = new Intent(MainActivity.this, SelectedImageActivity.class);
//
//
//            if (requestCode == REQUEST_CAMERA) {
//                f = new File(Environment.getExternalStorageDirectory()
//                        .toString());
//                for (File temp : f.listFiles()) {
//                    if (temp.getName().equals("temp.jpg")) {
//                        f = temp;
//                        break;
//                    }
//                }
//                try {
//
//                    int rotate = 0;
//                    ExifInterface exif;
//                    try {
//                        exif = new ExifInterface(
//                                f.getAbsolutePath());
//                        int orientation = exif.getAttributeInt(
//                                ExifInterface.TAG_ORIENTATION,
//                                ExifInterface.ORIENTATION_NORMAL);
//                        switch (orientation) {
//                            case ExifInterface.ORIENTATION_ROTATE_270:
//                                rotate = 270;
//                                break;
//                            case ExifInterface.ORIENTATION_ROTATE_180:
//                                rotate = 180;
//                                break;
//                            case ExifInterface.ORIENTATION_ROTATE_90:
//                                rotate = 90;
//                                break;
//                        }
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//
//                    final int finalRotate = rotate;
//
//                    m_bitmap1 = BitmapFactory.decodeFile(f.getAbsolutePath());
//                    int nh = (int) (m_bitmap1.getHeight() * (400.0 / m_bitmap1.getWidth()));
//                    m_bitmap1 = Bitmap.createScaledBitmap(m_bitmap1, 400, nh, true);
//
//                    Matrix matrix = new Matrix();
//                    matrix.postRotate(finalRotate);
//
//                    mGlobal.setImage(Bitmap.createBitmap(m_bitmap1, 0, 0, m_bitmap1.getWidth(), m_bitmap1.getHeight(), matrix, true));
//
//                    startActivity(i);
//                    finish();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            } else if (requestCode == SELECT_FILE) {
//                Uri selectedImageUri = data.getData();
//
//                try {
//
//                    f = FileUtils.getFile(this, (selectedImageUri));
//
//
//                    Glide.with(this).load(f)
//                            .asBitmap()
//                            .into(new SimpleTarget<Bitmap>(400, 400) {
//                                @Override
//                                public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
//
//                                    mGlobal.setImage(bitmap);
//                                    startActivity(i);
//                                    finish();
//                                }
//                            });
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
    }

    @Override
    protected void onDestroy() {


        try {
            m_bitmap1.recycle();
            m_bitmap1 = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.gc();
        super.onDestroy();


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void pageSwitcher(int seconds) {
        timer = new Timer(); // At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000); // delay
    }

    // Bottom navigation item selector
    private void SetBottomNavigationIcon(int Page) {
        switch (Page) {
            case 0:
//                mImageView_Tour.setColorFilter(getResources().getColor(R.color.pink));
//                mTextView_Tour.setTextColor(getResources().getColor(R.color.pink));
//                mImageView_Car.setColorFilter(getResources().getColor(R.color.grayDisabled));
//                mTextView_Car.setTextColor(getResources().getColor(R.color.grayDisabled));
//                mImageView_House.setColorFilter(getResources().getColor(R.color.grayDisabled));
//                mTextView_House.setTextColor(getResources().getColor(R.color.grayDisabled));
//                mLinearLayout_Tour.setAlpha(1);
//                mLinearLayout_House.setAlpha(0.6f);
//                mLinearLayout_Car.setAlpha(0.6f);

//                mLayout_bottom.setBackgroundColor(getResources().getColor(R.color.pink));
                mLinearLayout_Downloads.setAlpha(1);
                mLinearLayout_Bank.setAlpha(0.6f);
                mLinearLayout_Cut.setAlpha(0.6f);
                mLinearLayout_Archive.setAlpha(0.6f);
                break;
            case 1:
//                mLinearLayout_Downloads.setColorFilter(getResources().getColor(R.color.grayDisabled));
//                mLinearLayout_Bank.setTextColor(getResources().getColor(R.color.grayDisabled));
//                mLinearLayout_Cut.setColorFilter(getResources().getColor(R.color.grayDisabled));
//                mLinearLayout_Archive.setTextColor(getResources().getColor(R.color.grayDisabled));
//                mTextView_House.setTextColor(getResources().getColor(R.color.gold));
//                mLinearLayout_Tour.setAlpha(0.6f);
//                mLinearLayout_House.setAlpha(1);
//                mLinearLayout_Car.setAlpha(0.6f);
//                mLayout_bottom.setBackgroundColor(getResources().getColor(R.color.gold));

                mLinearLayout_Downloads.setAlpha(0.6f);
                mLinearLayout_Bank.setAlpha(1);
                mLinearLayout_Cut.setAlpha(0.6f);
                mLinearLayout_Archive.setAlpha(0.6f);

                break;
            case 2:
//                mImageView_Tour.setColorFilter(getResources().getColor(R.color.grayDisabled));
//                mTextView_Tour.setTextColor(getResources().getColor(R.color.grayDisabled));
//                mImageView_Car.setColorFilter(getResources().getColor(R.color.green));
//                mTextView_Car.setTextColor(getResources().getColor(R.color.green));
//                mImageView_House.setColorFilter(getResources().getColor(R.color.grayDisabled));
//                mTextView_House.setTextColor(getResources().getColor(R.color.grayDisabled));
//                mLinearLayout_Tour.setAlpha(0.6f);
//                mLinearLayout_House.setAlpha(0.6f);
//                mLinearLayout_Car.setAlpha(1);
//                mLayout_bottom.setBackgroundColor(getResources().getColor(R.color.green));

                mLinearLayout_Downloads.setAlpha(0.6f);
                mLinearLayout_Bank.setAlpha(0.6f);
                mLinearLayout_Cut.setAlpha(0.6f);
                mLinearLayout_Archive.setAlpha(1);
                break;
        }
    }

    private void setBottomNavigationZoom(LinearLayout ZoomIn) {
//        next.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.nav_zoom_out));
//
//        Animation zoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.nav_zoom_in);
//        zoom.setFillAfter(true);
//        ZoomIn.startAnimation(zoom);
//        next = ZoomIn;
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

    // Image slider Adapter
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        //sets tab names
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mStr_TabNames = new String[]{"دانلودها", "بانک رینگو", "برش", "آرشیو"};
        }

        // sets tab content (fragment)
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragmentDownload();
                case 1:
                    return new FragmentBank();
                case 2:
                    return new FragmentArchive();
//          case 2:
//                    return new FragmentAds_Car();
            }
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mStr_TabNames[position];
        }

    }

    // this is an inner class...
    class RemindTask extends TimerTask {

        @Override
        public void run() {

            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            runOnUiThread(new Runnable() {
                public void run() {

                    if (page > 3) { // In my case the number of pages are 5
                        page = 0;
                        mViewPager.setCurrentItem(page++);
//                        timer.cancel();
                        // Showing a toast for just testing purpose
//                        Toast.makeText(getApplicationContext(), "Timer stoped", Toast.LENGTH_LONG).show();
                    } else {
                        mViewPager.setCurrentItem(page++);
                    }
                }
            });

        }
    }
}
