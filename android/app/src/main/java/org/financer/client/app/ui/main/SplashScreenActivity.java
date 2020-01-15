package org.financer.client.app.ui.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Objects;

import org.financer.client.app.R;
import org.financer.client.app.connection.RetrievalServiceImpl;
import org.financer.client.app.local.LocalStorageImpl;
import org.financer.shared.model.user.User;

public class SplashScreenActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView loadingStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Objects.requireNonNull(getSupportActionBar()).hide();

        this.progressBar = findViewById(R.id.pb_splash_screen);
        this.progressBar.setMax(3);

        this.loadingStatusTextView = findViewById(R.id.tv_loading_status);

        loadData();
    }

    private void loadData() {
        final User user = (User) LocalStorageImpl.getInstance().readObject("user");
        if (user != null) {
            RetrievalServiceImpl.getInstance().fetchCategories(user, (category) -> {
                this.progressBar.setProgress(1);
                this.loadingStatusTextView.setText(getString(R.string.load_transactions));
                RetrievalServiceImpl.getInstance().fetchTransactions(user, category1 -> {
                    this.progressBar.setProgress(2);
                    this.loadingStatusTextView.setText(getString(R.string.load_fixed_transactions));
                    RetrievalServiceImpl.getInstance().fetchFixedTransactions(user, category2 -> {
                        this.progressBar.setProgress(3);
                        runOnUiThread(this::finish);
                    });
                });
            });
        }
    }
}
