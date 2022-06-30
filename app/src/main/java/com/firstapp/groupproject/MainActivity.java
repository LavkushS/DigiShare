package com.firstapp.groupproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    private EditText emailEt, passwordEt;
    private Button SignInButton;
    private TextView SignUpTv;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseauth;
    private com.google.android.gms.common.SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseauth = FirebaseAuth.getInstance(); //Returns instance of class corresponding to FireBaseApp Instance
        signInButton = findViewById(R.id.google_sign_in);
        emailEt = findViewById(R.id.email);
        passwordEt = findViewById(R.id.password);
        SignInButton = findViewById(R.id.login);
        progressDialog = new ProgressDialog(this);   //creates progress dialog
        SignUpTv = findViewById(R.id.signUpTv);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                          .requestIdToken(getString(R.string.default_web_client_id))
                                                          .requestEmail()
                                                           .build();  //to configure google sign-in api
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso); //Entry point of the google sign-in api

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });



        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();

            }
        });
        SignUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                //intent takes the user to the signup activity if user not registered
                startActivity(intent);
                finish();

            }
        });

    }


    private void Login() {
        String email = emailEt.getText().toString();
        String password = passwordEt.getText().toString();
        if (TextUtils.isEmpty(email)) {  //To check if the email field is empty
            emailEt.setError("Enter your email-id");
            return;
        } else if (TextUtils.isEmpty(password)) { //To check if the password field is empty
            passwordEt.setError("Enter your password");
            return;
        }

        progressDialog.setMessage("Please Wait..");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false); //the dialog should not be canceled when touched outside the window.
        firebaseauth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() { //checks if the credentials entered is present in Firebase Auth Database
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Successfully signed in!",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "SignIn Failed!",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });

    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent(); //Gets an Intent to start the Google Sign In flow
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){//The integer request code originally supplied to startActivityForResult() is checked
           // turn a GoogleSignInAccount present in the result data for the associated Activity started via getSignInInten
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{

            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            //GoogleSignInAccount holds the basic account information of the signed in Google user.
            FirebaseGoogleAuth(acc);

        }catch (ApiException e){
            Toast.makeText(MainActivity.this, "SignIn Failed!", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);

        }

    }
    private void FirebaseGoogleAuth(GoogleSignInAccount acct){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        //Try to sign in a user with the given AuthCredential using signInWithCredential
        firebaseauth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Toast.makeText(MainActivity.this, "Successfully signed in!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();

                }
                else{

                    Toast.makeText(MainActivity.this, "SignIn Failed!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        });
    }

}
