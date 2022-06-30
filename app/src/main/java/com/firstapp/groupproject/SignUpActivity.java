package com.firstapp.groupproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailEt, passwordEt1, passwordEt2;
    private Button SignUpButton;
    private TextView SignInTv;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseauth; //creating instance of FirebaseAuth

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        firebaseauth = FirebaseAuth.getInstance();
        emailEt = findViewById(R.id.email);
        passwordEt1 = findViewById(R.id.password1);
        passwordEt2 = findViewById(R.id.password2);
        SignUpButton = findViewById(R.id.register);
        progressDialog = new ProgressDialog(this);
        SignInTv = findViewById(R.id.signInTv);
        SignUpButton.setOnClickListener(new View.OnClickListener() { //When sign up button is clicked
            @Override
            public void onClick(View v) {
                Register();

            }
        });
        SignInTv.setOnClickListener(new View.OnClickListener() { //Takes user to the signin activity
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }
    private void Register(){
        //Takes the email and password entered by the user as input to be stored in FireBaseAuth
        String email=emailEt.getText().toString();
        String password1=passwordEt1.getText().toString();
        String password2=passwordEt2.getText().toString();
        if(TextUtils.isEmpty(email)){//To check if email field is empty
            emailEt.setError("Enter your email-id");
            return;
        }
        else if(TextUtils.isEmpty(password1)){ //To check if password field is empty
            passwordEt1.setError("Enter your password");
            return;
        }
        else if(TextUtils.isEmpty(password2)){ //To check if the password field is empty
            passwordEt2.setError("Confirm your password");
            return;
        }
        else if(!password1.equals(password2)){ //To check if the two passwords are equal or not
            passwordEt2.setError("Different password");
            return;
        }
        else if(password1.length()<6){ //To check the password length
            passwordEt2.setError("Length is short(It should be of atleast 6 characters!)");
            return;
        }
        else if(!isValidEmail(email)){//To check if valid email is entered
            emailEt.setError("Invalid email");
            return;
        }
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        //Register user in FirebaseAuth if credentials entered are as per the instructions
        firebaseauth.createUserWithEmailAndPassword(email,password1).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this,"Successfully registered",
                                    Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(SignUpActivity.this,DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                                Toast.makeText(SignUpActivity.this,"SignUp Failed!",
                                        Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                        }
                    }
                });

    }
    private Boolean isValidEmail(CharSequence target){//For checking if valid email is entered or not
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());

    }
}
