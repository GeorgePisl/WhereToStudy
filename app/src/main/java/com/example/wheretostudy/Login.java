package com.example.wheretostudy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wheretostudy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    EditText eEmail;
    EditText ePassword;
    TextView tvRegister;
    FirebaseAuth mAuth;
    TextView forgotPsw;
    ImageView visible;
    ImageView notVisible;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        FirebaseUser user = mAuth.getCurrentUser();

                if (user != null){
                    finish();
                    startActivity(new Intent(Login.this, MainActivity.class));
                }

                eEmail = (EditText) findViewById(R.id.email);
                ePassword = (EditText) findViewById(R.id.password);
                tvRegister = (TextView) findViewById(R.id.register);
                forgotPsw = (TextView) findViewById(R.id.tvForgotpsw);
                visible = (ImageView) findViewById(R.id.visibility_on);
                notVisible = (ImageView) findViewById(R.id.visibility_off);

                tvRegister.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        startActivity(new Intent(Login.this, Registration.class));
                    }

                });

                forgotPsw.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Login.this, PasswordReset.class));
                    }
                });

                visible.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        visible.setVisibility(View.INVISIBLE);
                        notVisible.setVisibility(View.VISIBLE);
                    }
                });

                notVisible.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        visible.setVisibility(View.VISIBLE);
                        notVisible.setVisibility(View.INVISIBLE);
                    }
                });
            }

            public boolean validate() {
                boolean result = true;



                if (TextUtils.isEmpty(eEmail.getText())) {

                    eEmail.setError("Email is required!");
                    result = false;
                }

                else if (!Patterns.EMAIL_ADDRESS.matcher(eEmail.getText().toString()).matches()) {

                    eEmail.setError("Email address is not valid");
                    result = false;

                }

                if (TextUtils.isEmpty(ePassword.getText())) {

                    ePassword.setError("Password is required!");
                    result = false;

                }
                else if (ePassword.getText().length()<6) {
                    Toast.makeText(Login.this, "Password need at least 6 characters", Toast.LENGTH_SHORT).show();
                    result = false;}

                return result;
            }

            public void login(View v2) {
                if (validate()) {
                    String email = eEmail.getText().toString();
                    String psw = ePassword.getText().toString();

                    progressDialog.setMessage("Please wait, you are being verified");
                    progressDialog.show();

                    mAuth.signInWithEmailAndPassword(email, psw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                startActivity(new Intent(Login.this, PermissionActivity.class));

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(Login.this, "Sorry, something went wrong. Maybe you are not registered.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }

        }

