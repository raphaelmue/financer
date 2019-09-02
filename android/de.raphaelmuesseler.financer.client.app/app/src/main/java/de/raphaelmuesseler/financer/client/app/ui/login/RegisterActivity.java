package de.raphaelmuesseler.financer.client.app.ui.login;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.app.connection.RetrievalServiceImpl;
import de.raphaelmuesseler.financer.client.app.format.AndroidFormatter;
import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.app.ui.main.FinancerActivity;
import de.raphaelmuesseler.financer.client.connection.AsyncConnectionCall;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.local.Application;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.exceptions.EmailAlreadyInUseException;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.Hash;
import de.raphaelmuesseler.financer.util.RandomString;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;

public class RegisterActivity extends AppCompatActivity implements Application {

    private LinearLayout progressBar;
    private EditText emailEditText, nameEditText, surnameEditText, passwordEditText, repeatPasswordEditText;
    private TextView birthDateTextView;
    private Spinner genderSpinner;

    private final Formatter formatter = new AndroidFormatter(LocalStorageImpl.getInstance(), this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ServerRequestHandler.setApplication(this);


        progressBar = findViewById(R.id.ll_loading);

        this.emailEditText = findViewById(R.id.et_register_email);
        this.nameEditText = findViewById(R.id.et_register_name);
        this.surnameEditText = findViewById(R.id.et_register_surname);
        this.birthDateTextView = findViewById(R.id.tv_register_birth_date);
        this.genderSpinner = findViewById(R.id.sp_gender);
        this.passwordEditText = findViewById(R.id.et_register_password);
        this.repeatPasswordEditText = findViewById(R.id.et_register_repeat_password);

        this.birthDateTextView.setOnClickListener(v -> new DatePickerDialog(
                RegisterActivity.this,
                (view, year, monthOfYear, dayOfMonth) ->
                        birthDateTextView.setText(formatter.formatDate(LocalDate.of(year, monthOfYear + 1, dayOfMonth))),
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue() - 1,
                LocalDate.now().getDayOfMonth()).show());

        this.genderSpinner.setAdapter(new GenderSpinnerAdapter(this));

        Button registerButton = findViewById(R.id.btn_register);
        registerButton.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        boolean cancel = false;

        if (TextUtils.isEmpty(this.emailEditText.getText())) {
            this.emailEditText.setError(getString(R.string.error_field_required));
            cancel = true;
        } else if (!this.isEmailValid(this.emailEditText.getText().toString())) {
            this.emailEditText.setError(getString(R.string.error_invalid_email));
            cancel = true;
        }

        if (TextUtils.isEmpty(this.nameEditText.getText())) {
            this.nameEditText.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if (TextUtils.isEmpty(this.surnameEditText.getText())) {
            this.surnameEditText.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if (TextUtils.isEmpty(this.passwordEditText.getText())) {
            this.passwordEditText.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if (TextUtils.isEmpty(this.repeatPasswordEditText.getText())) {
            this.repeatPasswordEditText.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if (!this.passwordEditText.getText().toString().equals(this.repeatPasswordEditText.getText().toString())) {
            this.repeatPasswordEditText.setError(getString(R.string.passwords_do_not_match));
            cancel = true;
        }
        if (this.passwordEditText.getText().toString().length() < 4) {
            this.passwordEditText.setError(getString(R.string.password_too_short));
            cancel = true;
        }

        if (!cancel) {
            String salt = new RandomString(32).nextString();
            String password = Hash.create(this.passwordEditText.getText().toString(), salt);

            User user = new User(0,
                    this.emailEditText.getText().toString(),
                    password,
                    salt,
                    this.nameEditText.getText().toString(),
                    this.surnameEditText.getText().toString(),
                    LocalDate.parse(birthDateTextView.getText().toString(), DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                            .withLocale(getResources().getConfiguration().locale)),
                    (User.Gender) this.genderSpinner.getSelectedItem());

            Map<String, Serializable> parameters = new HashMap<>();
            parameters.put("user", user);

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler("registerUser", parameters, new AsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult connectionResult) {
                    LocalStorageImpl.getInstance().writeObject("user", connectionResult.getResult());
                    RetrievalServiceImpl.getInstance().fetchAllData((User) connectionResult.getResult(), aVoid -> openFinancerActivity());
                }

                @Override
                public void onFailure(final Exception exception) {
                    exception.printStackTrace();
                    runOnUiThread(() -> {
                        if (exception instanceof EmailAlreadyInUseException || exception.getCause() != null && exception.getCause() instanceof EmailAlreadyInUseException) {
                            emailEditText.setError(getString(R.string.email_already_in_use));
                        }
                    });
                }
            }));
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private void openFinancerActivity() {
        Intent intent = new Intent(this, FinancerActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showLoadingBox() {
        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
    }

    @Override
    public void hideLoadingBox() {
        runOnUiThread(() -> progressBar.setVisibility(View.GONE));
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

    @Override
    public void showErrorDialog(Exception e) {
        runOnUiThread(() -> new AlertDialog.Builder(this)
                .setTitle("Financer")
                .setMessage(new AndroidFormatter(LocalStorageImpl.getInstance(), this).formatExceptionMessage(e))
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.ic_error)
                .show());
    }

    private class GenderSpinnerAdapter extends ArrayAdapter<User.Gender> {

        GenderSpinnerAdapter(Context context) {
            super(context, R.layout.support_simple_spinner_dropdown_item, User.Gender.values());
            this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            User.Gender gender = getItem(position);

            final View view;
            final TextView text;

            if (convertView == null) {
                view = LayoutInflater.from(this.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
            } else {
                view = convertView;
            }

            text = (TextView) view;
            text.setTextColor(getResources().getColor(R.color.white));

            if (gender != null) {
                switch (gender.getName()) {
                    case "male":
                        text.setText(getString(R.string.male));
                        break;
                    case "female":
                        text.setText(getString(R.string.female));
                        break;
                    case "notSpecified":
                        text.setText(getString(R.string.not_specified));
                        break;
                }
            }

            return view;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return this.getView(position, convertView, parent);
        }
    }
}
