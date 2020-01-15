package org.financer.client.app.ui.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.financer.client.app.R;
import org.financer.client.app.format.AndroidFormatter;
import org.financer.client.app.local.LocalStorageImpl;
import org.financer.client.app.ui.login.LoginActivity;
import org.financer.client.app.ui.main.transactions.TransactionFragment;
import org.financer.client.connection.ServerRequest;
import org.financer.client.connection.ServerRequestHandler;
import org.financer.client.local.Application;
import org.financer.shared.model.user.User;

public class FinancerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener, Application {

    public static final int REQUEST_WRITE_STORAGE_PERMISSION = 112;

    public static Application INSTANCE;

    private ProgressBar progressBar;

    public static Application getFinancerApplication() {
        return INSTANCE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        INSTANCE = this;

        LocalStorageImpl.setContext(this);
        ServerRequest.setHost(false);
        ServerRequestHandler.setApplication(this);
        ServerRequestHandler.setLocalStorage(LocalStorageImpl.getInstance());

        User user = (User) LocalStorageImpl.getInstance().readObject("user");
        if (user == null) {
            this.openLoginActivity();
        } else {
            Intent i = new Intent(this, SplashScreenActivity.class);
            startActivity(i);

            setContentView(R.layout.activity_financer);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            // initialize navigation view and setting first item checked
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.getMenu().getItem(0).setChecked(true);

            // setting OverviewFragment as default fragment when starting the application
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_content, OverviewFragment.newInstance()).commit();


            // setting user's full name and email to navigation header

            TextView tvUserEmail = navigationView.getHeaderView(0).findViewById(R.id.tvUserEmail);
            tvUserEmail.setText(user.getEmail());
            TextView tvUserFullName = navigationView.getHeaderView(0).findViewById(R.id.tvUserFullName);
            tvUserFullName.setText(user.getFullName());

            this.progressBar = findViewById(R.id.toolbar_progress_bar);
        }
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.financer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            LocalStorageImpl.getInstance().deleteAllData();
            openLoginActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment;
        Class fragmentClass = null;
        AppBarLayout appBarLayout = findViewById(R.id.toolbarContainer);

        switch (item.getItemId()) {
            case R.id.nav_overview:
                fragmentClass = OverviewFragment.class;
                setTitle(R.string.overview);
                appBarLayout.setElevation(8);
                break;
            case R.id.nav_transactions:
                fragmentClass = TransactionFragment.class;
                setTitle(R.string.transactions);
                appBarLayout.setElevation(0);
                break;
            case R.id.nav_statistics:
                fragmentClass = StatisticsFragment.class;
                setTitle(R.string.statistics);
                appBarLayout.setElevation(8);
                break;
            case R.id.nav_profile:
                fragmentClass = ProfileFragment.class;
                setTitle(R.string.profile);
                appBarLayout.setElevation(8);
                break;
            case R.id.nav_settings:
                fragmentClass = SettingsFragment.class;
                setTitle(R.string.settings);
                appBarLayout.setElevation(8);
                break;
        }

        if (fragmentClass != null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_content, fragment).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void showLoadingBox() {
        runOnUiThread(() -> this.progressBar.setVisibility(View.VISIBLE));
    }

    @Override
    public void hideLoadingBox() {
        runOnUiThread(() -> this.progressBar.setVisibility(View.INVISIBLE));
    }

    @Override
    public void setOffline() {

    }

    @Override
    public void setOnline() {

    }

    @Override
    public void showToast(MessageType messageType, String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_WRITE_STORAGE_PERMISSION:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    this.showToast(MessageType.ERROR, getString(R.string.error_storage_permission_not_granted));
                }
                break;
        }
    }
}
