package com.isatel.app.ringo;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;



public class ChooseContactActivity
    extends ListActivity
    implements TextWatcher
{
    private TextView mFilter;
    private SimpleCursorAdapter mAdapter;
    private Uri mRingtoneUri;
    private TextView mTextView_Header;

    public ChooseContactActivity() {
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setTitle(R.string.choose_contact_title);

        Intent intent = getIntent();
        mRingtoneUri = intent.getData();

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.choose_contact);

        try {
            mAdapter = new SimpleCursorAdapter(
                this,
                // Use a template that displays a text view
                R.layout.contact_row,
                // Give the cursor to the list adatper
                createCursor(""),
                // Map from database columns...
                new String[] {
                    People.CUSTOM_RINGTONE,
                    People.STARRED,
                    People.DISPLAY_NAME },
                // To widget ids in the row layout...
                new int[] {
                    R.id.row_ringtone,
                    R.id.row_starred,
                    R.id.row_display_name });

            mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    public boolean setViewValue(View view,
                                                Cursor cursor,
                                                int columnIndex) {
                        String name = cursor.getColumnName(columnIndex);
                        String value = cursor.getString(columnIndex);
                        if (name.equals(People.CUSTOM_RINGTONE)) {
                            if (value != null && value.length() > 0) {
                                view.setVisibility(View.VISIBLE);
                            } else  {
                                view.setVisibility(View.INVISIBLE);
                            }
                            return true;
                        }
                        if (name.equals(People.STARRED)) {
                            if (value != null && value.equals("1")) {
                                view.setVisibility(View.VISIBLE);
                            } else  {
                                view.setVisibility(View.INVISIBLE);
                            }
                            return true;
                        }

                        return false;
                    }
                });

            setListAdapter(mAdapter);

            // On click, assign ringtone to contact
            getListView().setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView parent,
                                            View view,
                                            int position,
                                            long id) {
                        assignRingtoneToContact();
                    }
                });

        } catch (SecurityException e) {
            // No permission to retrieve contacts?
            Log.e("Ringdroid", e.toString());
        }

        Typeface type = Typeface.createFromAsset(getAssets(), "iran_sans_regular.ttf");
        // Toolbar Buttons
        mTextView_Header = (TextView) findViewById(R.id.toolbar_main_tv_header);
//        mTextView_Header.setTypeface(typeFace_Medium);
        mTextView_Header.setText("انتخاب مخاطب");
        mTextView_Header.setTypeface(type);
        ImageView btnNavigation = (ImageView) findViewById(R.id.btnNavigation);
        btnNavigation.setVisibility(View.GONE);

        mFilter = (TextView) findViewById(R.id.search_filter);
        if (mFilter != null) {
            mFilter.addTextChangedListener(this);
        }
    }

    private boolean isEclairOrLater() {
	return Build.VERSION.SDK_INT >= 5;
    }

    private Uri getContactContentUri() {
	if (isEclairOrLater()) {
	    // ContactsContract.Contacts.CONTENT_URI
	    return Uri.parse("content://com.android.contacts/contacts");
	} else {
	    return Contacts.People.CONTENT_URI;
	}
    }

    private void assignRingtoneToContact() {
        Cursor c = mAdapter.getCursor();
        int dataIndex = c.getColumnIndexOrThrow(People._ID);
        String contactId = c.getString(dataIndex);

        dataIndex = c.getColumnIndexOrThrow(People.DISPLAY_NAME);
        String displayName = c.getString(dataIndex);

        Uri uri = Uri.withAppendedPath(getContactContentUri(), contactId);

        ContentValues values = new ContentValues();
        values.put(People.CUSTOM_RINGTONE, mRingtoneUri.toString());
        getContentResolver().update(uri, values, null, null);

        String message =
            getResources().getText(R.string.success_contact_ringtone) +
            " " +
            displayName;

        Toast.makeText(this, message, Toast.LENGTH_SHORT)
            .show();
        finish();
        return;
    }

    private Cursor createCursor(String filter) {
        String selection;
        if (filter != null && filter.length() > 0) {
            selection = "(DISPLAY_NAME LIKE \"%" + filter + "%\")";
        } else {
            selection = null;
        }
        Cursor cursor = managedQuery(
            getContactContentUri(),
            new String[] {
                People._ID,
                People.CUSTOM_RINGTONE,
                People.DISPLAY_NAME,
                People.LAST_TIME_CONTACTED,
                People.STARRED,
                People.TIMES_CONTACTED },
            selection,
            null,
            "STARRED DESC, " +
	    "TIMES_CONTACTED DESC, " +
	    "LAST_TIME_CONTACTED DESC, " +
	    "DISPLAY_NAME ASC");

        Log.i("Ringdroid", cursor.getCount() + " contacts");

        return cursor;
    }

    public void beforeTextChanged(CharSequence s, int start,
                                  int count, int after) {
    }

    public void onTextChanged(CharSequence s,
                              int start, int before, int count) {
    }

    public void afterTextChanged(Editable s) {
        String filterStr = mFilter.getText().toString();
        mAdapter.changeCursor(createCursor(filterStr));
    }
}
