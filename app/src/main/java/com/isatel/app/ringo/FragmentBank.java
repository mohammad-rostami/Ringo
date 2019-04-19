package com.isatel.app.ringo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;

public class FragmentBank extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static MediaPlayer mediaPlayer = null;
    public static boolean playPause = false;
    public static ArrayList<Struct> arrayList = new ArrayList<>();
    public static TextView load;
    public static String results;
    private static Adapter_Recycler adapter_recycler;
    private static ImageView play;
    private static ImageView pause;
    public boolean intialStage = true;
    public ArrayList<Struct> arrayList_cat = new ArrayList<>();
    public ProgressDialog mProgressDialog;
    public Dialog dialog_premium;
    public String[] mStr_TabNames = new String[]{"همه", "آرامش بخش", "شاد", "فیلم", "مذهبی", "اس ام اس", "زنگ هشدار", "برند"};
    public String cat = "";
    //    public ArrayList<Struct> mArray_Structs = new ArrayList<>();
//    public RecyclerView.Adapter mRecyclerAdapter_Adapter;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private String mStr_packages;
    private TextView mTextView_Header;
    private DownloadTask downloadTask;
    private ViewPager mViewPager;
    private Adapter_Recycler adapter_recycler_cat;
    private Dialog dialog_cat;
    private LinearLayout tools;
    private FrameLayout player;
    private TextView playTitle;
    private String file_id;
    private int filePosition;
    private String file_title;
    private LinearLayout download;
    private LinearLayout like;
    private LinearLayout bookmark;
    private LinearLayout share;
    private ImageView bookmarkImg;
    private ImageView likeImg;
    private boolean bookmarked;
    private boolean liked;
    private String file_name;
    private String save_path;
    private SQLiteDatabase myDB;

    public FragmentBank() {
        // Required empty public constructor
    }

    public static FragmentBank newInstance(String param1, String param2) {
        FragmentBank fragment = new FragmentBank();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static void pause() {
        pause.setVisibility(View.GONE);
        play.setVisibility(View.VISIBLE);

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        playPause = false;

        arrayList.clear();
        adapter_recycler.notifyDataSetChanged();
        try {
            JSONArray main = new JSONArray(results);
            JSONArray data = main.getJSONArray(0);
            if (data.length() < 1) {
                load.setVisibility(View.VISIBLE);
                load.setText("فعلا اینجا هیچی نیس!");
            } else {
                load.setVisibility(View.GONE);
            }
            for (int i = 0; i < data.length(); i++) {
                JSONObject file = data.getJSONObject(i);
                String title = file.getString("title");
                String artist = file.getString("artistId");

                JSONArray musicFile = file.getJSONArray("musicFiles");
                JSONObject musicFileOb = musicFile.getJSONObject(0);
                String id = musicFileOb.getString("id");

                Struct struct = new Struct();
                struct.strTitle = title;
                struct.strArtist = artist;
                struct.isPlaying = false;
                struct.Id = id;

                File music = new File("/sdcard/ringo/" + id + ".mp3");
                if (music.exists()) {
                    struct.state = true;
                } else {
                    struct.state = false;
                }
                arrayList.add(struct);
            }
            adapter_recycler.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // premium Dialog
        dialog_premium = new Dialog(getActivity());
        dialog_premium.setContentView(R.layout.dialog_premium);
        dialog_premium.setTitle("Custom Dialog");
        TextView cancel = (TextView) dialog_premium.findViewById(R.id.cancel);
        TextView get_code = (TextView) dialog_premium.findViewById(R.id.get_code);


        // cat Dialog
        dialog_cat = new Dialog(getActivity());
        dialog_cat.setContentView(R.layout.dialog_cat);
        dialog_cat.setTitle("Custom Dialog");
        dialog_cat.setCancelable(false);
        TextView cat_cancel = (TextView) dialog_cat.findViewById(R.id.cancel);
        TextView cat_confirm = (TextView) dialog_cat.findViewById(R.id.confirm);
        cat_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_cat.dismiss();
            }
        });
        cat_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cat.equals("همه")) {
                    new Async().execute("http://telegramgard.ir/rington/index.php/mobile/index/search/0/15", "1266");

                } else if (cat.equals("آرامش بخش")) {
                    new AsyncCat().execute("http://telegramgard.ir/rington/index.php/mobile/index/search/0/15", "1266", "relax");

                } else if (cat.equals("شاد")) {
                    new AsyncCat().execute("http://telegramgard.ir/rington/index.php/mobile/index/search/0/15", "1266", "happy");

                } else if (cat.equals("فیلم")) {
                    new AsyncCat().execute("http://telegramgard.ir/rington/index.php/mobile/index/search/0/15", "1266", "film");

                } else if (cat.equals("مذهبی")) {
                    new AsyncCat().execute("http://telegramgard.ir/rington/index.php/mobile/index/search/0/15", "1266", "religion");

                } else if (cat.equals("اس ام اس")) {
                    new AsyncCat().execute("http://telegramgard.ir/rington/index.php/mobile/index/search/0/15", "1266", "sms");

                } else if (cat.equals("زنگ هشدار")) {
                    new AsyncCat().execute("http://telegramgard.ir/rington/index.php/mobile/index/search/0/15", "1266", "alarm");

                } else if (cat.equals("برند")) {
                    new AsyncCat().execute("http://telegramgard.ir/rington/index.php/mobile/index/search/0/15", "1266", "brand");

                } else {
                    Toast.makeText(getContext(), "خطا", Toast.LENGTH_SHORT).show();
                }
                dialog_cat.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_premium.dismiss();
            }
        });

        get_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityGoPremium.class);
                getActivity().startActivity(intent);
                dialog_premium.dismiss();
                getActivity().finish();
            }
        });


        for (int i = 0; i < mStr_TabNames.length; i++) {
            Struct struct = new Struct();
            struct.strTitle = mStr_TabNames[i];
            if (i == 0) {
                struct.state = true;
            } else {
                struct.state = false;
            }
            arrayList_cat.add(struct);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bank, container, false);

        tools = (LinearLayout) view.findViewById(R.id.tools);
        player = (FrameLayout) view.findViewById(R.id.player);
        playTitle = (TextView) view.findViewById(R.id.play_title);
        play = (ImageView) view.findViewById(R.id.play);
        pause = (ImageView) view.findViewById(R.id.pause);

        bookmarkImg = (ImageView) view.findViewById(R.id.bookmark_img);
        likeImg = (ImageView) view.findViewById(R.id.like_img);

        share = (LinearLayout) view.findViewById(R.id.share);
        bookmark = (LinearLayout) view.findViewById(R.id.bookmark);
        like = (LinearLayout) view.findViewById(R.id.like);
        download = (LinearLayout) view.findViewById(R.id.download);

        share.setOnClickListener(new View.OnClickListener() {
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
                } catch (Exception e) {
                    //e.toString();
                }
            }
        });
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "نشان شد!", Toast.LENGTH_SHORT).show();
                if (bookmarked) {
                    bookmarkImg.setImageResource(R.drawable.bookmark_outline);
                    bookmarked = false;
                } else {
                    bookmarkImg.setImageResource(R.drawable.bookmark_filled);
                    bookmarked = true;
                }

            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "لایک شد!", Toast.LENGTH_SHORT).show();
                if (liked) {
                    likeImg.setImageResource(R.drawable.like_outline);
                    liked = false;
                } else {
                    likeImg.setImageResource(R.drawable.like_filled);
                    liked = true;
                }

            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// instantiate it within the onCreate method
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage("A message");
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(true);

