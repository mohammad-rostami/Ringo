package com.isatel.app.ringo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



import com.isatel.app.ringo.soundfile.CheapSoundFile;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Random;

//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.ByteArrayEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.params.BasicHttpParams;
//import org.apache.http.params.HttpConnectionParams;
//import org.apache.http.params.HttpParams;


public class RingtoneEditActivity extends Activity
        implements MarkerView.MarkerListener,
        WaveformView.WaveformListener {
    /**
     * This is a special intent action that means "edit a sound file".
     */
    public static final String EDIT = "com.isatel.app.ringo.action.EDIT";
    /**
     * Preference names
     */
    public static final String PREF_SUCCESS_COUNT = "success_count";
    public static final String PREF_STATS_SERVER_CHECK =
            "stats_server_check";
    public static final String PREF_STATS_SERVER_ALLOWED =
            "stats_server_allowed";
    public static final String PREF_ERROR_COUNT = "error_count";
    public static final String PREF_ERR_SERVER_CHECK =
            "err_server_check";
    public static final String PREF_ERR_SERVER_ALLOWED =
            "err_server_allowed";
    public static final String PREF_UNIQUE_ID = "unique_id";
    /**
     * Possible codes for PREF_*_SERVER_ALLOWED
     */
    public static final int SERVER_ALLOWED_UNKNOWN = 0;
    public static final int SERVER_ALLOWED_NO = 1;
    public static final int SERVER_ALLOWED_YES = 2;
    // Result codes
    private static final int REQUEST_CODE_RECORD = 1;
    private static final int REQUEST_CODE_CHOOSE_CONTACT = 2;
    public Uri newUri;
    private long mLoadingStartTime;
    private long mLoadingLastUpdateTime;
    private boolean mLoadingKeepGoing;
    private ProgressDialog mProgressDialog;
    private CheapSoundFile mSoundFile;
    private File mFile;
    private String mFilename;
    private String mDstFilename;
    private String mArtist;
    private String mAlbum;
    private String mGenre;
    private String mTitle;
    private int mYear;
    private String mExtension;
    private String mRecordingFilename;
    private int mNewFileKind;
    private Uri mRecordingUri;
    private boolean mWasGetContentIntent;
    private WaveformView mWaveformView;
    private MarkerView mStartMarker;
    private MarkerView mEndMarker;
    private TextView mStartText;
    private TextView mEndText;
    private TextView mInfo;
    private ImageView mPlayButton;
    private ImageView mRewindButton;
    private ImageView mFfwdButton;
    private boolean mKeyDown;
    private String mCaption = "";
    private int mWidth;
    private int mMaxPos;
    private int mStartPos;
    private int mEndPos;
    private boolean mStartVisible;
    private boolean mEndVisible;
    private int mLastDisplayedStartPos;
    private int mLastDisplayedEndPos;
    private int mOffset;
    private int mOffsetGoal;
    private int mFlingVelocity;
    private int mPlayStartMsec;
    private int mPlayStartOffset;
    private int mPlayEndMsec;
    private Handler mHandler;
    private boolean mIsPlaying;
    private MediaPlayer mPlayer;
    private boolean mCanSeekAccurately;
    private boolean mTouchDragging;
    private float mTouchStart;
    private int mTouchInitialOffset;
    private int mTouchInitialStartPos;
    private int mTouchInitialEndPos;
    private long mWaveformTouchStartMsec;
    private float mDensity;
    private int mMarkerLeftInset;
    private int mMarkerRightInset;
    private int mMarkerTopOffset;
    private int mMarkerBottomOffset;
    /**
     * Server url
     */


    //
    // Public methods and protected overrides
    //
    private Runnable mTimerRunnable = new Runnable() {
        public void run() {
            // Updating an EditText is slow on Android.  Make sure
            // we only do the update if the text has actually changed.
            if (mStartPos != mLastDisplayedStartPos &&
                    !mStartText.hasFocus()) {
                mStartText.setText(formatTime(mStartPos));
                mLastDisplayedStartPos = mStartPos;
            }

            if (mEndPos != mLastDisplayedEndPos &&
                    !mEndText.hasFocus()) {
                mEndText.setText(formatTime(mEndPos));
                mLastDisplayedEndPos = mEndPos;
            }

            mHandler.postDelayed(mTimerRunnable, 100);
        }
    };
    private OnClickListener mPlayListener = new OnClickListener() {
        public void onClick(View sender) {
            onPlay(mStartPos);
        }
    };
    private OnClickListener mRewindListener = new OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                int newPos = mPlayer.getCurrentPosition() - 5000;
                if (newPos < mPlayStartMsec)
                    newPos = mPlayStartMsec;
                mPlayer.seekTo(newPos);
            } else {
                mStartMarker.requestFocus();
                markerFocus(mStartMarker);
            }
        }
    };
    private OnClickListener mFfwdListener = new OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                int newPos = 5000 + mPlayer.getCurrentPosition();
                if (newPos > mPlayEndMsec)
                    newPos = mPlayEndMsec;
                mPlayer.seekTo(newPos);
            } else {
                mEndMarker.requestFocus();
                markerFocus(mEndMarker);
            }
        }
    };
    private OnClickListener mMarkStartListener = new OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                mStartPos = mWaveformView.millisecsToPixels(
                        mPlayer.getCurrentPosition() + mPlayStartOffset);
                updateDisplay();
            }
        }
    };
    private OnClickListener mMarkEndListener = new OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                mEndPos = mWaveformView.millisecsToPixels(
                        mPlayer.getCurrentPosition() + mPlayStartOffset);
                updateDisplay();
                handlePause();
            }
        }
    };
    private TextWatcher mTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) {
        }

        public void onTextChanged(CharSequence s,
                                  int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (mStartText.hasFocus()) {
                try {
                    mStartPos = mWaveformView.secondsToPixels(
                            Double.parseDouble(
                                    mStartText.getText().toString()));
                    updateDisplay();
                } catch (NumberFormatException e) {
                }
            }
            if (mEndText.hasFocus()) {
                try {
                    mEndPos = mWaveformView.secondsToPixels(
                            Double.parseDouble(
                                    mEndText.getText().toString()));
                    updateDisplay();
                } catch (NumberFormatException e) {
                }
            }
        }
    };
    private TextView mTextView_Header;
    private Dialog dialog_save;
    private LinearLayout mLinearLayout_Downloads;
    private LinearLayout mLinearLayout_Bank;
    private LinearLayout mLinearLayout_Cut;
    private LinearLayout mLinearLayout_Archive;
    private LinearLayout saveAs;
    private LinearLayout setAs;
    private ImageView ffwd;
    private ImageView rew;
    private Dialog dialog_Ringtone;
    private ImageView mImageViewBack;
    private ImageView mImageView_Save;
    private String filename;
    private String fileExtention;
    private OnClickListener mSaveListener = new OnClickListener() {
        public void onClick(View sender) {
            onSave();
        }
    };

    //
    // WaveformListener
    //

    public static void onAbout(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.about_title)
                .setMessage(R.string.about_text)
                .setPositiveButton(R.string.alert_ok_button, null)
                .setCancelable(false)
                .show();
    }

    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mRecordingFilename = null;
        mRecordingUri = null;
        mPlayer = null;
        mIsPlaying = false;

        Intent intent = getIntent();


        // If the Ringtone media select activity was launched via a
        // GET_CONTENT intent, then we shouldn't display a "saved"
        // message when the user saves, we should just return whatever
        // they create.
        mWasGetContentIntent = intent.getBooleanExtra(
                "was_get_content_intent", false);

        mFilename = intent.getData().toString();

        mSoundFile = null;
        mKeyDown = false;

        if (mFilename.equals("record")) {
            try {
                Intent recordIntent = new Intent(
                        MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivityForResult(recordIntent, REQUEST_CODE_RECORD);
            } catch (Exception e) {
                showFinalAlert(e, R.string.record_error);
            }
        }

        mHandler = new Handler();

        loadGui();

        mHandler.postDelayed(mTimerRunnable, 100);

        if (!mFilename.equals("record")) {
            loadFromFile();
        }
    }

    /**
     * Called with the activity is finally destroyed.
     */
    @Override
    protected void onDestroy() {
        Log.i("Ringtone", "EditActivity OnDestroy");

        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer = null;

        if (mRecordingFilename != null) {
            try {
                if (!new File(mRecordingFilename).delete()) {
                    showFinalAlert(new Exception(), R.string.delete_tmp_error);
                }

                getContentResolver().delete(mRecordingUri, null, null);
            } catch (SecurityException e) {
                showFinalAlert(e, R.string.delete_tmp_error);
            }
        }

        super.onDestroy();
    }

    /**
     * Called with an Activity we started with an Intent returns.
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent dataIntent) {
        if (requestCode == REQUEST_CODE_CHOOSE_CONTACT) {
            // The user finished saving their ringtone and they're
            // just applying it to a contact.  When they return here,
            // they're done.
            sendStatsToServerIfAllowedAndFinish();
            return;
        }

        if (requestCode != REQUEST_CODE_RECORD) {
            return;
        }

        if (resultCode != RESULT_OK) {
            finish();
            return;
        }

        if (dataIntent == null) {
            finish();
            return;
        }

        // Get the recorded file and open it, but save the uri and
        // filename so that we can delete them when we exit; the
        // recorded file is only temporary and only the edited & saved
        // ringtone / other sound will stick around.
        mRecordingUri = dataIntent.getData();
        mRecordingFilename = getFilenameFromUri(mRecordingUri);
        mFilename = mRecordingFilename;
        loadFromFile();
    }

    /**
     * Called when the orientation changes and/or the keyboard is shown
     * or hidden.  We don't need to recreate the whole activity in this
     * case, but we do need to redo our layout somewhat.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        final int saveZoomLevel = mWaveformView.getZoomLevel();
        super.onConfigurationChanged(newConfig);

        loadGui();
        enableZoomButtons();

        mHandler.postDelayed(new Runnable() {
            public void run() {
                mStartMarker.requestFocus();
                markerFocus(mStartMarker);

                mWaveformView.setZoomLevel(saveZoomLevel);
                mWaveformView.recomputeHeights(mDensity);

                updateDisplay();
            }
        }, 500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_options, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_save).setVisible(true);
        menu.findItem(R.id.action_reset).setVisible(true);
        menu.findItem(R.id.action_about).setVisible(true);
        return true;
    }

    //
    // MarkerListener
    //

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_save) {
            onSave();
            return true;
        } else if (i == R.id.action_reset) {
            resetPositions();
            mOffsetGoal = 0;
            updateDisplay();
            return true;
        } else if (i == R.id.action_about) {
            onAbout(this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            onPlay(mStartPos);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Every time we get a message that our waveform drew, see if we need to
     * animate and trigger another redraw.
     */
    public void waveformDraw() {
        mWidth = mWaveformView.getMeasuredWidth();
        if (mOffsetGoal != mOffset && !mKeyDown)
            updateDisplay();
        else if (mIsPlaying) {
            updateDisplay();
        } else if (mFlingVelocity != 0) {
            updateDisplay();
        }
    }

    public void waveformTouchStart(float x) {
        mTouchDragging = true;
        mTouchStart = x;
        mTouchInitialOffset = mOffset;
        mFlingVelocity = 0;
        mWaveformTouchStartMsec = System.currentTimeMillis();
    }

    public void waveformTouchMove(float x) {
        mOffset = trap((int) (mTouchInitialOffset + (mTouchStart - x)));
        updateDisplay();
    }

    public void waveformTouchEnd() {
        mTouchDragging = false;
        mOffsetGoal = mOffset;

        long elapsedMsec = System.currentTimeMillis() -
                mWaveformTouchStartMsec;
        if (elapsedMsec < 300) {
            if (mIsPlaying) {
                int seekMsec = mWaveformView.pixelsToMillisecs(
                        (int) (mTouchStart + mOffset));
                if (seekMsec >= mPlayStartMsec &&
                        seekMsec < mPlayEndMsec) {
                    mPlayer.seekTo(seekMsec - mPlayStartOffset);
                } else {
                    handlePause();
                }
            } else {
                onPlay((int) (mTouchStart + mOffset));
            }
        }
    }

    public void waveformFling(float vx) {
        mTouchDragging = false;
        mOffsetGoal = mOffset;
        mFlingVelocity = (int) (-vx);
        updateDisplay();
    }

    public void waveformZoomIn() {
        mWaveformView.zoomIn();
        mStartPos = mWaveformView.getStart();
        mEndPos = mWaveformView.getEnd();
        mMaxPos = mWaveformView.maxPos();
        mOffset = mWaveformView.getOffset();
        mOffsetGoal = mOffset;
        enableZoomButtons();
        updateDisplay();
    }

    public void waveformZoomOut() {
        mWaveformView.zoomOut();
        mStartPos = mWaveformView.getStart();
        mEndPos = mWaveformView.getEnd();
        mMaxPos = mWaveformView.maxPos();
        mOffset = mWaveformView.getOffset();
        mOffsetGoal = mOffset;
        enableZoomButtons();
        updateDisplay();
    }

    //
    // Static About dialog method, also called from RingtoneSelectActivity
    //

    public void markerDraw() {
    }

    //
    // Internal methods
    //

    public void markerTouchStart(MarkerView marker, float x) {
        mTouchDragging = true;
        mTouchStart = x;
        mTouchInitialStartPos = mStartPos;
        mTouchInitialEndPos = mEndPos;
    }

    public void markerTouchMove(MarkerView marker, float x) {
        float delta = x - mTouchStart;

        if (marker == mStartMarker) {
            mStartPos = trap((int) (mTouchInitialStartPos + delta));
            mEndPos = trap((int) (mTouchInitialEndPos + delta));
        } else {
            mEndPos = trap((int) (mTouchInitialEndPos + delta));
            if (mEndPos < mStartPos)
                mEndPos = mStartPos;
        }

        updateDisplay();
    }

    public void markerTouchEnd(MarkerView marker) {
        mTouchDragging = false;
        if (marker == mStartMarker) {
            setOffsetGoalStart();
        } else {
            setOffsetGoalEnd();
        }
    }

    public void markerLeft(MarkerView marker, int velocity) {
        mKeyDown = true;

        if (marker == mStartMarker) {
            int saveStart = mStartPos;
            mStartPos = trap(mStartPos - velocity);
            mEndPos = trap(mEndPos - (saveStart - mStartPos));
            setOffsetGoalStart();
        }

        if (marker == mEndMarker) {
            if (mEndPos == mStartPos) {
                mStartPos = trap(mStartPos - velocity);
                mEndPos = mStartPos;
            } else {
                mEndPos = trap(mEndPos - velocity);
            }

            setOffsetGoalEnd();
        }

        updateDisplay();
    }

    public void markerRight(MarkerView marker, int velocity) {
        mKeyDown = true;

        if (marker == mStartMarker) {
            int saveStart = mStartPos;
            mStartPos += velocity;
            if (mStartPos > mMaxPos)
                mStartPos = mMaxPos;
            mEndPos += (mStartPos - saveStart);
            if (mEndPos > mMaxPos)
                mEndPos = mMaxPos;

            setOffsetGoalStart();
        }

        if (marker == mEndMarker) {
            mEndPos += velocity;
            if (mEndPos > mMaxPos)
                mEndPos = mMaxPos;

            setOffsetGoalEnd();
        }

        updateDisplay();
    }

    public void markerEnter(MarkerView marker) {
    }

    public void markerKeyUp() {
        mKeyDown = false;
        updateDisplay();
    }

    public void markerFocus(MarkerView marker) {
        mKeyDown = false;
        if (marker == mStartMarker) {
            setOffsetGoalStartNoUpdate();
        } else {
            setOffsetGoalEndNoUpdate();
        }

        // Delay updaing the display because if this focus was in
        // response to a touch event, we want to receive the touch
        // event too before updating the display.
        mHandler.postDelayed(new Runnable() {
            public void run() {
                updateDisplay();
            }
        }, 100);
    }

    /**
     * Called from both onCreate and onConfigurationChanged
     * (if the user switched layouts)
     */
    private void loadGui() {
        // Inflate our UI from its XML layout description.
        setContentView(R.layout.editor);

        mLinearLayout_Downloads = (LinearLayout) findViewById(R.id.toolbar_icons_ll_downloads);
        mLinearLayout_Bank = (LinearLayout) findViewById(R.id.toolbar_icons_ll_bank);
        mLinearLayout_Cut = (LinearLayout) findViewById(R.id.toolbar_icons_ll_cut);
        mLinearLayout_Archive = (LinearLayout) findViewById(R.id.toolbar_icons_ll_archive);

        mLinearLayout_Downloads.setAlpha(0.6f);
        mLinearLayout_Bank.setAlpha(0.6f);
        mLinearLayout_Cut.setAlpha(1);
        mLinearLayout_Archive.setAlpha(0.6f);

        // changing tabs when clicking bottom nav items
        mLinearLayout_Downloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RingtoneSelectActivity.finisher();
                finish();
                MainActivity.ViewPagerTabSelector(0);
            }
        });
        mLinearLayout_Bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RingtoneSelectActivity.finisher();
                finish();
                MainActivity.ViewPagerTabSelector(1);
            }
        });
        mLinearLayout_Archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RingtoneSelectActivity.finisher();
                finish();
                MainActivity.ViewPagerTabSelector(2);
            }
        });

        ffwd = (ImageView) findViewById(R.id.ffwd);
        rew = (ImageView) findViewById(R.id.rew);
        saveAs = (LinearLayout) findViewById(R.id.saveAs);
        setAs = (LinearLayout) findViewById(R.id.setAs);
        setAs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_Ringtone.show();

            }
        });
        saveAs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        Typeface type_roboto_regular = Typeface.createFromAsset(getAssets(), "roboto_regular.ttf");
        Typeface type = Typeface.createFromAsset(getAssets(), "iran_sans_regular.ttf");

        // Toolbar Buttons
        mImageView_Save = (ImageView) findViewById(R.id.btnSave);
        mImageView_Save.setVisibility(View.VISIBLE);

        // premium Dialog
        dialog_Ringtone = new Dialog(RingtoneEditActivity.this);
        dialog_Ringtone.setContentView(R.layout.after_save_action);
        dialog_Ringtone.setTitle("Custom Dialog");
        TextView button_make_default = (TextView) dialog_Ringtone.findViewById(R.id.button_make_default);
        TextView button_choose_contact = (TextView) dialog_Ringtone.findViewById(R.id.button_choose_contact);
        TextView button_do_nothing = (TextView) dialog_Ringtone.findViewById(R.id.button_do_nothing);
        button_make_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RingtoneManager.setActualDefaultRingtoneUri(
                        getApplicationContext(),
                        RingtoneManager.TYPE_RINGTONE,
                        newUri);
                dialog_Ringtone.dismiss();

            }
        });
        button_choose_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseContactForRingtone(newUri);
                dialog_Ringtone.dismiss();

            }
        });
        button_do_nothing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_Ringtone.dismiss();
            }
        });


        mImageView_Save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveAs.getVisibility() == View.VISIBLE) {
                    Intent intent = new Intent(RingtoneEditActivity.this, RingtoneSelectActivity.class);
                    RingtoneEditActivity.this.startActivity(intent);
                    finish();
                } else {
                    onSave();
                }
