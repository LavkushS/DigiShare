package com.firstapp.groupproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    private Button logout;
    private Button addbook;
    private Button visit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() { //When logout button is clicked
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(DashboardActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
        addbook=findViewById(R.id.addbook);
        addbook.setOnClickListener(new View.OnClickListener() { //when Add book button is clicked
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashboardActivity.this,AddBookActivity.class);
                startActivity(intent);

            }
        });
        visit=findViewById(R.id.visit);
        visit.setOnClickListener(new View.OnClickListener() { //when Visit Store Button is Clicked
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashboardActivity.this,VisitStoreActivity.class);
                startActivity(intent);

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Showing items in the menu

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_about){//User taken to the activity where app information is provided
            Intent intent = new Intent(DashboardActivity.this,AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
