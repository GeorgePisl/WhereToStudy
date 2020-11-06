package com.example.wheretostudy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wheretostudy.R;
import com.example.wheretostudy.ClientHelperClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.UUID;


public class Registration extends AppCompatActivity {

    EditText eUsername, eEmail, ePassword;
    Button registerbtn;
    FirebaseAuth mAuth;
    private ImageView profilePic;
    Uri imageUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    TextView tvCancel;
    ImageView visible;
    ImageView notVisible;
    ProgressDialog pdRegistration;
    boolean uploaded = false;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

                eUsername = findViewById(R.id.editTextUsername);
                eEmail = (EditText) findViewById(R.id.emailRegister);
                ePassword = (EditText) findViewById(R.id.passwordRegister);

                profilePic = (ImageView) findViewById(R.id.profilePicture);

                visible = (ImageView) findViewById(R.id.visibility_on);
                notVisible = (ImageView) findViewById(R.id.visibility_off);

                pdRegistration = new ProgressDialog(this);

                registerbtn = (Button) findViewById(R.id.buttonRegister);
                tvCancel = (TextView) findViewById(R.id.tvCancel);


                storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference();

                mAuth = FirebaseAuth.getInstance();


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

                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Registration.this, Login.class));
                    }
                });

            }


            public void addProfilePic(View view) {
                choosePicture();
            }

            public void choosePicture() {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }

            @Override
            protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    imageUri = data.getData();
                    profilePic.setImageURI(imageUri);
                    uploadPicture();
                }
            }

            private void uploadPicture() {

                final ProgressDialog pd = new ProgressDialog(this);
                pd.setTitle("Uploading image...");
                pd.show();

                final String randomKey = UUID.randomUUID().toString();
                StorageReference riversRef = storageReference.child("Profile Picture/" + randomKey);

                riversRef.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                pd.dismiss();
                                Snackbar.make(findViewById(android.R.id.content), "Image uploaded succesfully", Snackbar.LENGTH_SHORT).show();
                                uploaded = true;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                double progressPercent = (100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                pd.setMessage("Percentage: " + (int) progressPercent + "%");
                            }
                        });
            }


            public void register(View v) {
                String userName = eUsername.getText().toString().trim();

                if (TextUtils.isEmpty(userName)) {
                    eUsername.setError("Username is required");
                    return;
                }
                else {
                    if (validate()) {
                        //upload data to database
                        checkEmail(v);

                    }


                }

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
                else if (ePassword.getText().length() < 6) {
                    Toast.makeText(Registration.this, "Password need at least 6 characters", Toast.LENGTH_SHORT).show();
                    result = false;
                }

                return result;
            }


            public void checkEmail(View view) {

                final String eMail = eEmail.getText().toString().trim();
                final String passWord = ePassword.getText().toString().trim();

                mAuth.fetchSignInMethodsForEmail(eEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                boolean check = task.getResult().getSignInMethods().isEmpty();

                                if (!check) {
                                    Toast.makeText(Registration.this, "This email has already been registered.", Toast.LENGTH_SHORT).show();
                                } else {

                                    if (uploaded == false){
                                        goOn(eMail, passWord);
                                    }
                                    else {
                                        pdRegistration.setMessage("Please wait, you are being registered");
                                        pdRegistration.show();

                                        mAuth.createUserWithEmailAndPassword(eMail, passWord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                if (task.isSuccessful()) {
                                                    pdRegistration.dismiss();
                                                    uploadDataOnDatabase();
                                                    Intent intent = new Intent(Registration.this, PermissionActivity.class);
                                                    intent.putExtra("username", eUsername.getText());
                                                    startActivity(intent);
                                                } else {
                                                    pdRegistration.dismiss();
                                                    Toast.makeText(Registration.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
            }

            private void uploadDataOnDatabase() {
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("Clients");

                String clientUsername = eUsername.getText().toString();
                String clientEmail = eEmail.getText().toString();
                String clientPassword = ePassword.getText().toString();

                ClientHelperClass helperClass = new ClientHelperClass();
                helperClass.setUsername(clientUsername);
                helperClass.setEmail(clientEmail);
                helperClass.setPassword(clientPassword);

                databaseReference.child(String.valueOf(eUsername.getText())).setValue(helperClass);

            }


            public void goOn(final String eMail, final String passWord) {

                final AlertDialog.Builder alert = new AlertDialog.Builder(Registration.this);
                View mView = getLayoutInflater().inflate(R.layout.custom_dialog, null);

                final TextView text = (TextView) mView.findViewById(R.id.editableText);
                text.setText("Attention!\nYou are not uploading any profile picture.\nYou will be able to upload it later in your profile page.");

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
                    }
                });

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pdRegistration.setMessage("Please wait, you are being registered");
                        pdRegistration.show();

                        mAuth.createUserWithEmailAndPassword(eMail, passWord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    pdRegistration.dismiss();
                                    uploadDataOnDatabase();
                                    Intent intent = new Intent(Registration.this, PermissionActivity.class);
                                    intent.putExtra("username", eUsername.getText());
                                    startActivity(intent);
                                } else {
                                    pdRegistration.dismiss();
                                    Toast.makeText(Registration.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });

                alertDialog.show();

            }

        }

