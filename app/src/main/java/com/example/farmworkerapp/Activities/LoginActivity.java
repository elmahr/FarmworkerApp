package com.example.farmworkerapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText userMail, userPassword;
    private Button btnLogin;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth;
    private Intent HomePickerActivity;
    private Intent HomeCheckerActivity;
    private Button createAcc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        userMail = findViewById(R.id.loginMail);
        userPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.loginBtn);
        loginProgress = findViewById(R.id.loginProgress);
        mAuth = FirebaseAuth.getInstance();
        HomePickerActivity = new Intent(this, com.example.farmworkerapp.Activities.HomePickerActivity.class);
        createAcc = findViewById(R.id.createAccount);
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerActiviy = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerActiviy);
                finish();



            }
        });


        loginProgress.setVisibility(View.INVISIBLE);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgress.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                final String mail = userMail.getText().toString();
                final String password = userPassword.getText().toString();

                if(mail.isEmpty() || password.isEmpty()){
                    showMessage("Please Verify All Fields");
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);

                }
                else{
                    signIn(mail,password);
                }
            }
        });



    }

    private void signIn(final String mail, String password) {
        System.out.println("HEREHEREHEREHERE");
        System.out.println(mail);
        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful() && mail == "sophiap@yahoo.com"){
                    System.out.println("IN CHECKER");
                    loginProgress.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    updateUIChecker();
                }
                else if(task.isSuccessful()){
                    System.out.println("IN PICKER");
                    loginProgress.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    updateUIPicker();
                }
                else{
                    showMessage(task.getException().getMessage());
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    private void updateUIPicker() {

        startActivity(HomePickerActivity);
        finish();

    }

    private void updateUIChecker() {

        startActivity(HomePickerActivity);
        finish();

    }

    private void showMessage(String text) {



        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();


    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        //String name = user.getDisplayName();
        System.out.println("the current user is: " + user);
        //System.out.println("the current name is: " +name);

        if(user != null){
            //user is already connected so we redirect to their respective home page
            updateUIPicker();
        }

    }
}

/*            if(name == "Sophia Petrillo"){
                updateUIChecker();
            }
            else {
                updateUIPicker();
            }*/