//                dialog_save.show();
            }
        });

        // premium Dialog
        dialog_save = new Dialog(RingtoneEditActivity.this);
        dialog_save.setContentView(R.layout.dialog_save);
        dialog_save.setTitle("Custom Dialog");
        TextView dialog_txt = (TextView) dialog_save.findViewById(R.id.dialog_txt1);
        TextView save = (TextView) dialog_save.findViewById(R.id.save);

        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_save.dismiss();
            }
        });


        mTextView_Header = (TextView) findViewById(R.id.toolbar_main_tv_header);
//        mTextView_Header.setTypeface(typeFace_Medium);
        mTextView_Header.setText("برش موسیقی");
        mTextView_Header.setTypeface(type);

        mImageViewBack = (ImageView) findViewById(R.id.btnNavigation);
        mImageViewBack.setVisibility(View.GONE);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;

        mMarkerLeftInset = (int) (46 * mDensity);
        mMarkerRightInset = (int) (48 * mDensity);
        mMarkerTopOffset = (int) (10 * mDensity);
        mMarkerBottomOffset = (int) (10 * mDensity);

        mStartText = (TextView) findViewById(R.id.starttext);
        mStartText.addTextChangedListener(mTextWatcher);
        mEndText = (TextView) findViewById(R.id.endtext);
        mEndText.addTextChangedListener(mTextWatcher);

        mPlayButton = (ImageView) findViewById(R.id.play);
        mPlayButton.setOnClickListener(mPlayListener);
        mRewindButton = (ImageView) findViewById(R.id.rew);
        mRewindButton.setOnClickListener(mRewindListener);
        mFfwdButton = (ImageView) findViewById(R.id.ffwd);
        mFfwdButton.setOnClickListener(mFfwdListener);

        TextView markStartButton = (TextView) findViewById(R.id.mark_start);
        markStartButton.setOnClickListener(mMarkStartListener);
        TextView markEndButton = (TextView) findViewById(R.id.mark_end);
        markEndButton.setOnClickListener(mMarkStartListener);

        enableDisableButtons();

        mWaveformView = (WaveformView) findViewById(R.id.waveform);
        mWaveformView.setListener(this);

        mInfo = (TextView) findViewById(R.id.info);
        mInfo.setText(mCaption);

        mMaxPos = 0;
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;

        if (mSoundFile != null && !mWaveformView.hasSoundFile()) {
            mWaveformView.setSoundFile(mSoundFile);
            mWaveformView.recomputeHeights(mDensity);
            mMaxPos = mWaveformView.maxPos();
        }

        mStartMarker = (MarkerView) findViewById(R.id.startmarker);
        mStartMarker.setListener(this);
        mStartMarker.setAlpha(255);
        mStartMarker.setFocusable(true);
        mStartMarker.setFocusableInTouchMode(true);
        mStartVisible = true;

        mEndMarker = (MarkerView) findViewById(R.id.endmarker);
        mEndMarker.setListener(this);
        mEndMarker.setAlpha(255);
        mEndMarker.setFocusable(true);
        mEndMarker.setFocusableInTouchMode(true);
        mEndVisible = true;

        updateDisplay();

        // sequence example
        ShowcaseConfig config = new ShowcaseConfig();
        config.setMaskColor(Color.parseColor("#AD000000"));
        config.setDelay(500); // half second between each showcase view
        config.setDismissTextColor(Color.parseColor("#008888"));
        config.setFadeDuration(500);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "ss");

        sequence.setConfig(config);

        sequence.addSequenceItem(mStartMarker,
                "با این نشانگر میتونی زمان شروع رو تغییر بدی", "باشه");

        sequence.addSequenceItem(mEndMarker,
                "این نشانگر زمان پایان موسیقی رو مشخص میکنه", "باشه");


        sequence.start();

    }

    private void loadFromFile() {
        mFile = new File(mFilename);
        mExtension = getExtensionFromFilename(mFilename);

        SongMetadataReader metadataReader = new SongMetadataReader(
                this, mFilename);
        mTitle = metadataReader.mTitle;
        mArtist = metadataReader.mArtist;
        mAlbum = metadataReader.mAlbum;
        mYear = metadataReader.mYear;
        mGenre = metadataReader.mGenre;

        String titleLabel = mTitle;
        if (mArtist != null && mArtist.length() > 0) {
            titleLabel += " - " + mArtist;
        }
        setTitle(titleLabel);

        mLoadingStartTime = System.currentTimeMillis();
        mLoadingLastUpdateTime = System.currentTimeMillis();
        mLoadingKeepGoing = true;
        mProgressDialog = new ProgressDialog(RingtoneEditActivity.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle(R.string.progress_dialog_loading);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        mLoadingKeepGoing = false;
                    }
                });
        mProgressDialog.show();

        final CheapSoundFile.ProgressListener listener =
                new CheapSoundFile.ProgressListener() {
                    public boolean reportProgress(double fractionComplete) {
                        long now = System.currentTimeMillis();
                        if (now - mLoadingLastUpdateTime > 100) {
                            mProgressDialog.setProgress(
                                    (int) (mProgressDialog.getMax() *
                                            fractionComplete));
                            mLoadingLastUpdateTime = now;
                        }
                        return mLoadingKeepGoing;
                    }
                };

        // Create the MediaPlayer in a background thread
        mCanSeekAccurately = false;
        new Thread() {
            public void run() {
                mCanSeekAccurately = SeekTest.CanSeekAccurately(
                        getPreferences(Context.MODE_PRIVATE));

                System.out.println("Seek test done, creating media player.");
                try {
                    MediaPlayer player = new MediaPlayer();
                    player.setDataSource(mFile.getAbsolutePath());
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.prepare();
                    mPlayer = player;
                } catch (final java.io.IOException e) {
                    Runnable runnable = new Runnable() {
                        public void run() {

                        }
                    };
                    mHandler.post(runnable);
                }
                ;
            }
        }.start();

        // Load the sound file in a background thread
        new Thread() {
            public void run() {
                try {
                    mSoundFile = CheapSoundFile.create(mFile.getAbsolutePath(), listener);

                    if (mSoundFile == null) {
                        mProgressDialog.dismiss();
                        String name = mFile.getName().toLowerCase();
                        String[] components = name.split("\\.");
                        String err;
                        if (components.length < 2) {
                            err = getResources().getString(
                                    R.string.no_extension_error);
                        } else {
                            err = getResources().getString(
                                    R.string.bad_extension_error) + " " +
                                    components[components.length - 1];
                        }
                        final String finalErr = err;

                        return;
                    }
                } catch (final Exception e) {
                    mProgressDialog.dismiss();
                    e.printStackTrace();
                    mInfo.setText(e.toString());


                    return;
                }
                mProgressDialog.dismiss();
                if (mLoadingKeepGoing) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            finishOpeningSoundFile();
                        }
                    };
                    mHandler.post(runnable);
                } else {
                    RingtoneEditActivity.this.finish();
                }
            }
        }.start();
    }

    private void finishOpeningSoundFile() {
        mWaveformView.setSoundFile(mSoundFile);
        mWaveformView.recomputeHeights(mDensity);

        mMaxPos = mWaveformView.maxPos();
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;

        mTouchDragging = false;

        mOffset = 0;
        mOffsetGoal = 0;
        mFlingVelocity = 0;
        resetPositions();
        if (mEndPos > mMaxPos)
            mEndPos = mMaxPos;

        mCaption = mSoundFile.getFiletype() + " : فرمت فایل " + " \n" +
                mSoundFile.getSampleRate() + " Hz \n " +
                mSoundFile.getAvgBitrateKbps() + " kbps" + " : کیفیت" + " \n " +
                "زمان موسیقی : " + formatTime(mMaxPos) + " ثانیه";
        mInfo.setText(mCaption);

        updateDisplay();
    }

    private synchronized void updateDisplay() {
        if (mIsPlaying) {
            int now = mPlayer.getCurrentPosition() + mPlayStartOffset;
            int frames = mWaveformView.millisecsToPixels(now);
            mWaveformView.setPlayback(frames);
            setOffsetGoalNoUpdate(frames - mWidth / 2);
            if (now >= mPlayEndMsec) {
                handlePause();
            }
        }

        if (!mTouchDragging) {
            int offsetDelta;

            if (mFlingVelocity != 0) {
                float saveVel = mFlingVelocity;

                offsetDelta = mFlingVelocity / 30;
                if (mFlingVelocity > 80) {
                    mFlingVelocity -= 80;
                } else if (mFlingVelocity < -80) {
                    mFlingVelocity += 80;
                } else {
                    mFlingVelocity = 0;
                }

                mOffset += offsetDelta;

                if (mOffset + mWidth / 2 > mMaxPos) {
                    mOffset = mMaxPos - mWidth / 2;
                    mFlingVelocity = 0;
                }
                if (mOffset < 0) {
                    mOffset = 0;
                    mFlingVelocity = 0;
                }
                mOffsetGoal = mOffset;
            } else {
                offsetDelta = mOffsetGoal - mOffset;

                if (offsetDelta > 10)
                    offsetDelta = offsetDelta / 10;
                else if (offsetDelta > 0)
                    offsetDelta = 1;
                else if (offsetDelta < -10)
                    offsetDelta = offsetDelta / 10;
                else if (offsetDelta < 0)
                    offsetDelta = -1;
                else
                    offsetDelta = 0;

                mOffset += offsetDelta;
            }
        }

        mWaveformView.setParameters(mStartPos, mEndPos, mOffset);
        mWaveformView.invalidate();

        mStartMarker.setContentDescription(
                getResources().getText(R.string.start_marker) + " " +
                        formatTime(mStartPos));
        mEndMarker.setContentDescription(
                getResources().getText(R.string.end_marker) + " " +
                        formatTime(mEndPos));

        int startX = mStartPos - mOffset - mMarkerLeftInset;
        if (startX + mStartMarker.getWidth() >= 0) {
            if (!mStartVisible) {
                // Delay this to avoid flicker
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        mStartVisible = true;
                        mStartMarker.setAlpha(255);
                    }
                }, 0);
            }
        } else {
            if (mStartVisible) {
                mStartMarker.setAlpha(0);
                mStartVisible = false;
            }
            startX = 0;
        }

        int endX = mEndPos - mOffset - mEndMarker.getWidth() +
                mMarkerRightInset;
        if (endX + mEndMarker.getWidth() >= 0) {
            if (!mEndVisible) {
                // Delay this to avoid flicker
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        mEndVisible = true;
                        mEndMarker.setAlpha(255);
                    }
                }, 0);
            }
        } else {
            if (mEndVisible) {
                mEndMarker.setAlpha(0);
                mEndVisible = false;
            }
            endX = 0;
        }

        mStartMarker.setLayoutParams(
                new AbsoluteLayout.LayoutParams(
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        startX,
                        mMarkerTopOffset));

        mEndMarker.setLayoutParams(
                new AbsoluteLayout.LayoutParams(
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        endX,
                        mWaveformView.getMeasuredHeight() -
                                mEndMarker.getHeight() - mMarkerBottomOffset));
    }

    private void enableDisableButtons() {
        if (mIsPlaying) {
            mPlayButton.setImageResource(R.drawable.pause);
            mPlayButton.setContentDescription(getResources().getText(R.string.stop));
        } else {
            mPlayButton.setImageResource(R.drawable.play2);
            mPlayButton.setContentDescription(getResources().getText(R.string.play));
        }
    }

    private void resetPositions() {
        mStartPos = mWaveformView.secondsToPixels(0.0);
        mEndPos = mWaveformView.secondsToPixels(15.0);
    }

    private int trap(int pos) {
        if (pos < 0)
            return 0;
        if (pos > mMaxPos)
            return mMaxPos;
        return pos;
    }

    private void setOffsetGoalStart() {
        setOffsetGoal(mStartPos - mWidth / 2);
    }

    private void setOffsetGoalStartNoUpdate() {
        setOffsetGoalNoUpdate(mStartPos - mWidth / 2);
    }

    private void setOffsetGoalEnd() {
        setOffsetGoal(mEndPos - mWidth / 2);
    }

    private void setOffsetGoalEndNoUpdate() {
        setOffsetGoalNoUpdate(mEndPos - mWidth / 2);
    }

    private void setOffsetGoal(int offset) {
        setOffsetGoalNoUpdate(offset);
        updateDisplay();
    }

    private void setOffsetGoalNoUpdate(int offset) {
        if (mTouchDragging) {
            return;
        }

        mOffsetGoal = offset;
        if (mOffsetGoal + mWidth / 2 > mMaxPos)
            mOffsetGoal = mMaxPos - mWidth / 2;
        if (mOffsetGoal < 0)
            mOffsetGoal = 0;
    }

    private String formatTime(int pixels) {
        if (mWaveformView != null && mWaveformView.isInitialized()) {
            return formatDecimal(mWaveformView.pixelsToSeconds(pixels));
        } else {
            return "";
        }
    }

    private String formatDecimal(double x) {
        int xWhole = (int) x;
        int xFrac = (int) (100 * (x - xWhole) + 0.5);

        if (xFrac >= 100) {
            xWhole++; //Round up
            xFrac -= 100; //Now we need the remainder after the round up
            if (xFrac < 10) {
                xFrac *= 10; //we need a fraction that is 2 digits long
            }
        }

        if (xFrac < 10)
            return xWhole + ".0" + xFrac;
        else
            return xWhole + "." + xFrac;
    }

    private synchronized void handlePause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        mWaveformView.setPlayback(-1);
        mIsPlaying = false;
        enableDisableButtons();
    }

    private synchronized void onPlay(int startPosition) {
        if (mIsPlaying) {
            handlePause();
            return;
        }

        if (mPlayer == null) {
            // Not initialized yet
            return;
        }

        try {
            mPlayStartMsec = mWaveformView.pixelsToMillisecs(startPosition);
            if (startPosition < mStartPos) {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mStartPos);
            } else if (startPosition > mEndPos) {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mMaxPos);
            } else {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mEndPos);
            }

            mPlayStartOffset = 0;

            int startFrame = mWaveformView.secondsToFrames(
                    mPlayStartMsec * 0.001);
            int endFrame = mWaveformView.secondsToFrames(
                    mPlayEndMsec * 0.001);
            int startByte = mSoundFile.getSeekableFrameOffset(startFrame);
            int endByte = mSoundFile.getSeekableFrameOffset(endFrame);
            if (mCanSeekAccurately && startByte >= 0 && endByte >= 0) {
                try {
                    mPlayer.reset();
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    FileInputStream subsetInputStream = new FileInputStream(
                            mFile.getAbsolutePath());
                    mPlayer.setDataSource(subsetInputStream.getFD(),
                            startByte, endByte - startByte);
                    mPlayer.prepare();
                    mPlayStartOffset = mPlayStartMsec;
                } catch (Exception e) {
                    System.out.println("Exception trying to play file subset");
                    mPlayer.reset();
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mPlayer.setDataSource(mFile.getAbsolutePath());
                    mPlayer.prepare();
                    mPlayStartOffset = 0;
                }
            }

            mPlayer.setOnCompletionListener(new OnCompletionListener() {
                public synchronized void onCompletion(MediaPlayer arg0) {
                    handlePause();
                }
            });
            mIsPlaying = true;

            if (mPlayStartOffset == 0) {
                mPlayer.seekTo(mPlayStartMsec);
            }
            mPlayer.start();
            updateDisplay();
            enableDisableButtons();
        } catch (Exception e) {
            showFinalAlert(e, R.string.play_error);
            return;
        }
    }

    /**
     * Show a "final" alert dialog that will exit the activity
     * after the user clicks on the OK button.  If an exception
     * is passed, it's assumed to be an error condition, and the
     * dialog is presented as an error, and the stack trace is
     * logged.  If there's no exception, it's a success message.
     */
    private void showFinalAlert(Exception e, CharSequence message) {
        CharSequence title;
        if (e != null) {
            Log.e("Ringtone", "Error: " + message);
            Log.e("Ringtone", getStackTrace(e));
            title = getResources().getText(R.string.alert_title_failure);
            setResult(RESULT_CANCELED, new Intent());
        } else {
            Log.i("Ringtone", "Success: " + message);
            title = getResources().getText(R.string.alert_title_success);
        }

        new AlertDialog.Builder(RingtoneEditActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(
                        R.string.alert_ok_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                finish();
                            }
                        })
                .setCancelable(false)
                .show();
    }

    private void showFinalAlert(Exception e, int messageResourceId) {
        showFinalAlert(e, getResources().getText(messageResourceId));
    }

    private String makeRingtoneFilename(CharSequence title, String extension) {
        String parentdir;
        switch (mNewFileKind) {
            default:
            case FileSaveDialog.FILE_KIND_MUSIC:
                parentdir = "/sdcard/media/audio/ringtones";
                break;
            case FileSaveDialog.FILE_KIND_ALARM:
                parentdir = "/sdcard/media/audio/ringtones";
                break;
            case FileSaveDialog.FILE_KIND_NOTIFICATION:
                parentdir = "/sdcard/media/audio/ringtones";
                break;
            case FileSaveDialog.FILE_KIND_RINGTONE:
                parentdir = "/sdcard/media/audio/ringtones";
                break;
        }

        // Create the parent directory
        File parentDirFile = new File(parentdir);
        parentDirFile.mkdirs();

        // If we can't write to that special path, try just writing
        // directly to the sdcard
        if (!parentDirFile.isDirectory()) {
            parentdir = "/sdcard";
        }

        // Turn the title into a filename
        filename = "";
        for (int i = 0; i < title.length(); i++) {
            if (Character.isLetterOrDigit(title.charAt(i))) {
                filename += title.charAt(i);
            }
        }

        // Try to make the filename unique
        String path = null;
        for (int i = 0; i < 100; i++) {
            String testPath;
            if (i > 0) {
                testPath = parentdir + "/" + filename + i + extension;
                fileExtention = i + extension;
            } else {
                testPath = parentdir + "/" + filename + extension;
                fileExtention = extension;
            }
//            Toast.makeText(this, parentdir + "/" + filename + fileExtention, Toast.LENGTH_SHORT).show();

            try {
                RandomAccessFile f = new RandomAccessFile(
                        new File(testPath), "r");
            } catch (Exception e) {
                // Good, the file didn't exist
                path = testPath;
                break;
            }
        }

        return path;
    }

    private void saveRingtone(final CharSequence title) {
        final String outPath = makeRingtoneFilename(title, mExtension);

        if (outPath == null) {
            showFinalAlert(new Exception(), R.string.no_unique_filename);
            return;
        }

        mDstFilename = outPath;

        double startTime = mWaveformView.pixelsToSeconds(mStartPos);
        double endTime = mWaveformView.pixelsToSeconds(mEndPos);
        final int startFrame = mWaveformView.secondsToFrames(startTime);
        final int endFrame = mWaveformView.secondsToFrames(endTime);
        final int duration = (int) (endTime - startTime + 0.5);

        // Create an indeterminate progress dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle(R.string.progress_dialog_saving);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        // Save the sound file in a background thread
        new Thread() {
            public void run() {
                final File outFile = new File(outPath);
                try {
                    // Write the new file
                    mSoundFile.WriteFile(outFile,
                            startFrame,
                            endFrame - startFrame);

                    // Try to load the new file to make sure it worked
                    final CheapSoundFile.ProgressListener listener =
                            new CheapSoundFile.ProgressListener() {
                                public boolean reportProgress(double frac) {
                                    // Do nothing - we're not going to try to
                                    // estimate when reloading a saved sound
                                    // since it's usually fast, but hard to
                                    // estimate anyway.
                                    return true;  // Keep going
                                }
                            };
                    CheapSoundFile.create(outPath, listener);
                } catch (Exception e) {
                    mProgressDialog.dismiss();

                    CharSequence errorMessage;
                    if (e.getMessage().equals("No space left on device")) {
                        errorMessage = getResources().getText(
                                R.string.no_space_error);
                        e = null;
                    } else {
                        errorMessage = getResources().getText(
                                R.string.write_error);
                    }

                    final CharSequence finalErrorMessage = errorMessage;
                    final Exception finalException = e;

                    return;
                }

                mProgressDialog.dismiss();

                Runnable runnable = new Runnable() {
                    public void run() {
                        afterSavingRingtone(title,
                                outPath,
                                outFile,
                                duration);
                    }
                };
                mHandler.post(runnable);
            }
        }.start();
    }

    private void afterSavingRingtone(CharSequence title,
                                     String outPath,
                                     File outFile,
                                     int duration) {
        long length = outFile.length();
        if (length <= 512) {
            outFile.delete();
            new AlertDialog.Builder(this)
                    .setTitle(R.string.alert_title_failure)
                    .setMessage(R.string.too_small_error)
                    .setPositiveButton(R.string.alert_ok_button, null)
                    .setCancelable(false)
                    .show();
            return;
        }

        // Create the database record, pointing to the existing file path

        long fileSize = outFile.length();
        String mimeType = "audio/mpeg";

        String artist = "" + getResources().getText(R.string.artist_name);

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, outPath);
        values.put(MediaStore.MediaColumns.TITLE, title.toString());
        values.put(MediaStore.MediaColumns.SIZE, fileSize);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);

        values.put(MediaStore.Audio.Media.ARTIST, artist);
        values.put(MediaStore.Audio.Media.DURATION, duration);

        values.put(MediaStore.Audio.Media.IS_RINGTONE,
                mNewFileKind == FileSaveDialog.FILE_KIND_RINGTONE);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION,
                mNewFileKind == FileSaveDialog.FILE_KIND_NOTIFICATION);
        values.put(MediaStore.Audio.Media.IS_ALARM,
                mNewFileKind == FileSaveDialog.FILE_KIND_ALARM);
        values.put(MediaStore.Audio.Media.IS_MUSIC,
                mNewFileKind == FileSaveDialog.FILE_KIND_MUSIC);

        // Insert it into the database
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(outPath);
        newUri = getContentResolver().insert(uri, values);
        setResult(RESULT_OK, new Intent().setData(newUri));

        // Update a preference that counts how many times we've
        // successfully saved a ringtone or other audio
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        int successCount = prefs.getInt(PREF_SUCCESS_COUNT, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putInt(PREF_SUCCESS_COUNT, successCount + 1);
        prefsEditor.commit();

        // If Ringtone was launched to get content, just return
        if (mWasGetContentIntent) {
            sendStatsToServerIfAllowedAndFinish();
            return;
        }

        // There's nothing more to do with music or an alarm.  Show a
        // success message and then quit.
        if (mNewFileKind == FileSaveDialog.FILE_KIND_MUSIC ||
                mNewFileKind == FileSaveDialog.FILE_KIND_ALARM) {
            Toast.makeText(this,
                    R.string.save_success_message,
                    Toast.LENGTH_SHORT)
                    .show();
            sendStatsToServerIfAllowedAndFinish();
            return;
        }

        // If it's a notification, give the user the option of making
        // this their default notification.  If they say no, we're finished.
        if (mNewFileKind == FileSaveDialog.FILE_KIND_NOTIFICATION) {
            new AlertDialog.Builder(RingtoneEditActivity.this)
                    .setTitle(R.string.alert_title_success)
                    .setMessage(R.string.set_default_notification)
                    .setPositiveButton(R.string.alert_yes_button,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    RingtoneManager.setActualDefaultRingtoneUri(
                                            RingtoneEditActivity.this,
                                            RingtoneManager.TYPE_NOTIFICATION,
                                            newUri);
                                    sendStatsToServerIfAllowedAndFinish();
                                }
                            })
                    .setNegativeButton(
                            R.string.alert_no_button,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    sendStatsToServerIfAllowedAndFinish();
                                }
                            })
                    .setCancelable(false)
                    .show();
            return;
        }

        // If we get here, that means the type is a ringtone.  There are
        // three choices: make this your default ringtone, assign it to a
        // contact, or do nothing.

