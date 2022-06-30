package com.firstapp.groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class details extends AppCompatActivity {

    TextView name,author,pages,genre,desc;
    Button btn; //go back to recycler view
    ImageView viewpdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        name = findViewById(R.id.name);
        author = findViewById(R.id.author);
        pages = findViewById(R.id.pages);
        genre = findViewById(R.id.genre);
        desc = findViewById(R.id.desc);
        viewpdf = findViewById(R.id.viewpdf);
        btn = findViewById(R.id.back);

        //Book details are displayed
        name.setText(getIntent().getStringExtra("uname").toString());
        author.setText(getIntent().getStringExtra("uauthor").toString());
        pages.setText(getIntent().getStringExtra("upages").toString());
        genre.setText(getIntent().getStringExtra("ugenre").toString());
        desc.setText(getIntent().getStringExtra("udesc").toString());
        String file_url = getIntent().getStringExtra(("updf").toString());

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //to go back to the store
                startActivity(new Intent(getApplicationContext(),VisitStoreActivity.class));
                finish();
            }
        });
        viewpdf.setOnClickListener(new View.OnClickListener() { //when pdf icon is clicked
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setType("application/pdf");
                intent.setData(Uri.parse(file_url)); //for retrieving the URL from Firestore to download the pdf
                startActivity(intent);

            }
        });
    }
}