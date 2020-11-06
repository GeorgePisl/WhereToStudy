package com.example.wheretostudy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wheretostudy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordReset extends AppCompatActivity {

    EditText passwordEmail;
    Button resetPassword;
    FirebaseAuth mAuth;
    TextView cancelReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

                passwordEmail = (EditText) findViewById(R.id.email_reset);
                resetPassword = (Button) findViewById(R.id.buttonResetPsw);
                mAuth = FirebaseAuth.getInstance();
                cancelReset = (TextView) findViewById(R.id.cancel);

                cancelReset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(PasswordReset.this, MainActivity.class));
                    }
                });


            }

            public void resetpsw(View v){
                String useremail = passwordEmail.getText().toString().trim();

                if(TextUtils.isEmpty(passwordEmail.getText())) {
                    Toast.makeText(PasswordReset.this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PasswordReset.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(PasswordReset.this, MainActivity.class));
                            }
                            else {
                                Toast.makeText(PasswordReset.this, "Error in sending password reset email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
    }
}