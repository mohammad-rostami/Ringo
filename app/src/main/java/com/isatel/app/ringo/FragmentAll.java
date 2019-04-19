package com.isatel.app.ringo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;

public class FragmentAll extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public String[] mStr_TabNames;
    public MediaPlayer mediaPlayer = null;
    public boolean playPause = false;
    public boolean intialStage = true;
    public ArrayList<Struct> arrayList = new ArrayList<>();
    public ProgressDialog mProgressDialog;
    public Dialog dialog_premium;
    //    public ArrayList<Struct> mArray_Structs = new ArrayList<>();
//    public RecyclerView.Adapter mRecyclerAdapter_Adapter;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private String mStr_packages;
    private Adapter_Recycler adapter_recycler;
    private TextView mTextView_Header;
    private DownloadTask downloadTask;
    private ViewPager mViewPager;
    public TextView load;

    public FragmentAll() {
        // Required empty public constructor
    }

    public static FragmentAll newInstance(String param1, String param2) {
        FragmentAll fragment = new FragmentAll();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bank, container, false);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        load = (TextView) view.findViewById(R.id.loading);
        new Async().execute("http://telegramgard.ir/rington/index.php/mobile/index/search/0/15", "1266");

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.music_list);
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
                String file_id = arrayList.get(position).Id;
                if (!playPause) {
                    playPause = true;
//                    if (intialStage)
//                    new Player().execute("http://telegramgard.ir/rington/index.php/musics/getMuiscFile/" + file_id);
                    new Player().execute("http://telegramgard.ir/rington/index.php/musics/getMuiscFile/" + file_id);
//                    else {
//                        if (!mediaPlayer.isPlaying())
//                            mediaPlayer.start();
//                    }
                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
//                        try {
//                            mediaPlayer.prepare();
//                        } catch (IOException e) {
//
//
//                        }
//                        mediaPlayer = null;
                    }
                    playPause = false;

                }


            }

            @Override
            public void onItemLock(int position) {
                dialog_premium.show();
            }
        }, 1, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter_recycler);

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
                if (data.length()<1){
                    load.setVisibility(View.VISIBLE);
                    load.setText("فعلا اینجا هیچی نیس!");
                }else {
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
