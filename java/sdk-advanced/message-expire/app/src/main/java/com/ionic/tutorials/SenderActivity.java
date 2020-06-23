package com.ionic.tutorials;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ionic.machina.sample.BuildConfig;
import com.ionic.machina.sample.R;
import com.ionic.tutorials.ui.login.LoggedInUserView;
import com.ionic.tutorials.ui.login.LoginFormState;
import com.ionic.tutorials.ui.login.LoginResult;
import com.ionic.tutorials.ui.login.LoginViewModel;
import com.ionic.tutorials.ui.login.LoginViewModelFactory;
import com.ionic.tutorials.utils.Constants;
import com.ionic.tutorials.utils.FileUtils;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.cipher.chunk.ChunkCipherAuto;
import com.ionic.sdk.agent.cipher.chunk.data.ChunkCryptoEncryptAttributes;
import com.ionic.sdk.agent.key.KeyAttributesMap;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPassword;
import com.ionic.sdk.error.IonicException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Locale;

public class SenderActivity extends AppCompatActivity {

    private static final String TAG = SenderActivity.class.getSimpleName();

    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.email);
        final EditText passwordEditText = findViewById(R.id.text);
        final Button sendButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                sendButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        // Send button Action
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);

                protectMessage(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());

                loadingProgressBar.setVisibility(View.GONE);
            }
        });

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},0x1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        File profiles = new File(getFilesDir(), "profiles.pw");
        if( ! profiles.exists()) {
            if (FileUtils.copyResToPath(this, R.raw.profiles,
                    profiles.getAbsolutePath()) == null) {
                Log.e(TAG, String.format("Unable to copy res to '%s'",
                        profiles.getAbsolutePath()));
            }
        }
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String sent = getString(R.string.message_sent);
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), sent, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void sendMessage(String email, String message) {

        final Intent emailIntent = new Intent( android.content.Intent.ACTION_SEND);

        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[] { email});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Sample Message");

        File attachment = writeToFile(message);
        Uri uri = FileProvider.getUriForFile(this, AUTHORITY, attachment);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        startActivity(Intent.createChooser(
                emailIntent, "Send Mail"));
    }

    private File writeToFile(String message) {

        String fileName = "message.mpf";
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(message);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            return null;
        }

        return new File(getFilesDir(), fileName);
    }

    private void protectMessage(final String email, final String message) {

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

                    agent.setMetadata("ionic-application-name", "ionic-time-based-access-tutorial");
                    agent.setMetadata("ionic-application-version", "1.0.0");

                    // initialize chunk cipher object
                    ChunkCipherAuto senderCipher = new ChunkCipherAuto(agent);

                    // Create key attribute to expire access in in 2 minutes
                    KeyAttributesMap mutableAttributes = new KeyAttributesMap();
                    mutableAttributes.put("ionic-expiration", Arrays.asList(getExpireDate(2)));
                    ChunkCryptoEncryptAttributes attr = new ChunkCryptoEncryptAttributes(mutableAttributes);

                    // encrypt using the key attributes
                    final String encryptedMessage = senderCipher.encrypt(message.getBytes(), attr);

                    // Call the 'sendMessage' function on the main thread
                    SenderActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendMessage(email, encryptedMessage);
                        }
                    });

                } catch(IonicException e) {

                    e.printStackTrace();
                }
            }
        });
    }

    private String getExpireDate(int minFromNow) {

        java.util.Date expireDate = new java.util.Date(System.currentTimeMillis() + minFromNow * 60 * 1000);
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
        String formattedDate = sdf.format(expireDate);
        return formattedDate;
    }
}
