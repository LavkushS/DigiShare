package com.firstapp.groupproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

public class AddBookActivity extends AppCompatActivity {

    ImageView speechButton; //on pressing converts speech to text
    private EditText speechText; //description of the book
    private static final int RECOGNIZER_RESULT = 1;
    private EditText name, author, pages, genre; //book details
    private Button add_button;
    private String book_name, book_author, book_pages, book_genre, book_desc;
    ImageView imagebrowse,uploadfile,cancelfile; //for browsing and for indicating a pdf is uploaded/removed
    Uri filepath; //storing the url form of the pdf retrieved from the Firebase Storage(to store it in firestore)

    StorageReference storageReference; // creating reference to a Google Cloud Storage object
    CollectionReference collectionReference; // used for adding documents, getting document references, and querying for documents

    private FirebaseFirestore db; //creating FirebaseFirestore object


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("Store");
        imagebrowse = findViewById(R.id.imagebrowse);
        uploadfile = findViewById(R.id.uploadfile);
        cancelfile = findViewById(R.id.cancelfile);
        uploadfile.setVisibility(View.INVISIBLE); //kept invisible until pdf is uploaded
        cancelfile.setVisibility(View.INVISIBLE);//kept invisible until pdf is uploaded

        name = findViewById(R.id.book_name);
        author = findViewById(R.id.book_author);
        pages = findViewById(R.id.book_pages);
        genre = findViewById(R.id.book_genre);
        speechButton = findViewById(R.id.mic);
        speechText = findViewById(R.id.book_desc);
        add_button = findViewById(R.id.add);

        cancelfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadfile.setVisibility(View.INVISIBLE);
                cancelfile.setVisibility(View.INVISIBLE);
                imagebrowse.setVisibility(View.VISIBLE);//kept visible when user needs to browse for pdf/pdf not uploaded
            }
        });

        imagebrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                //When permission to access storage is granted corresponding intent is created
                                Intent intent = new Intent();
                                intent.setType("application/pdf");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent,"Select the PDF file.."),101);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();//Continues with the permission request process
                            }
                        }).check();
            }
        });


        add_button.setOnClickListener(new View.OnClickListener() { //when add book button is clicked
            @Override
            public void onClick(View v) {
                //Converting the inputs to string
                book_name = name.getText().toString();
                book_author = author.getText().toString();
                book_pages = pages.getText().toString();
                book_genre = genre.getText().toString();
                book_desc = speechText.getText().toString();
                // validating the text fields if empty or not.
                if (TextUtils.isEmpty(book_name)) {
                    name.setError("Please enter Book Name");
                } else if (TextUtils.isEmpty(book_author)) {
                    author.setError("Please enter Author's Name");
                } else if (TextUtils.isEmpty(book_pages)) {
                    pages.setError("Please enter the number of pages");
                } else if (TextUtils.isEmpty(book_genre)) {
                    genre.setError("Please enter the genre of the book");
                } else if (TextUtils.isEmpty(book_desc)) {
                    speechText.setError("Please enter the book description");
                }else {
                    //Limiting the user to enter the genre as Academic/Non Academic only
                    if(book_genre.equals("Academic") || book_genre.equals("Non Academic")){
                        // calling method to add data to Firebase Firestore.
                        addDataToFirestore(book_name, book_author, book_pages, book_genre, book_desc,filepath);
                    }
                    else{
                        genre.setError("Please enter the correct genre as specified!Also,make sure that no space is present after the 'c' Academic.");
                    }

                }

            }
        });

        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start an activity that prompts the user for speech and send it through a speech recognizer.
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                //Informs the recognizer which speech model to prefer and use a language model based on free-form speech recognition.
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Converting Speech to Text");
                startActivityForResult(speechIntent, RECOGNIZER_RESULT);


            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK){

           // An ArrayList<String> of the recognition results is created when performing ACTION_RECOGNIZE_SPEECH
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            speechText.setText(matches.get(0).toString()); //Book description now consists of the text converted from speech
        }
        if(requestCode == 101 && resultCode == RESULT_OK){
            filepath = data.getData();
            uploadfile.setVisibility(View.VISIBLE); //Once pdf is uploaded pdf icon is visible
            cancelfile.setVisibility(View.VISIBLE); //Once pdf is uploaded cancel icon is visible to remove the pdf(if required)
            imagebrowse.setVisibility(View.INVISIBLE); //Set invisible to indicate a pdf has been uploaded
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private void addDataToFirestore(String book_name, String book_author, String book_pages,String book_genre,
                                    String book_desc,Uri filepath) {

       final ProgressDialog pd= new ProgressDialog(this);
        pd.setTitle("File Uploading...");
        pd.show();
        //System.currentTimeMillis is used to generate unique names of pdf to prevent error in the Firebase Storage
        StorageReference reference = storageReference.child("uploads/"+System.currentTimeMillis()+".pdf");
        reference.putFile(filepath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Asynchronously retrieves a long lived download URL with a revokable token which can be used to share the file in firestore
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                BookDetails bookdetails = new BookDetails(book_name,book_author,book_pages,book_genre,book_desc,uri.toString());
                                // add data to Firebase Firestore.
                                pd.dismiss();
                                collectionReference.add(bookdetails).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        uploadfile.setVisibility(View.INVISIBLE);
                                        cancelfile.setVisibility(View.INVISIBLE);
                                        imagebrowse.setVisibility(View.VISIBLE);
                                        name.setText("");
                                        author.setText("");
                                        pages.setText("");
                                        genre.setText("");
                                        speechText.setText("");
                                        Toast.makeText(AddBookActivity.this, "Your book has been successfully added to our database", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // this method is called when the data addition process is failed.
                                        // displaying a toast message when data addition is failed.
                                        Toast.makeText(AddBookActivity.this, "Failed to add the book \n" + e, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                        //Shows the real time percentage of the pdf uploaded
                        float percent = (100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        pd.setMessage("Uploaded :"+(int)percent+"%");

                    }
                });

    }

}
