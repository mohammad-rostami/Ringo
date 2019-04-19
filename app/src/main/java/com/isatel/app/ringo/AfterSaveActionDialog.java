package com.isatel.app.ringo;

import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.view.View;
import android.widget.TextView;


public class AfterSaveActionDialog extends Dialog {

    private Message mResponse;

    public AfterSaveActionDialog(Context context, Message response) {
        super(context);

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.after_save_action);

        setTitle(R.string.alert_title_success);

        ((TextView)findViewById(R.id.button_make_default))
            .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        closeAndSendResult(R.id.button_make_default);
                    }
                });
        ((TextView)findViewById(R.id.button_choose_contact))
            .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        closeAndSendResult(R.id.button_choose_contact);
                    }
                });
        ((TextView)findViewById(R.id.button_do_nothing))
            .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        closeAndSendResult(R.id.button_do_nothing);
                    }
                });

        mResponse = response;
    }

    private void closeAndSendResult(int clickedButtonId) {
        mResponse.arg1 = clickedButtonId;
        mResponse.sendToTarget();
        dismiss();
    }
}