//        final Handler handler = new Handler() {
//            public void handleMessage(Message response) {
//                int actionId = response.arg1;
//                if (actionId == R.id.button_make_default) {
//                    RingtoneManager.setActualDefaultRingtoneUri(
//                            RingtoneEditActivity.this,
//                            RingtoneManager.TYPE_RINGTONE,
//                            newUri);
//                    Toast.makeText(
//                            RingtoneEditActivity.this,
//                            R.string.default_ringtone_success_message,
//                            Toast.LENGTH_SHORT)
//                            .show();
//                    sendStatsToServerIfAllowedAndFinish();
//
//                } else if (actionId == R.id.button_choose_contact) {
//                    chooseContactForRingtone(newUri);
//
//                } else {
//                    sendStatsToServerIfAllowedAndFinish();
//
//                }
//            }
//        };
//        Message message = Message.obtain(handler);

        // after_save_action
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                // notifying media scanner to find new file
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(new File("/sdcard/media/audio/ringtones/" + filename + fileExtention));
                    scanIntent.setData(contentUri);
                    getApplicationContext().sendBroadcast(scanIntent);
                } else {
                    final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                    getApplicationContext().sendBroadcast(intent);
                }

            }
        }, 200);
//        Toast.makeText(this,"/sdcard/media/audio/ringtones/" + filename + fileExtention, Toast.LENGTH_SHORT).show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentArchive.memoryScan();

            }
        }, 500);

        ffwd.setVisibility(View.GONE);
        rew.setVisibility(View.GONE);
        saveAs.setVisibility(View.VISIBLE);
        setAs.setVisibility(View.VISIBLE);
        dialog_save.show();

        mImageView_Save.setImageResource(R.drawable.close);
