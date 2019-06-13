package com.example.farmworkerapp.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.farmworkerapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {



    ImageView ImgUserPhoto;
    static int PReqCode = 1;
    static int REQUESESCODE = 1;
    Uri pickedImgUri;


    private EditText userEmail, userPassword, userPassword2, userName;
    private ProgressBar loadingProgress;
    private Button regButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //ini views
        userEmail = findViewById(R.id.regMail);
        userPassword = findViewById(R.id.regPassword);
        userPassword2 = findViewById(R.id.regPassword2);
        userName = findViewById(R.id.regName);
        loadingProgress = findViewById(R.id.regProgressBar);
        regButton = findViewById(R.id.regButton);
        loadingProgress.setVisibility(View.INVISIBLE);

        ImgUserPhoto =  findViewById(R.id.regUserPhoto);

        mAuth = FirebaseAuth.getInstance();



        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                regButton.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String password2 = userPassword2.getText().toString();
                final String name = userName.getText().toString();


                if(email.isEmpty() || password.isEmpty() || password2.isEmpty() || name.isEmpty() || pickedImgUri==null){
                    //something goes wrong all fields MUST be filled
                    //Display ERROR MESSAGE

                    showMessage("Please verify all fields are filled and a photo is selected");
                    regButton.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);

                }
                else{
                    //everything is okay and all fields are filled
                    //we can now start to create an account
                    //CreateUserAccount method will try to create the user if the email is valid

                    CreateUserAccount(email, name, password);

                }


            }
        });


        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT >= 22){


                    checkAndRequestForPermission();


                }
                else{

                    openGallery();


                }




            }
        });



    }

    private void CreateUserAccount(String email, final String name, String password) {
        //this method will create a user account with a specific email and password

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //user account created successfully
                    showMessage("Account Created");
                    //after we created user account we need to update their profile picture and name
                    updateUserInfo(name, pickedImgUri, mAuth.getCurrentUser());

                }
                else{

                    //account creation failed
                    showMessage("Account Creation Failed" + task.getException().getMessage());
                    regButton.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE) ;

                }
            }
        });

    }

    //update user photo and name
    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {

        //first we need to upload user photo to firebase storage and get the uri

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //image uploaded successfully
                //now we can get our image url

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        //uri contain user image url

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(uri).build();

                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    //user info updated successfully
                                    showMessage("Registration Complete");
                                    updateUI();
                                }


                            }
                        });

                    }
                });


            }
        });


    }

    private void updateUI() {

        Intent homeActivity = new Intent(getApplicationContext(), HomePickerActivity.class);
        startActivity(homeActivity);
        finish();

    }


    //simple method to show toast message
    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

    }


    private void openGallery() {
        //user picking image

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESESCODE);

    }

    private void checkAndRequestForPermission() {


        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){

                Toast.makeText(RegisterActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();

            }


            else
            {
                ActivityCompat.requestPermissions(RegisterActivity.this,
                                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                                    PReqCode);
            }
        }


        else{
            openGallery();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUESESCODE && data!= null){

            //the user has successfully picked an image
            //we need to save its reference to a Url variable
            pickedImgUri = data.getData();
            ImgUserPhoto.setImageURI(pickedImgUri);



        }
    }

}
