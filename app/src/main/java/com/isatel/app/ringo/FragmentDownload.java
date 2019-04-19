package com.isatel.app.ringo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.SeekBar;
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

public class FragmentDownload extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_CODE_CHOOSE_CONTACT = 2;
    public static ArrayList<Struct> arrayList = new ArrayList<>();
    public static Cursor cursor;
    public static MediaPlayer mediaPlayer = null;
    public static boolean playPause = false;
    private static Adapter_Recycler adapter_recycler;
    private static TextView Empty;
    private static ImageView play;
    private static ImageView pause;
    public String[] mStr_TabNames;
    public boolean intialStage = true;
    public ProgressDialog mProgressDialog;
    public Dialog dialog_premium;
    public Dialog dialog_Ringtone;
    public TextView load;
    public String path;
    //    public ArrayList<Struct> mArray_Structs = new ArrayList<>();
//    public RecyclerView.Adapter mRecyclerAdapter_Adapter;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private String mStr_packages;
    private TextView mTextView_Header;
    private DownloadTask downloadTask;
    private ViewPager mViewPager;
    private LinearLayout back;
    private LinearLayout tools;
    private FrameLayout player;
    private TextView playTitle;
    private LinearLayout share;
    private LinearLayout save;
    private LinearLayout set;
    private LinearLayout toolbar;
    private String file_title;
    private int filePosition;
    private String file_id;
    private ProgressBar mSeekBar;
    private RecyclerView recyclerView;
    private int i = 0;

    public FragmentDownload() {
        // Required empty public constructor
    }

    public static FragmentDownload newInstance(String param1, String param2) {
        FragmentDownload fragment = new FragmentDownload();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static void memoryScan() {
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        ContentResolver contentResolver = G.context.getContentResolver();
        cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Audio.Media.DATA + " like ? ", new String[]{"%ringo%"}, null);
        try {
            arrayList.clear();
            adapter_recycler.notifyDataSetChanged();
        } catch (Exception e) {
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                if (cursor != null && cursor.moveToFirst()) {
                    int songId = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                    int songName = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                    int songTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    int songArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int songAlbum = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                    int path = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);


                    do {
                        long currentId = cursor.getLong(songId);
                        String name = cursor.getString(songName);
                        String currentTitle = cursor.getString(songTitle);
                        String filePath = cursor.getString(path);
                        String fileArtist = cursor.getString(songArtist);
                        String fileAlbum = cursor.getString(songArtist);
                        Log.d("title", currentTitle);
                        Log.d("artist", fileArtist);
                        Log.d("album", fileAlbum);
                        Log.d("path", filePath);

                        String Name = "";
                        Name = dataBaseChecker("downloadedFiles", "name", "id", name);

                        if (Name != null) {
                            if (!Name.equals("")) {
                                Struct struct = new Struct();
                                struct.strTitle = Name;
                                struct.strArtist = fileArtist;
                                struct.state = false;
                                struct.isPlaying = false;
                                struct.path = filePath;
                                struct.Id = "1";
                                arrayList.add(struct);
                            }
                        }
//                arrayList.add(new Songs(currentId, currentTitle, currentArtist));
                    } while (cursor.moveToNext());

                    try {
                        if (arrayList.size() > 0) {
                            Empty.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                    }
              /*retrieve data from database */

                }
                try {
//            Toast.makeText(G.context, "hi " + String.valueOf(arrayList.size()), Toast.LENGTH_SHORT).show();
                    adapter_recycler.notifyDataSetChanged();
                } catch (Exception e) {
                }
            }
        }, 300);
    }

    public static String dataBaseChecker(String TableName, String column, String columnName, String amount) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(G.context);
        Cursor cursor = null;
        String title = null;
        try {
            dataBaseHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        SQLiteDatabase sqld = dataBaseHelper.getWritableDatabase();
        try {
            cursor = sqld.rawQuery("select * from " + TableName + " where " + columnName + "=\"" + amount + "\"", null);

            if (cursor.moveToFirst()) {
                do {
                    title = cursor.getString(cursor.getColumnIndex(column));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
        }

        sqld.close();
        return title;
    }

    public static void pause() {
        pause.setVisibility(View.GONE);
        play.setVisibility(View.VISIBLE);

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        playPause = false;

        memoryScan();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        // premium Dialog
        dialog_Ringtone = new Dialog(getActivity());
        dialog_Ringtone.setContentView(R.layout.after_save_action);
        dialog_Ringtone.setTitle("Custom Dialog");
        TextView button_make_default = (TextView) dialog_Ringtone.findViewById(R.id.button_make_default);
        TextView button_choose_contact = (TextView) dialog_Ringtone.findViewById(R.id.button_choose_contact);
        TextView button_do_nothing = (TextView) dialog_Ringtone.findViewById(R.id.button_do_nothing);
        button_make_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RingtoneManager.setActualDefaultRingtoneUri(
                        getActivity(),
                        RingtoneManager.TYPE_RINGTONE,
                        Uri.parse(new File(path).toString()));
                dialog_Ringtone.dismiss();

            }
        });
        button_choose_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseContactForRingtone(Uri.parse(new File(path).toString()));
                dialog_Ringtone.dismiss();

            }
        });
        button_do_nothing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_Ringtone.dismiss();
            }
        });

        // premium Dialog
        dialog_premium = new Dialog(getActivity());
        dialog_premium.setContentView(R.layout.dialog_premium);
        dialog_premium.setTitle("Custom Dialog");
        TextView cancel = (TextView) dialog_premium.findViewById(R.id.cancel);
        TextView get_code = (TextView) dialog_premium.findViewById(R.id.get_code);

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

