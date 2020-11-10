package com.example.wheretostudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseListAdapter;

import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;
import android.os.Bundle;

public class ChatActivity extends AppCompatActivity {

    private static final int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.nav_activity_chat);

            //part for navigation drawer
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                                                                 @Override
                                                                 public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                                                     if(item.getItemId() == R.id.nav_home){
                                                                         Intent myIntent = new Intent(ChatActivity.this, MapActivity.class);
                                                                         ChatActivity.this.startActivity(myIntent);

                                                                     }
                                                                     else if (item.getItemId() == R.id.nav_test){
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
            adapter = new FirebaseListAdapter<ChatMessage>(options){


                @Override
                protected void populateView(View v, ChatMessage model, int position) {
                    Log.i("MESSAGGIO", "prova di nuovo");
                    //get the views
                    RelativeLayout messageLayout = (RelativeLayout) v.findViewById(R.id.message_bubble_layout);
                    RelativeLayout messageLayoutParent = (RelativeLayout) v.findViewById(R.id.message_bubble_layout_parent);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageLayout.getLayoutParams();

                    TextView messageText = (TextView) v.findViewById(R.id.message_text);
                    TextView messageTime = (TextView) v.findViewById(R.id.message_time);
                    TextView messageUser = (TextView) v.findViewById(R.id.message_user);

                    // if (model.getMessageUser().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                    messageLayout.setBackgroundResource(R.drawable.bubble2);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    messageLayout.setLayoutParams(params);
            /*
                }
                // If not my message then align to left
                else {
                    messageLayout.setBackgroundResource(R.drawable.bubble1);
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    messageLayout.setLayoutParams(params);
                }
*/

                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getMessageUser());
                    messageTime.setText(DateFormat.format("HH:mm A", model.getMessageTime()));
                    Log.i("MESSAGGIO", model.getMessageText());


                }
            };

            messageList.setAdapter(adapter);
            Log.i("TAAAAAAAAAAAAG", "sono arrivato dido");
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



    public void postMessage(View v){
        EditText msg = (EditText) findViewById(R.id.input);
        String text = msg.getText().toString();
        //Log.i("TAAAAAAAAAAAAG", "ciao dido");
        if(text != null && !text.equals("")) {
            // Read the input field and push a new instance
            // of ChatMessage to the Firebase database
            FirebaseDatabase.getInstance()
                    .getReference().child("chats")
                    .push()
                    .setValue(new ChatMessage(msg.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
            msg.setText("");
        } else {
            Toast.makeText(this, "Please enter something !!", Toast.LENGTH_LONG).show();
        }

    }


}