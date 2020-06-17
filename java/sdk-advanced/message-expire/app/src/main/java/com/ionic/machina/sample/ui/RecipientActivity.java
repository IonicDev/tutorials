package com.ionic.machina.sample.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.ionic.machina.sample.R;
import com.ionic.machina.sample.utils.Constants;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.cipher.chunk.ChunkCipherAuto;
import com.ionic.sdk.agent.cipher.chunk.data.ChunkCryptoChunkInfo;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPassword;
import com.ionic.sdk.error.IonicException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class RecipientActivity extends Activity {

    ProgressBar mProgressView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_access);

        if(mProgressView != null) {
            mProgressView = findViewById(R.id.progress);
            mProgressView.animate();
        }

        String message = getStringFromUri(getIntent().getData());
        processMessage(message);
    }

    private String getStringFromUri(Uri uri) {

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Reader reader = new BufferedReader(
                    new InputStreamReader(inputStream,
                            Charset.forName(StandardCharsets.UTF_8.name())));

            StringBuilder textBuilder = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1)
                textBuilder.append((char) c);

            return textBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void displayMessage(final String message) {

        int icon = R.drawable.message_icon;
        String title = "Plain Text Message:";
        displayMessage(icon, title, message);
    }

    private void displayMessage(final int icon, final String title, final String message) {

        // Update the UI on the main thread
        RecipientActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(mProgressView != null)
                    mProgressView.setVisibility(View.INVISIBLE);

                new AlertDialog.Builder(RecipientActivity.this)
                        .setTitle(title)
                        .setMessage(message)

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        //.setNegativeButton(android.R.string.no, null)
                        .setIcon(icon)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finish();
                            }
                        })
                        .show();
            }
        });
    }

    private void processMessage(final String receivedMessage) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    // initialize agent
                    Agent agent = new Agent();
                    File persistorFile = new File(getFilesDir(), "profiles.pw");
                    DeviceProfilePersistorPassword persistor =
                            new DeviceProfilePersistorPassword(persistorFile.getAbsolutePath());
                    persistor.setPassword(Constants.IONIC_PERSISTOR_PASSWORD);
                    agent.initialize(persistor);


                    final int icon;
                    final String title;
                    final String message;
                    // initialize chunk cipher object
                    ChunkCipherAuto receiveCipher = new ChunkCipherAuto(agent);
                    ChunkCryptoChunkInfo info =  receiveCipher.getChunkInfo(receivedMessage);
                    if(info.isEncrypted()) {
                        // decrypt
                        icon = R.drawable.ionic_icon;
                        title = getString(R.string.message_title_protected);
                        message = receiveCipher.decrypt(receivedMessage);
                    } else {
                        icon = R.drawable.message_icon;
                        title = getString(R.string.message_title_plain);
                        message = receivedMessage;
                    }

                    // Update the UI on the main thread
                    displayMessage(icon, title, message);

                } catch(final IonicException e) {

                    e.printStackTrace();
                    String title = getString(R.string.message_title_error);
                    displayMessage(R.drawable.ionic_icon, title, e.getLocalizedMessage());
                }
            }
        });
    }
}
