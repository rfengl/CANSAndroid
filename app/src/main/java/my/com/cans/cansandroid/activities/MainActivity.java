package my.com.cans.cansandroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.objects.CANSInfo;
import my.com.cans.cansandroid.objects.dbo.T_User;
import my.com.cans.cansandroid.services.BaseAPICallback;
import my.com.cans.cansandroid.services.BaseAPIResponse;
import my.com.cans.cansandroid.services.MobileAPI;
import my.com.cans.cansandroid.services.MyHTTP;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
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

        verifyUser();
    }

    protected void verifyUser() {
        new MyHTTP(this).call(MobileAPI.class).verify().enqueue(new BaseAPICallback<BaseAPIResponse>(this) {
            @Override
            public void onResponse(Call<BaseAPIResponse> call, Response<BaseAPIResponse> response) {
                BaseAPIResponse resp = response.body();
                if (!resp.Succeed)
                    MainActivity.this.gotoLogin();
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            gotoEditForm("9ed4fdbc-0e40-4074-9b24-b4f9f3369761");
        } else if (id == R.id.nav_report) {
            gotoEditReport("test");
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
