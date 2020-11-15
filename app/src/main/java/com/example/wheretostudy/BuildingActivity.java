package com.example.wheretostudy;

import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BuildingActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseListAdapter<Classroom> adapter;

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView menu = toolbar.findViewById(R.id.lateralMenu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        Bundle bundle = getIntent().getExtras();
        String buildingName = bundle.getString("building");
        String imageRsc = bundle.getString("image");
        System.out.println(imageRsc);

        //part for navigation drawer
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.nav_home){
                    item.setChecked(false);
                    Intent myIntent = new Intent(BuildingActivity.this, MapActivity.class);
                    BuildingActivity.this.startActivity(myIntent);

                }
                else if (item.getItemId() == R.id.nav_chat){
                    item.setChecked(false);
                    Intent myIntent = new Intent(BuildingActivity.this, ChatActivity.class);
                    BuildingActivity.this.startActivity(myIntent);
                }
                else if (item.getItemId() == R.id.nav_logout){
                    item.setChecked(false);
                    mAuth.signOut();
                    FacebookSdk.sdkInitialize(getApplicationContext());
                    LoginManager.getInstance().logOut();
                    AccessToken.setCurrentAccessToken(null);
                    finish();
                    startActivity(new Intent(BuildingActivity.this, Login.class));
                    if (authStateListener != null) {
                        mAuth.removeAuthStateListener(authStateListener);
                    }
                }
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        ListView classroomList = (ListView) findViewById(R.id.list_classroom);
        ImageView planimetry_photo = (ImageView) findViewById(R.id.planimetry_photo);
        int id = this.findViewById(android.R.id.content).getContext().getResources().getIdentifier("drawable/"+ "planimetry_" + imageRsc, null, this.findViewById(android.R.id.content).getContext().getPackageName());
        System.out.println("fashjkfhsajk" + id);
        planimetry_photo.setImageResource(id);
        Query query = FirebaseDatabase.getInstance().getReference().child("locations").child(buildingName).child("classroom");
        System.out.println(query);
        FirebaseListOptions<Classroom> options = new FirebaseListOptions.Builder<Classroom>()
                .setQuery(query, Classroom.class)
                .setLayout(R.layout.list_classroom_layout)
                .build();

        adapter = new FirebaseListAdapter<Classroom>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Classroom model, int position) {
                TextView tName = (TextView) v.findViewById(R.id.classroom_name);
                TextView tSeats = (TextView) v.findViewById(R.id.classroom_seats);
                tName.setText(model.getName());
                tSeats.setText("Places Available: " + String.valueOf(model.getAvailable()) + "/" + String.valueOf(model.getCapacity()));
            }

        };
        classroomList.setAdapter(adapter);
    }
}
