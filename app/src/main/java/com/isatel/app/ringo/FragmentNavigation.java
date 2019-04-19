package com.isatel.app.ringo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;

public class FragmentNavigation extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static TextView mTextView_Home;
    private static TextView mTextView_Soot;
    private static TextView mTextView_Bookmark;
    //    public ArrayList<Struct> mArray_Structs = new ArrayList<>();
//    public RecyclerView.Adapter mRecyclerAdapter_Adapter;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private String mStr_packages;
    private Dialog dialog;
    private LinearLayout mLinearLayout_Main;
    private Typeface type;
    private static TextView Premium;
    private Typeface type_roboto;
    private TextView mTextView_ProShot;
    public Dialog dialog_premium;

    public FragmentNavigation() {
        // Required empty public constructor
    }

    public static FragmentNavigation newInstance(String param1, String param2) {
        FragmentNavigation fragment = new FragmentNavigation();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static void buttonSelection(Context context, String page) {
        switch (page) {
            case "home":
                mTextView_Home.setTextColor(context.getResources().getColor(R.color.red));
                mTextView_Soot.setTextColor(context.getResources().getColor(R.color.sky_black));
                mTextView_Bookmark.setTextColor(context.getResources().getColor(R.color.sky_black));
                mTextView_Home.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text2));
                mTextView_Soot.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text2));
                mTextView_Bookmark.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text2));
                break;
            case "soot":
                mTextView_Home.setTextColor(context.getResources().getColor(R.color.sky_black));
                mTextView_Soot.setTextColor(context.getResources().getColor(R.color.purple2));
                mTextView_Bookmark.setTextColor(context.getResources().getColor(R.color.sky_black));
                mTextView_Home.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text2));
                mTextView_Soot.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text2));
                mTextView_Bookmark.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text2));
                break;
            case "bookmark":
                mTextView_Home.setTextColor(context.getResources().getColor(R.color.sky_black));
                mTextView_Soot.setTextColor(context.getResources().getColor(R.color.sky_black));
                mTextView_Bookmark.setTextColor(context.getResources().getColor(R.color.navyBlue));
                mTextView_Home.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text2));
                mTextView_Soot.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text2));
                mTextView_Bookmark.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text2));
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        type = Typeface.createFromAsset(getActivity().getAssets(), "iran_sans_regular.ttf");
        type_roboto = Typeface.createFromAsset(getActivity().getAssets(), "roboto_light.ttf");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        // Contact dialog_contact
//        dialog = new Dialog(getActivity());
//        dialog.setContentView(R.layout.dialog_premium);
//        dialog.setTitle("Custom Dialog");
//        TextView premium_close = (TextView) dialog.findViewById(R.id.premium_dismiss);
//        premium_close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });

        mLinearLayout_Main = (LinearLayout) view.findViewById(R.id.fragment_data_rv_table);
        mTextView_ProShot = (TextView) view.findViewById(R.id.proshot);
        mTextView_ProShot.setTypeface(type_roboto);
//        int softBar = ActivityHome.softBar;
//        float scale = getResources().getDisplayMetrics().density;
//        int dpAsPixels = (int) (46 * scale + 0.5f);
//        mLinearLayout_Main.setPadding(0, dpAsPixels, 0, softBar);
//        Toast.makeText(getContext(), softBar, Toast.LENGTH_SHORT).show();

        TextView premium = (TextView)view.findViewById(R.id.premium);
        premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TextView mTextView_Itemfavorite = (TextView) view.findViewById(R.id.favorite_txt);
        TextView mTextView_Itembookmark = (TextView) view.findViewById(R.id.bookmark_txt);
        TextView mTextView_Itemarchive = (TextView) view.findViewById(R.id.archive_txt);
        TextView mTextView_Itemdownload = (TextView) view.findViewById(R.id.download_txt);
        TextView mTextView_ItemContact = (TextView) view.findViewById(R.id.fragment_nav_item_contact);
        TextView mTextView_ItemAbout = (TextView) view.findViewById(R.id.fragment_nav_item_about);
        TextView mTextView_ItemRule = (TextView) view.findViewById(R.id.fragment_nav_item_rule);
        Premium = (TextView)view.findViewById(R.id.premium);
        mTextView_Itemfavorite.setTypeface(type);
        mTextView_Itembookmark.setTypeface(type);
        mTextView_Itemarchive.setTypeface(type);
        mTextView_Itemdownload.setTypeface(type);
        mTextView_ItemContact.setTypeface(type);
        mTextView_ItemAbout.setTypeface(type);
        mTextView_ItemRule.setTypeface(type);
        Premium.setTypeface(type);

        Premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityGoPremium.class);
                getActivity().startActivity(intent);
            }
        });

        LinearLayout mLinearLayout_ItemArchive = (LinearLayout) view.findViewById(R.id.archive);
        LinearLayout mLinearLayout_ItemDownload = (LinearLayout) view.findViewById(R.id.download);
        LinearLayout mLinearLayout_ItemContact = (LinearLayout) view.findViewById(R.id.fragment_nav_ll_item_contact);
        LinearLayout mLinearLayout_ItemAbout = (LinearLayout) view.findViewById(R.id.fragment_nav_ll_item_about);
        LinearLayout mLinearLayout_ItemRule = (LinearLayout) view.findViewById(R.id.fragment_nav_ll_item_rule);


        mLinearLayout_ItemArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            MainActivity.tabSwitcher(3);
            }
        });
        mLinearLayout_ItemDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.tabSwitcher(0);

            }
        });
        mLinearLayout_ItemContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityDescriptions.class);
                intent.putExtra("CONTENT", "contact");
                getActivity().startActivity(intent);
                activityFinisher();
            }
        });
        mLinearLayout_ItemAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityDescriptions.class);
                intent.putExtra("CONTENT", "about");
                getActivity().startActivity(intent);
                activityFinisher();
            }
        });
        mLinearLayout_ItemRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Proshot");
                    String sAux = "\nراستی اینو دیدی؟\n\n";
                    sAux = sAux + "https://www.isatelco.com \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void activityFinisher() {
//        if (ActivityHome.homePageIsActivated) {

//        } else {
//            getActivity().finish();
//        }
    }
    public static void isPremium() {
        Premium.setVisibility(View.GONE);
//        if (ActivityHome.homePageIsActivated) {

//        } else {
//            getActivity().finish();
//        }
    }

}
