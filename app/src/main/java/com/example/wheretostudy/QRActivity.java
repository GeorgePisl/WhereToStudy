package com.example.wheretostudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import javax.xml.transform.Result;

public class QRActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    CodeScanner mCodeScanner;
    private final static int CAMERA_REQUEST_CODE = 101;


    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_REQUEST_CODE);
    }


    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(QRActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void codeScanner() {
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setCamera(CodeScanner.CAMERA_BACK);
        mCodeScanner.setFormats(CodeScanner.ALL_FORMATS);
        mCodeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        mCodeScanner.setScanMode(ScanMode.CONTINUOUS);
        mCodeScanner.setAutoFocusEnabled(true);
        mCodeScanner.setFlashEnabled(false);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull com.google.zxing.Result result) {
                QRActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCodeScanner.stopPreview();
                        String qr_result = result.getText();
                        final List<String> info_classroom = Arrays.asList(qr_result.split("-"));
                        System.out.println(info_classroom.toString());
                        Toast.makeText(QRActivity.this, info_classroom.get(0) + " CLASSROOM: " + info_classroom.get(1), Toast.LENGTH_SHORT).show();
                        View mView;
                        if (info_classroom.contains("entrata")) {
                            mView = getLayoutInflater().inflate(R.layout.custom_entry, null);

                            //System.out.println("ENTRATA");
                        } else {
                            mView = getLayoutInflater().inflate(R.layout.custom_exit, null);
                            //System.out.println("USCITA");
                        }
                        final AlertDialog.Builder alert = new AlertDialog.Builder(QRActivity.this);
                        TextView cancel, ok;
                        cancel = (TextView) mView.findViewById(R.id.btn_Cancel);
                        ok = (TextView) mView.findViewById(R.id.btn_Ok);
                        alert.setView(mView);

                        final AlertDialog alertDialog = alert.create();
                        alertDialog.setCanceledOnTouchOutside(false);

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                mCodeScanner.startPreview();
                            }
                        });

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DatabaseReference f_database = FirebaseDatabase.getInstance().getReference().child("locations").child(info_classroom.get(0)).child("classroom").child(info_classroom.get(1));
                                f_database.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (info_classroom.get(2).equals("entrata")) {
                                            int available = Integer.parseInt(snapshot.child("available").getValue().toString());
                                            if (available - 1 >= 0) {
                                                snapshot.child("available").getRef().setValue(available - 1);
                                                Toast.makeText(QRActivity.this, "SUCCESS!! YOU CAN ENTER THE CLASSROOM", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(QRActivity.this, "I'M SORRY THERE ARE NO AVAILABLE SEATS", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            int capacity = Integer.parseInt(snapshot.child("capacity").getValue().toString());
                                            int available = Integer.parseInt(snapshot.child("available").getValue().toString());
                                            if(available + 1 <= capacity){
                                                snapshot.child("available").getRef().setValue(available + 1);
                                                Toast.makeText(QRActivity.this, "SUCCESS!! YOU FREED A SEAT", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(QRActivity.this, "ERROR MAXIMUM CAPACITY REACHED", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                                alertDialog.dismiss();
                                mCodeScanner.startPreview();

                            }
                        });
                        alertDialog.show();

                    }
                });
            }
        });

        mCodeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull Exception error) {
                QRActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("QR-ACTIVITY", "Camera initialization error: " + error.getMessage());
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_qr);

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
                    Intent myIntent = new Intent(QRActivity.this, MapActivity.class);
                    QRActivity.this.startActivity(myIntent);

                } else if (item.getItemId() == R.id.nav_chat) {
                    item.setChecked(false);
                    Intent myIntent = new Intent(QRActivity.this, ChatActivity.class);
                    QRActivity.this.startActivity(myIntent);
                } else if (item.getItemId() == R.id.nav_logout) {
                    item.setChecked(false);
                    mAuth.signOut();
                    FacebookSdk.sdkInitialize(getApplicationContext());
                    LoginManager.getInstance().logOut();
                    AccessToken.setCurrentAccessToken(null);
                    finish();
                    startActivity(new Intent(QRActivity.this, Login.class));
                    if (authStateListener != null) {
                        mAuth.removeAuthStateListener(authStateListener);
                    }
                }
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        if (checkPermission()) {
            codeScanner();
        } else {
            requestPermission();
            codeScanner();

        }
    }
}