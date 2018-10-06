package de.raphaelmuesseler.financer.client.app.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.app.ui.login.LoginActivity;
import de.raphaelmuesseler.financer.client.app.ui.main.transactions.TransactionFragment;
import de.raphaelmuesseler.financer.shared.model.User;

public class FinancerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalStorageImpl.setContext(this);

        this.user = LocalStorageImpl.getInstance().getLoggedInUser();
        if (this.user == null) {
            this.openLoginActivity();
        } else {
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
            tvUserEmail.setText(this.user.getEmail());
            TextView tvUserFullName = navigationView.getHeaderView(0).findViewById(R.id.tvUserFullName);
            tvUserFullName.setText(this.user.getFullName());
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            LocalStorageImpl.getInstance().logUserOut();
            openLoginActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        Class fragmentClass = null;
        AppBarLayout appBarLayout= findViewById(R.id.toolbarContainer);

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
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_content, fragment).commit();

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
