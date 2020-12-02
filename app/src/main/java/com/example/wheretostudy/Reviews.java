package com.example.wheretostudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Reviews extends AppCompatActivity {
Button add;
FirebaseFirestore firebaseFirestore;
FirebaseAuth mAuth;
FirebaseAuth.AuthStateListener authStateListener;
TextView nSeats, title;
CheckedTextView airC, sock, lib, vendMac;
RecyclerView recyclerView;
Calendar calendar;
SimpleDateFormat simpleDateFormat;
String currentDate, buildingName, className;
FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        className = intent.getStringExtra("ClassroomName");
        String classSeats = intent.getStringExtra("ClassroomSeats");
        buildingName = intent.getStringExtra("BuildingName");

        nSeats = findViewById(R.id.seats_per_room);
        nSeats.setText("Seats available: " +classSeats);

        title = findViewById(R.id.descriptionTitle);
        title.setText(className);

        airC = findViewById(R.id.air_cond);
        airC.setChecked(intent.getBooleanExtra("AirC", false));
        sock = findViewById(R.id.sockets);
        sock.setChecked(intent.getBooleanExtra("Sockets", false));
        lib = findViewById(R.id.library);
        lib.setChecked(intent.getBooleanExtra("Library", false));
        vendMac = findViewById(R.id.vending_machines);
        vendMac.setChecked(intent.getBooleanExtra("Vend_mach", false));

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_reviews);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        recyclerView = findViewById(R.id.reviewsList);

        ImageView menu = toolbar.findViewById(R.id.lateralMenu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_reviews);
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(drawerLayout.getApplicationWindowToken(),0);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });


        mAuth = FirebaseAuth.getInstance();
        //part for navigation drawer
        NavigationView navigationView = findViewById(R.id.nav_view_reviews);
        navigationView.getMenu().getItem(0).setChecked(true);
        //update username, email and profile picture in header
        View headerView = navigationView.getHeaderView(0);
        TextView navUser = headerView.findViewById(R.id.profileUsername);
        navUser.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        TextView navEmail = headerView.findViewById(R.id.profileEmail);
        navEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        //ImageView navPhoto = headerView.findViewById(R.id.profileImageView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    item.setChecked(false);
                    Intent myIntent = new Intent(Reviews.this, MapActivity.class);
                    Reviews.this.startActivity(myIntent);


                } else if (item.getItemId() == R.id.nav_qr) {
                    item.setChecked(false);
                    Intent myIntent = new Intent(Reviews.this, QRActivity.class);
                    Reviews.this.startActivity(myIntent);

                } else if (item.getItemId() == R.id.nav_chat) {
                    item.setChecked(false);
                    Intent myIntent = new Intent(Reviews.this, ChatActivity.class);
                    Reviews.this.startActivity(myIntent);

                } else if (item.getItemId() == R.id.nav_logout) {
                    item.setChecked(false);
                    mAuth.signOut();
                    FacebookSdk.sdkInitialize(getApplicationContext());
                    LoginManager.getInstance().logOut();
                    AccessToken.setCurrentAccessToken(null);
                    finish();
                    startActivity(new Intent(Reviews.this, Login.class));
                    if (authStateListener != null) {
                        mAuth.removeAuthStateListener(authStateListener);
                    }
                }
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_reviews);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;


            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
        //Query
        Query query = firebaseFirestore.collection("Reviews").whereEqualTo("Classroom", className);
        //RecyclerOptions
        FirestoreRecyclerOptions <ReviewModel> options = new FirestoreRecyclerOptions.Builder<ReviewModel>()
                .setQuery(query, ReviewModel.class)
                .build();
        //ViewHolder
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<ReviewModel, ReviewsViewHolder>(options) {
            @NonNull
            @Override
            public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_review_item, parent, false);
                return new ReviewsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ReviewsViewHolder holder, int position, @NonNull ReviewModel model) {
                holder.username.setText(model.getUserName());
                holder.commentText.setText(model.getComment());
                holder.date.setText(model.getDate());
                holder.rate.setRating(Float.parseFloat(model.getRate()));

            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Reviews.this));
        recyclerView.setAdapter(firestoreRecyclerAdapter);

        add = findViewById(R.id.addReviewBtn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReviewPopUp();
            }
        });



    }

    private void openReviewPopUp() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(Reviews.this);
        View mView = getLayoutInflater().inflate(R.layout.add_review, null);

        TextView cancel = (TextView) mView.findViewById(R.id.btn_Cancel);

        TextView user = mView.findViewById(R.id.username_review);
        user.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        Button ok = (Button) mView.findViewById(R.id.addBtn);
        EditText msg = (EditText) mView.findViewById(R.id.commentReview);

        RatingBar stars = mView.findViewById(R.id.ratingBar);

        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //EditText msg = (EditText) findViewById(R.id.input);
                String text = msg.getText().toString();
                Float rate = stars.getRating();
                calendar = Calendar.getInstance();
                simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                currentDate = simpleDateFormat.format(calendar.getTime());

                if (rate<1) {

                    Toast.makeText(Reviews.this, "Please rate at least one star", Toast.LENGTH_LONG).show();
                }
                else {
                    storeUserReview(user.getText().toString(), rate, text);
                    alertDialog.dismiss();
                }


            }
        });

        alertDialog.show();
    }

    private void storeUserReview(String user, Float rate, String text) {
        Map<String,String> userData = new HashMap<>();
        userData.put("Username", user);
        userData.put("Comment", text);
        userData.put("Rate", rate.toString());
        userData.put("Date", currentDate);
        userData.put("Classroom", className);

        firebaseFirestore.collection("Reviews").document().set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    //Toast.makeText(Reviews.this, "Successfull", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(Reviews.this, "Firestore Error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private class ReviewsViewHolder extends RecyclerView.ViewHolder {

        private TextView username, commentText, date, time;
        private RatingBar rate;


        public ReviewsViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.commentUsername);
            commentText = itemView.findViewById(R.id.commentText);
            date = itemView.findViewById(R.id.commentDate);
            rate = itemView.findViewById(R.id.commentRatingBar);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firestoreRecyclerAdapter.stopListening();
    }
    @Override
    protected void onStart() {
        super.onStart();
        firestoreRecyclerAdapter.startListening();
    }
}