//        AfterSaveActionDialog dlog = new AfterSaveActionDialog(this, message);
//        dlog.show();


    }

    private void chooseContactForRingtone(Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_EDIT, uri);
//            intent.setClassName("com.isatel.app.ringo", "ChooseContactActivity");
            intent.setClass(getApplicationContext(), ChooseContactActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_CONTACT);
        } catch (Exception e) {
            Log.e("Ringtone", "Couldn't open Choose Contact window");
        }
    }

    private void onSave() {
        if (mIsPlaying) {
            handlePause();
        }

        final Handler handler = new Handler() {
            public void handleMessage(Message response) {
                CharSequence newTitle = (CharSequence) response.obj;
                mNewFileKind = response.arg1;
                saveRingtone(newTitle);
            }
        };
        Message message = Message.obtain(handler);
        FileSaveDialog dlog = new FileSaveDialog(
                this, getResources(), mTitle, message);
        dlog.show();
    }

    private void enableZoomButtons() {
    }

    private String getStackTrace(Exception e) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(stream, true);
        e.printStackTrace(writer);
        return stream.toString();
    }

    /**
     * Return extension including dot, like ".mp3"
     */
    private String getExtensionFromFilename(String filename) {
        return filename.substring(filename.lastIndexOf('.'),
                filename.length());
    }

    private String getFilenameFromUri(Uri uri) {
        Cursor c = managedQuery(uri, null, "", null, null);
        if (c.getCount() == 0) {
            return null;
        }
        c.moveToFirst();
        int dataIndex = c.getColumnIndexOrThrow(
                MediaStore.Audio.Media.DATA);

        return c.getString(dataIndex);
    }

    private void sendStatsToServerIfAllowedAndFinish() {
        Log.i("Ringtone", "sendStatsToServerIfAllowedAndFinish");

        final SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);

        // Check if we already have a pref for whether or not we can
        // contact the server.
        int serverAllowed = prefs.getInt(PREF_STATS_SERVER_ALLOWED,
                SERVER_ALLOWED_UNKNOWN);
        if (serverAllowed == SERVER_ALLOWED_NO) {
            Log.i("Ringtone", "SERVER_ALLOWED_NO");
            finish();
            return;
        }

        if (serverAllowed == SERVER_ALLOWED_YES) {
            Log.i("Ringtone", "SERVER_ALLOWED_YES");

            return;
        }

        // Number of times the user has successfully saved a sound.
        int successCount = prefs.getInt(PREF_SUCCESS_COUNT, 0);

        // The number of times the user must have successfully saved
        // a sound before we'll ask them.  Defaults to 2, and doubles
        // each time they click "Later".
        final int allowServerCheckIndex =
                prefs.getInt(PREF_STATS_SERVER_CHECK, 2);
        if (successCount < allowServerCheckIndex) {
            Log.i("Ringtone", "successCount " + successCount +
                    " is less than " + allowServerCheckIndex);
            finish();
            return;
        }


    }


    long getUniqueId() {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        long uniqueId = prefs.getLong(PREF_UNIQUE_ID, 0);
        if (uniqueId == 0) {
            uniqueId = new Random().nextLong();

            SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.putLong(PREF_UNIQUE_ID, uniqueId);
            prefsEditor.commit();
        }

        return uniqueId;
    }


}