// execute this when the downloader must be fired
                downloadTask = new DownloadTask(getActivity());
                downloadTask.execute("http://telegramgard.ir/rington/index.php/musics/getMuiscFile/" + file_id, file_id + ".mp3");

                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        downloadTask.cancel(true);
                    }
                });
            }
        });

//        play.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pause.setVisibility(View.VISIBLE);
//                play.setVisibility(View.GONE);
//                playPause = true;
//
//                FragmentDownload.pause();
//                FragmentArchive.pause();
//
//                new Player().execute("http://telegramgard.ir/rington/index.php/musics/getMuiscFile/" + file_id);
//
//                Struct struct = new Struct();
//                struct.strTitle = file_title;
//                struct.isPlaying = true;
//                struct.Id = file_id;
//                adapter_recycler.notifyItemChanged(filePosition);
//
//            }
//        });
//        pause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pause.setVisibility(View.GONE);
//                play.setVisibility(View.VISIBLE);
//                playPause = false;
//
//                FragmentDownload.pause();
//                FragmentArchive.pause();
//
//                Struct struct = new Struct();
//                struct.strTitle = file_title;
//                struct.isPlaying = false;
//                struct.Id = file_id;
//                adapter_recycler.notifyItemChanged(filePosition);
//
//                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.stop();
//                    mediaPlayer.reset();
//                }
//
//            }
//        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentArchive.pause();
                FragmentBank.pause();

                playPause = true;
                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);

                new Player().execute("http://telegramgard.ir/rington/index.php/musics/getMuiscFile/" + file_id);
                playerRefresh(true, filePosition);


            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentBank.pause();
                FragmentArchive.pause();


                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);

                mediaPlayer.stop();
                mediaPlayer.reset();

                playerRefresh(false, 0);


            }
        });

        LinearLayout mLinearLayout_Filter = (LinearLayout) view.findViewById(R.id.toolbar_filter_ll_filter);
        mLinearLayout_Filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_cat.show();
            }
        });


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        load = (TextView) view.findViewById(R.id.loading);
        new Async().execute("http://telegramgard.ir/rington/index.php/mobile/index/search/0/15", "1266");

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.music_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        adapter_recycler = new Adapter_Recycler(getContext(), arrayList, new OnItemListener() {
            @Override
            public void onItemSelect(int position) {
                file_id = arrayList.get(position).Id;
                file_name = arrayList.get(position).strTitle;

// instantiate it within the onCreate method
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage("A message");
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(true);

// execute this when the downloader must be fired
                downloadTask = new DownloadTask(getActivity());
                downloadTask.execute("http://telegramgard.ir/rington/index.php/musics/getMuiscFile/" + file_id, file_id + ".mp3");

                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        downloadTask.cancel(true);
                    }
                });
            }

            @Override
            public void onItemClick(int position) {
                tools.setVisibility(View.VISIBLE);
                player.setVisibility(View.VISIBLE);

                filePosition = position;
                file_title = arrayList.get(position).strTitle;
                file_id = arrayList.get(position).Id;

                playTitle.setText(file_title);

//                if (!playPause) {
//                    playPause = true;
//
//                    FragmentDownload.pause();
//                    FragmentArchive.pause();
//
//                    pause.setVisibility(View.VISIBLE);
//                    play.setVisibility(View.GONE);
//
//                    new Player().execute("http://telegramgard.ir/rington/index.php/musics/getMuiscFile/" + file_id);
//                } else {
//                    if (mediaPlayer.isPlaying()) {
//                        FragmentDownload.pause();
//                        FragmentArchive.pause();
//
//                        pause.setVisibility(View.GONE);
//                        play.setVisibility(View.VISIBLE);
//
//                        mediaPlayer.stop();
//                        mediaPlayer.reset();
//                    }
//                    playPause = false;
//                }
                if (mediaPlayer.isPlaying()) {
                    FragmentBank.pause();
                    FragmentArchive.pause();

                    pause.setVisibility(View.GONE);
                    play.setVisibility(View.VISIBLE);

                    mediaPlayer.stop();
                    mediaPlayer.reset();

                    playerRefresh(false, 0);

                } else {
                    FragmentArchive.pause();
                    FragmentBank.pause();

                    playPause = true;
                    pause.setVisibility(View.VISIBLE);
                    play.setVisibility(View.GONE);

                    new Player().execute("http://telegramgard.ir/rington/index.php/musics/getMuiscFile/" + file_id);
                    playerRefresh(true, position);
                }
            }

            @Override
            public void onItemLock(int position) {
                dialog_premium.show();
            }
        }, 3, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter_recycler);

        RecyclerView recyclerView_cat = (RecyclerView) dialog_cat.findViewById(R.id.cat);
        LinearLayoutManager manager_cat = new LinearLayoutManager(getContext());
        adapter_recycler_cat = new Adapter_Recycler(getContext(), arrayList_cat, new OnItemListener() {
            @Override
            public void onItemSelect(int position) {

            }

            @Override
            public void onItemClick(int position) {
                cat = "";
                cat = arrayList_cat.get(position).strTitle;

            }

            @Override
            public void onItemLock(int position) {
            }
        }, 2, false);
        recyclerView_cat.setLayoutManager(manager_cat);
        recyclerView_cat.setAdapter(adapter_recycler_cat);

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

    @Override
    public void onPause() {
        super.onPause();
        pause();
    }

    public void playerRefresh(boolean playing, int Position) {
        arrayList.clear();
        adapter_recycler.notifyDataSetChanged();

        try {
            JSONArray main = new JSONArray(results);
            JSONArray data = main.getJSONArray(0);
            if (data.length() < 1) {
                load.setVisibility(View.VISIBLE);
                load.setText("فعلا اینجا هیچی نیس!");
            } else {
                load.setVisibility(View.GONE);
            }
            for (int i = 0; i < data.length(); i++) {
                JSONObject file = data.getJSONObject(i);
                String title = file.getString("title");
                String artist = file.getString("artistId");

                JSONArray musicFile = file.getJSONArray("musicFiles");
                JSONObject musicFileOb = musicFile.getJSONObject(0);
                String id = musicFileOb.getString("id");

                Struct struct = new Struct();
                struct.strTitle = title;
                struct.strArtist = artist;
                if (playing) {
                    if (i == Position) {
                        struct.isPlaying = true;
                    } else {
                        struct.isPlaying = false;
                    }
                } else {
                    struct.isPlaying = false;
                }
                struct.Id = id;

                File music = new File("/sdcard/ringo/" + id + ".mp3");
                if (music.exists()) {
                    struct.state = true;
                } else {
                    struct.state = false;
                }
                arrayList.add(struct);
            }
            adapter_recycler.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    class Player extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progress;

        public Player() {
            progress = new ProgressDialog(getActivity());
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            Boolean prepared;
            try {

                mediaPlayer.setDataSource(params[0]);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {


                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        intialStage = true;
                        playPause = false;
//                        btn.setBackgroundResource(R.drawable.button_play);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mediaPlayer.prepare();
                prepared = true;
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                Log.d("IllegarArgument", e.getMessage());
                prepared = false;
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                if (progress.isShowing()) {
                    progress.cancel();
                }
                Log.d("Prepared", "//" + result);
                mediaPlayer.start();
                intialStage = false;
            } catch (Exception e) {
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            this.progress.setMessage("Buffering...");
            this.progress.show();

        }
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                save_path = sUrl[1];
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                File saveDirectory = new File("/sdcard/ringo_rings/");
                // have the object build the directory structure, if needed.
                saveDirectory.mkdirs();
                // create a File object for the parent directory
                File wallpaperDirectory = new File("/sdcard/ringo/");
                // have the object build the directory structure, if needed.
                wallpaperDirectory.mkdirs();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream("/sdcard/ringo/" + save_path);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else {
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();

                // create or open database
                myDB = getContext().openOrCreateDatabase("downloadedFiles.sqlite", MODE_PRIVATE, null);

                // create table
                myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                        + "downloadedFiles"
                        + " (id VARCHAR, name VARCHAR);");

                // Insert data to a Table
                myDB.execSQL("INSERT INTO "
                        + "downloadedFiles"
                        + " (id, name)"
                        + " VALUES ('" + save_path + "','" + file_name + "');");

                // notifying media scanner to find new file
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(new File("/sdcard/ringo/" + save_path));
                    scanIntent.setData(contentUri);
                    getContext().sendBroadcast(scanIntent);
                } else {
                    final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                    getContext().sendBroadcast(intent);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FragmentDownload.memoryScan();

                    }
                }, 300);
            }
        }
    }

    private class Async extends Webservice.PostClass {
        @Override
        protected void onPreExecute() {
            load.setVisibility(View.VISIBLE);
            load.setText("درحال بارگذاری!");
        }

        @Override
        protected void onPostExecute(final String result) {
            results = result;
            arrayList.clear();
            adapter_recycler.notifyDataSetChanged();
            try {
                JSONArray main = new JSONArray(result);
                JSONArray data = main.getJSONArray(0);
                if (data.length() < 1) {
                    load.setVisibility(View.VISIBLE);
                    load.setText("فعلا اینجا هیچی نیس!");
                } else {
                    load.setVisibility(View.GONE);
                }
                for (int i = 0; i < data.length(); i++) {
                    JSONObject file = data.getJSONObject(i);
                    String title = file.getString("title");
                    String artist = file.getString("artistId");

                    JSONArray musicFile = file.getJSONArray("musicFiles");
                    JSONObject musicFileOb = musicFile.getJSONObject(0);
                    String id = musicFileOb.getString("id");

                    Struct struct = new Struct();
                    struct.strTitle = title;
                    struct.strArtist = artist;
                    struct.isPlaying = false;
                    struct.Id = id;

                    File music = new File("/sdcard/ringo/" + id + ".mp3");
                    if (music.exists()) {
                        struct.state = true;
                    } else {
                        struct.state = false;
                    }
                    arrayList.add(struct);
                }
                adapter_recycler.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("data", "catched");

            }
        }
    }

    private class AsyncCat extends Webservice.PostClassCategory {
        @Override
        protected void onPreExecute() {
            load.setVisibility(View.VISIBLE);
            load.setText("درحال بارگذاری!");
        }

        @Override
        protected void onPostExecute(final String result) {
            results = result;
            arrayList.clear();
            adapter_recycler.notifyDataSetChanged();
            try {
                JSONArray main = new JSONArray(result);
                JSONArray data = main.getJSONArray(0);
                if (data.length() < 1) {
                    load.setVisibility(View.VISIBLE);
                    load.setText("فعلا اینجا هیچی نیس!");
                } else {
                    load.setVisibility(View.GONE);
                }
                for (int i = 0; i < data.length(); i++) {
                    JSONObject file = data.getJSONObject(i);
                    String title = file.getString("title");
                    String artist = file.getString("artistId");

                    JSONArray musicFile = file.getJSONArray("musicFiles");
                    JSONObject musicFileOb = musicFile.getJSONObject(0);
                    String id = musicFileOb.getString("id");

                    Struct struct = new Struct();
                    struct.strTitle = title;
                    struct.strArtist = artist;
                    struct.isPlaying = false;
                    struct.Id = id;

                    File music = new File("/sdcard/ringo/" + id + ".mp3");
                    if (music.exists()) {
                        struct.state = true;
                    } else {
                        struct.state = false;
                    }
                    arrayList.add(struct);
                }
                adapter_recycler.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