//        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        memoryScan();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download, container, false);

        mSeekBar = (ProgressBar) view.findViewById(R.id.seekBar);
//        mSeekBar.getProgressDrawable().setColorFilter(Color.parseColor("#d6074f"), PorterDuff.Mode.MULTIPLY);
        Empty = (TextView) view.findViewById(R.id.loading);
        if (arrayList.size() > 0) {
            Empty.setVisibility(View.GONE);
        }
        toolbar = (LinearLayout) view.findViewById(R.id.toolbar);
        tools = (LinearLayout) view.findViewById(R.id.tools);
        player = (FrameLayout) view.findViewById(R.id.player);
        playTitle = (TextView) view.findViewById(R.id.play_title);
        play = (ImageView) view.findViewById(R.id.play);
        pause = (ImageView) view.findViewById(R.id.pause);

//        bookmarkImg = (ImageView) view.findViewById(R.id.bookmark_img);
//        likeImg = (ImageView) view.findViewById(R.id.like_img);

        share = (LinearLayout) view.findViewById(R.id.share);
        save = (LinearLayout) view.findViewById(R.id.save);
        set = (LinearLayout) view.findViewById(R.id.set);

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
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_Ringtone.show();

            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentArchive.pause();
                FragmentBank.pause();

                playPause = true;
                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);

                new Player().execute(path);
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


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        load = (TextView) view.findViewById(R.id.loading);
        back = (LinearLayout) view.findViewById(R.id.toolbar_main_tb_main);
        back.setBackgroundColor(Color.parseColor("#c4c4c4"));
        ImageView btnNavigation = (ImageView) view.findViewById(R.id.btnNavigation);
        btnNavigation.setVisibility(View.GONE);
//        new Async().execute("http://telegramgard.ir/rington/index.php/mobile/index/search/0/15", "1266");

        recyclerView = (RecyclerView) view.findViewById(R.id.music_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        adapter_recycler = new Adapter_Recycler(getContext(), arrayList, new OnItemListener() {
            @Override
            public void onItemSelect(int position) {
                String file_id = arrayList.get(position).Id;

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

                toolbar.setVisibility(View.GONE);
                tools.setVisibility(View.VISIBLE);
                player.setVisibility(View.VISIBLE);
                mSeekBar.setVisibility(View.VISIBLE);

                file_id = arrayList.get(position).Id;
                filePosition = position;
                file_title = arrayList.get(position).strTitle;

                playTitle.setText(file_title);
                path = arrayList.get(position).path;
//                if (!playPause) {
//                    FragmentArchive.pause();
//                    FragmentBank.pause();
//
//                    playPause = true;
//                    pause.setVisibility(View.VISIBLE);
//                    play.setVisibility(View.GONE);
//
//                    new Player().execute(path);
//                } else {
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

                    new Player().execute(path);
                    playerRefresh(true, position);
                }
//                    playPause = false;
//                }
            }

            @Override
            public void onItemLock(int position) {
                dialog_premium.show();
            }
        }, 4, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter_recycler);

        final Handler mHandler = new Handler();
//Make sure you update Seekbar on UI thread
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    mSeekBar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
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

    private void chooseContactForRingtone(Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_EDIT, uri);
//            intent.setClassName("com.isatel.app.ringo", "ChooseContactActivity");
            intent.setClass(getContext(), ChooseContactActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_CONTACT);
        } catch (Exception e) {
            Log.e("Ringtone", "Couldn't open Choose Contact window");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pause();
    }

    public void playerRefresh(boolean playing, int Position) {
        arrayList.clear();
        adapter_recycler.notifyDataSetChanged();

        i = 0;
        if (cursor != null && cursor.moveToFirst()) {
            int songId = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songName = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int songTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songAlbum = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int path = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                long currentId = cursor.getLong(songId);
                String name = cursor.getString(songName);
                String currentTitle = cursor.getString(songTitle);
                String filePath = cursor.getString(path);
                String fileArtist = cursor.getString(songArtist);
                String fileAlbum = cursor.getString(songArtist);
                Log.d("title", currentTitle);
                Log.d("artist", fileArtist);
                Log.d("album", fileAlbum);
                Log.d("path", filePath);

                String Name = "";
                Name = dataBaseChecker("downloadedFiles", "name", "id", name);

                if (Name != null) {
                    if (!Name.equals("")) {
                        Struct struct = new Struct();
//                        i = i++;
                        struct.strTitle = Name;
                        struct.strArtist = fileArtist;
                        struct.state = false;
                        if (playing) {
                            if (i++ == Position) {
                                struct.isPlaying = true;
                            } else {
                                struct.isPlaying = false;
                            }
                        } else {
                            struct.isPlaying = false;
                        }
                        struct.path = filePath;
                        struct.Id = "1";
                        arrayList.add(struct);
                    }
                }
//                arrayList.add(new Songs(currentId, currentTitle, currentArtist));
            } while (cursor.moveToNext());
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

                Uri uri = Uri.parse(path);
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(getContext(), uri);
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int millSecond = Integer.parseInt(durationStr);
                mSeekBar.setMax(millSecond / 1000);

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
                String save_path = sUrl[1];
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
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
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
                    struct.Id = id;

                    File music = new File("/sdcard/ringo/" + id + ".mp3");
                    if (music.exists()) {
                        //Do something
                        struct.state = true;
                    } else {
                        struct.state = false;

                        // Do something else.
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
}
