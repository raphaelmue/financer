package de.raphaelmuesseler.financer.client.app.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.app.ui.main.FinancerActivity;
import de.raphaelmuesseler.financer.client.connection.AsyncConnectionCall;
import de.raphaelmuesseler.financer.client.connection.ServerRequest;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.local.Application;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;

public class LoginActivity extends AppCompatActivity implements Application {

    private ServerRequestHandler mAuthTask = null;

    private LinearLayout progresBar;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ServerRequest.setHost(false);
        ServerRequestHandler.setApplication(this);

        setContentView(R.layout.activity_login);

        progresBar = findViewById(R.id.ll_loading);

        emailEditText = findViewById(R.id.et_email);

        passwordEditText = findViewById(R.id.et_password);
        passwordEditText.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button mEmailSignInButton = findViewById(R.id.btn_login);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        emailEditText.setError(null);
        passwordEditText.setError(null);

        // Store values at the time of the login attempt.
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean cancel = false;

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getString(R.string.error_field_required));
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailEditText.setError(getString(R.string.error_invalid_email));
            cancel = true;
        }

        if (!cancel) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("email", email);
            parameters.put("password", password);

            mAuthTask = new ServerRequestHandler("checkCredentials", parameters, new AsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult connectionResult) {
                    if (connectionResult.getResult() != null) {
                        LocalStorageImpl.getInstance().writeObject("user", connectionResult.getResult());
                        openFinancerActivity();
                        showToast(MessageType.SUCCESS, "asdf");
                    } else {
                        runOnUiThread(() -> {
                            passwordEditText.setError(getString(R.string.error_invalid_credentials));
                            passwordEditText.requestFocus();
                        });
                    }
                }

                @Override
                public void onFailure(Exception exception) {
                    exception.printStackTrace();
                }

                @Override
                public void onAfter() {
                    mAuthTask = null;
                }
            });
            new Thread(mAuthTask).start();
        }
    }

    private void openFinancerActivity() {
        Intent intent = new Intent(this, FinancerActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    @Override
    public void showLoadingBox() {
        runOnUiThread(() -> progresBar.setVisibility(View.VISIBLE));
    }

    @Override
    public void hideLoadingBox() {
        runOnUiThread(() -> progresBar.setVisibility(View.GONE));
    }

    @Override
    public void setOffline() {

    }

    @Override
    public void setOnline() {

    }

    @Override
    public void showToast(MessageType messageType, String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}

