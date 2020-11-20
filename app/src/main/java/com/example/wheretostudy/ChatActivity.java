package com.example.wheretostudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


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

import java.util.Random;

import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

public class ChatActivity extends AppCompatActivity {

    private static final int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    public void postMessage(View v) {
        EditText msg = (EditText) findViewById(R.id.input);
        String text = msg.getText().toString();
        //Log.i("TAAAAAAAAAAAAG", "ciao dido");

        DatabaseReference f_database = FirebaseDatabase.getInstance().getReference().child("Clients").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("room");
        f_database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String roomUser = "";
                if (dataSnapshot.getValue() != null) {
                    roomUser = dataSnapshot.getValue().toString();
                }


                if (text != null && !text.equals("")) {
                    // Read the input field and push a new instance
                    // of ChatMessage to the Firebase database
                    FirebaseDatabase.getInstance()
                            .getReference().child("chats")
                            .push()                                             //getReference("Clients")
                            .setValue(new ChatMessage(msg.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), roomUser));
                    msg.setText("");
                    //Log.i("TAAAAAAAAAAAAG", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    //Log.i("TAAAAAAAAAAAAG", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                } else {
                    Toast.makeText(ChatActivity.this, "Please enter something !!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_chat);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle  actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.menu_chat, R.string.menu_chat) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        ImageView menu = toolbar.findViewById(R.id.lateralMenu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout = findViewById(R.id.drawer_layout);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(drawerLayout.getApplicationWindowToken(), 0);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });


        mAuth = FirebaseAuth.getInstance();
        //part for navigation drawer
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);
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
                    Intent myIntent = new Intent(ChatActivity.this, MapActivity.class);
                    ChatActivity.this.startActivity(myIntent);


                } else if (item.getItemId() == R.id.nav_qr) {
                    item.setChecked(false);
                    Intent myIntent = new Intent(ChatActivity.this, QRActivity.class);
                    ChatActivity.this.startActivity(myIntent);

                } else if (item.getItemId() == R.id.nav_logout) {
                    item.setChecked(false);
                    mAuth.signOut();
                    FacebookSdk.sdkInitialize(getApplicationContext());
                    LoginManager.getInstance().logOut();
                    AccessToken.setCurrentAccessToken(null);
                    finish();
                    startActivity(new Intent(ChatActivity.this, Login.class));
                    if (authStateListener != null) {
                        mAuth.removeAuthStateListener(authStateListener);
                    }
                }
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;


            }
        });
        // User is already signed in. Therefore, display
        // a welcome Toast


        //Log.i("TAAAAAAAAAAAAG", "sono arrivato quddddddi");
        ListView messageList = (ListView) findViewById(R.id.messagesList);
        messageList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        messageList.setStackFromBottom(true);


        //adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.message_layout,
        //     FirebaseDatabase.getInstance().getReference()) {


        Query query = FirebaseDatabase.getInstance().getReference().child("chats");
        //DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        //database.keepSynced(true);
        Log.i("TAAAAAAAAAAAAG", "sono arrivato qui");
//The error said the constructor expected FirebaseListOptions - here you create them:
        FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .setLayout(R.layout.message_layout)
                .build();


        //Finally you pass them to the constructor here:
        adapter = new FirebaseListAdapter<ChatMessage>(options) {
            @Override
            protected void populateView(@NotNull View v, @NotNull ChatMessage model, int position) {
                TextView messageUtente = (TextView) v.findViewById(R.id.message_user);
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);


                RelativeLayout messageLayout = (RelativeLayout) v.findViewById(R.id.message_bubble_layout);
                //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageLayout.getLayoutParams();

                if (model.getMessageUser().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {


                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    //params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    messageLayout.setLayoutParams(params);

                    // messageLayout.setGravity(Gravity.RIGHT);

                    messageLayout.setBackgroundResource(R.drawable.bubble2);
                    messageUtente.setTextColor(Color.RED);
                    // params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    // messageLayout.setLayoutParams(params);

                }
                // If not my message then align to left
                else {
                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    messageUtente.setTextColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));


                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    //params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    messageLayout.setLayoutParams(params);


                    //messageLayout.setGravity(Gravity.LEFT);
                    messageLayout.setBackgroundResource(R.drawable.bubble1);
                    //params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    //messageLayout.setLayoutParams(params);

                }
                String aula;
                if (model.getMessageRoom().equals("")) {
                    aula = "";
                } else {
                    aula = " - " + model.getMessageRoom();
                }

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser() + aula);
                messageTime.setText(DateFormat.format("HH:mm A", model.getMessageTime()));
                //Log.i("MESSAGGIO", model.getMessageText());


            }
        };

        messageList.setAdapter(adapter);
        //Log.i("TAAAAAAAAAAAAG", "sono arrivato dido");
    }


/*
            if(FirebaseAuth.getInstance().getCurrentUser() == null){
                // Start sign in/sign up activity
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .build(),
                        SIGN_IN_REQUEST_CODE
                );
            } else {
                // User is already signed in. Therefore, display
                // a welcome Toast
                Toast.makeText(this,
                        "Welcome " + FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getDisplayName(),
                        Toast.LENGTH_LONG)
                        .show();

                displayChatMessages();
            }*/

/*
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == SIGN_IN_REQUEST_CODE){
                if(resultCode == RESULT_OK){
                    Toast.makeText(this, "Successfully signed in. Welcome!", Toast.LENGTH_LONG).show();
                    displayChatMessages();
                }

                else {
                    Toast.makeText(this, "Sorry, couldn't sign you in. Please try again later",
                            Toast.LENGTH_LONG)
                            .show();
                    finish();
                }
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.main_menu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if(item.getItemId() == R.id.menu_sign_out){
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MainActivity.this, "You have been signed out!", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
            }
            return true;
        }
    */


}