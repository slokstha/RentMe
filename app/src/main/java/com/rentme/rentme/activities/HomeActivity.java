package com.rentme.rentme.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.rentme.rentme.R;
import com.rentme.rentme.models.User;
import com.rentme.rentme.utils.Constants;
import com.squareup.picasso.Picasso;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    NavController bottomnavController, navController;
    Toolbar toolbar;
    Menu menuItem;
    TextView header_name_tv, header_phone_tv;
    CircleImageView header_imageView;
    ImageView go_profile_iv;
    String title, statement;
    SharedPreferences preferences;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        BottomNavigationView navView = findViewById(R.id.bottom_nav_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        FloatingActionButton fab = findViewById(R.id.fab);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.navigation_vehicle)
                .build();
        bottomnavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, bottomnavController);
        navView.getMenu().getItem(1).setEnabled(false);
        navView.getMenu().getItem(2).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(HomeActivity.this, VehicleDetailActivity.class));
                return true;
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        header_name_tv = headerView.findViewById(R.id.header_name_tv);
        header_phone_tv = headerView.findViewById(R.id.header_phone_tv);
        header_imageView = headerView.findViewById(R.id.header_imageView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        preferences = getSharedPreferences(Constants.sharePrefName, MODE_PRIVATE);
        final String userData = preferences.getString("userDetails", null);
        user = new Gson().fromJson(userData, User.class);
        if (userData == null) {
            navigationView.getMenu().getItem(0).setTitle("Login/Signup");
            navigationView.getMenu().getItem(5).setVisible(false);
            title = "Hi Guest";
            statement = "Please login/signup";
            header_name_tv.setText(title);
            header_phone_tv.setText(statement);
            header_imageView.setImageResource(R.drawable.person);
        } else {
            header_name_tv.setText(user.getName());
            header_phone_tv.setText(user.getPhone());
            if (user.getProfile_pic_url() != null) {
                Picasso.get().load(user.getProfile_pic_url()).placeholder(R.drawable.person).error(R.drawable.person).into(header_imageView);
            } else {
                header_imageView.setImageResource(R.drawable.person);
            }
        }
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userData == null) {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                } else {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                }
            }
        });
        //for profile or login item click
        navigationView.getMenu().getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (userData == null) {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                } else {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                }

                return true;
            }
        });
        //for add post item click
        navigationView.getMenu().getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (userData == null) {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                } else {
                    startActivity(new Intent(HomeActivity.this, AddRoomActivity.class));
                }
                return true;
            }
        });
        //for transportation item click
        navigationView.getMenu().getItem(2).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(HomeActivity.this, VehicleDetailActivity.class));
                return true;
            }
        });
        //for help item click
        navigationView.getMenu().getItem(3).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(HomeActivity.this,HelpActivity.class));
                return true;
            }
        });
        navigationView.getMenu().getItem(4).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(HomeActivity.this,AboutActivity.class));
                return true;
            }
        });
        //for logout item click
        navigationView.getMenu().getItem(5).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                return true;
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                if (userData == null) {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                } else {

                    startActivity(new Intent(HomeActivity.this, AddRoomActivity.class));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuItem = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
