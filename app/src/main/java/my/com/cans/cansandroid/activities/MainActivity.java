package my.com.cans.cansandroid.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.fragments.BaseFragment;
import my.com.cans.cansandroid.fragments.FormsFragment;
import my.com.cans.cansandroid.fragments.ReportsFragment;
import my.com.cans.cansandroid.managers.CustomLocationManager;
import my.com.cans.cansandroid.managers.ValidateManager;
import my.com.cans.cansandroid.objects.CANSInfo;
import my.com.cans.cansandroid.objects.dbo.T_User;
import my.com.cans.cansandroid.services.BaseAPICallback;
import my.com.cans.cansandroid.services.MobileAPI;
import my.com.cans.cansandroid.services.MobileAPIResponse;
import my.com.cans.cansandroid.services.MyHTTP;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab_report = (FloatingActionButton) findViewById(R.id.fab_report);
        fab_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoEditReport(null);
            }
        });

        FloatingActionButton fab_form = (FloatingActionButton) findViewById(R.id.fab_form);
        fab_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoEditForm(null);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        ImageView imageView = (ImageView) header.findViewById(R.id.imageView);
        imageView.setImageResource(R.mipmap.ic_launcher);

        T_User user = new CANSInfo(this).getUser();
        TextView userName = (TextView) header.findViewById(R.id.user_name);
        userName.setText(user.loginID);

        new CustomLocationManager(this).checkGPS();
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyUser();
    }

    @Override
    protected void refresh(SwipeRefreshLayout swipeRefreshLayout) {
        if (mCurrentFragment == null)
            super.refresh(swipeRefreshLayout);
        else
            mCurrentFragment.refresh(swipeRefreshLayout);
    }

    private boolean mIsLogined = false;

    protected void verifyUser() {
        new MyHTTP(this).call(MobileAPI.class).verify().enqueue(new BaseAPICallback<MobileAPIResponse.VerifyResponse>(this) {
            @Override
            public void onResponse(Call<MobileAPIResponse.VerifyResponse> call, Response<MobileAPIResponse.VerifyResponse> response) {
                final MobileAPIResponse.VerifyResponse resp = response.body();

                if (!getString(R.string.version_no).equals(resp.Result.Version)) {
                    String message = getString(R.string.version_not_matched, resp.Result.Version);
                    if (ValidateManager.hasValue(resp.Result.Message))
                        message = message + "\n\n" + resp.Result.Message;

                    confirm(message, new OnConfirmListener() {
                        @Override
                        public void onConfirm(DialogInterface dialog, int which) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(getString(R.string.new_apk_url)));
                            startActivity(browserIntent);
                            System.exit(0);
                        }
                    }, new OnConfirmListener() {
                        @Override
                        public void onConfirm(DialogInterface dialog, int which) {
                            if (resp.Succeed) {
                                if (!mIsLogined)
                                    gotoForms();
                                mIsLogined = true;
                            } else {
                                mIsLogined = false;
                                MainActivity.this.gotoLogin();
                            }
                        }
                    });
                } else {
                    if (resp.Succeed) {
                        if (!mIsLogined)
                            gotoForms();
                        mIsLogined = true;
                    } else {
                        mIsLogined = false;
                        MainActivity.this.gotoLogin();
                    }
                }
            }
        });
    }

    protected void gotoLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    protected void gotoEditForm(String key) {
        Intent intent = new Intent(this, EditFormActivity.class);
        if (key != null && !key.isEmpty())
            intent.putExtra("key", key);
        startActivity(intent);
    }

    protected void gotoEditReport(String key) {
        Intent intent = new Intent(this, EditReportActivity.class);
        if (key != null && !key.isEmpty())
            intent.putExtra("key", key);
        startActivity(intent);
    }

    protected void gotoForms() {
        this.setTitle(R.string.fill_form);
        switchFragment("forms", new FormsFragment());
    }

    protected void gotoReports() {
        this.setTitle(R.string.fill_report);
        switchFragment("reports", new ReportsFragment());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private BaseFragment mCurrentFragment;

    private void switchFragment(String tag, BaseFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        mCurrentFragment = fragment;
        transaction.replace(R.id.content_fragment, fragment, tag);
        transaction.commit();
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_form) {
            gotoForms();
        } else if (id == R.id.nav_report) {
            gotoReports();
        } else if (id == R.id.nav_monitor) {
            Intent intent = new Intent(this, PhoneMonitorActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            CANSInfo db = new CANSInfo(this);
            T_User user = db.getUser();
            user.password = "";
            db.update(user);

            this.gotoLogin();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
