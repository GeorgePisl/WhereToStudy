package com.example.wheretostudy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wheretostudy.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.facebook.FacebookSdk;
import com.google.firebase.auth.UserProfileChangeRequest;


public class Login extends AppCompatActivity {
    EditText eEmail;
    EditText ePassword;
    TextView tvRegister;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    TextView forgotPsw;
    ImageView visible;
    ImageView notVisible;
    LoginButton loginFacebook;
    AccessTokenTracker accessTokenTracker;
    private CallbackManager mCallBackManager;
    private ProgressDialog progressDialog;
    private static final String TAG = "FacebookAuthentication";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        progressDialog = new ProgressDialog(this);
        FirebaseUser user = mAuth.getCurrentUser();


                if (user != null){
                    finish();
                    startActivity(new Intent(Login.this, MapActivity.class));
                }

                eEmail = (EditText) findViewById(R.id.email);
                ePassword = (EditText) findViewById(R.id.password);
                tvRegister = (TextView) findViewById(R.id.register);
                forgotPsw = (TextView) findViewById(R.id.tvForgotpsw);
                visible = (ImageView) findViewById(R.id.visibility_on);
                notVisible = (ImageView) findViewById(R.id.visibility_off);
                loginFacebook = (LoginButton) findViewById(R.id.login_button);
                loginFacebook.setReadPermissions("email", "public_profile");

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

                mCallBackManager = CallbackManager.Factory.create();
                loginFacebook.registerCallback(mCallBackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "onSuccess" +loginResult);
                        handleFacebookToken(loginResult.getAccessToken());
                        //finish();
                        //startActivity(new Intent(Login.this, PermissionActivity.class));
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "onError" + error);
                    }
                });

                authStateListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user!=null){
                            finish();
                            startActivity(new Intent(Login.this, PermissionActivity.class));

                        }
                    }
                };

                accessTokenTracker = new AccessTokenTracker() {
                    @Override
                    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                        if (currentAccessToken == null) {
                            mAuth.signOut();
                        }
                    }
                };

            }

            private void handleFacebookToken(AccessToken token) {
                Log.d(TAG, "handleFacebookToken" + token);

                AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
                mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Sign in with credential: successful");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Login.this, "Authentication succeded", Toast.LENGTH_SHORT).show();
                            if (user!=null){



                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(user.getDisplayName()).build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.i("UTENTEEEE", "User profile updated.");
                                                }
                                            }
                                        });
                                startActivity(new Intent(Login.this, PermissionActivity.class));
                            }
                        }
                        else {
                            Log.d(TAG, "Sign in with credential: failed", task.getException());
                            Toast.makeText(Login.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                mCallBackManager.onActivityResult(requestCode, resultCode, data);
                super.onActivityResult(requestCode, resultCode, data);
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

            @Override
            protected void onStart() {
                super.onStart();
                mAuth.addAuthStateListener(authStateListener);
            }

            @Override
            protected  void onStop() {
                super.onStop();
                if (authStateListener != null) {
                    mAuth.removeAuthStateListener(authStateListener);
                }
            }

        }

