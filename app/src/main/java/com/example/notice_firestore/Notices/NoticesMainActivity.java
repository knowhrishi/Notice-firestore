package com.example.notice_firestore.Notices;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.notice_firestore.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NoticesMainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 234;

    //view objects
    private Button buttonChoose;
    private Button buttonUpload;
    private EditText editTextName;
    private ImageView imageView;
    private NumberPicker numberPickerPriority;


    private static final String TAG = "NoticeMainActivity";

    //uri to store file
    private Uri filePath = null;

    MaterialSpinner year;
    String notif_year;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth;
    private DocumentReference noteRef;
    private DatabaseReference databaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    String authorname, notificationText = "";
    String currenttimestamp;
    LottieAnimationView lottieAnimationView;

    //firebase objects
    private StorageReference storageReference;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices_main);

        lottieAnimationView = findViewById(R.id.upload);
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        imageView = (ImageView) findViewById(R.id.imageView);
        editTextName = (EditText) findViewById(R.id.editText);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        numberPickerPriority = findViewById(R.id.number_picker_priority);
        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);

        databaseReference = FirebaseDatabase.getInstance().getReference("notices");

        year = (MaterialSpinner) findViewById(R.id.spinnerClass);

        lottieAnimationView.setVisibility(View.INVISIBLE);
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);


        //priority = numberPickerPriority.getValue();
        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();

            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                noteRef = db.collection("users").document(auth.getUid());
                noteRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    authorname = documentSnapshot.getString("first_name") + " " + documentSnapshot.getString("last_name");
                                }
                                lottieAnimationView.pauseAnimation();
                                lottieAnimationView.setVisibility(View.INVISIBLE);
                            }

                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                lottieAnimationView.pauseAnimation();
                                lottieAnimationView.setVisibility(View.INVISIBLE);
                            }
                        });

                notificationText = editTextName.getText().toString();
                notificationText= notificationText.replaceAll("\n"," <br>");


                editTextName.setText(notificationText);
                if(!notificationText.equals("")){
                    int FLG=0,length=notificationText.length();

                    String subText = null;
                    for (int i=0;i<length;i++){
                         if(notificationText.charAt(i)=='*'){
                            if(FLG==0) {
                                subText = notificationText.substring(0, i) + "<b>" + notificationText.substring(i + 1);
                                notificationText=subText;
                                length=notificationText.length();
                                FLG=1;
                            }else {
                                subText = notificationText.substring(0, i ) + "</b>" + notificationText.substring(i + 1);
                                notificationText=subText;
                                length=notificationText.length();
                                FLG=0;
                            }

                        }

                    }

                    editTextName.setText(notificationText);
                }

                if (filePath != null) {
                    uploadFile();
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    lottieAnimationView.playAnimation();
                } else {
//
                    int priority = numberPickerPriority.getValue();
                    CollectionReference notebookRef = FirebaseFirestore.getInstance()
                            .collection("notices");
                    notebookRef.add(new Note(auth.getUid(),
                            notificationText,
                            "empty",
                            authorname,
                            currenttimestamp,
                            notif_year,
                            priority));
                    Toast.makeText(NoticesMainActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    finish();

                }

            }
        });

        year.setItems("FE-1", "FE-2", "FE-3", "SE-1", "SE-2", "SE-3", "TE-1", "TE-2", "TE-3", "BE-1", "BE-2", "BE-3");
        year.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                notif_year = item;
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        currenttimestamp = dateFormat.format(new Date());
    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            Picasso.get().load(filePath).resize(400, 400).into(imageView);
        }
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        //checking if file is available
        if (filePath != null) {
            //getting the storage reference
            final StorageReference sRef = storageReference.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(filePath));


            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog


                            sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
//
                                    int priority = numberPickerPriority.getValue();
                                    CollectionReference notebookRef = FirebaseFirestore.getInstance()
                                            .collection("notices");
                                    notebookRef.add(new Note(auth.getUid(),
                                            notificationText,
                                            uri.toString(),
                                            authorname,
                                            currenttimestamp,
                                            notif_year,
                                            priority));
                                    Toast.makeText(NoticesMainActivity.this, "Added", Toast.LENGTH_SHORT).show();
                                    finish();

                                    lottieAnimationView.setVisibility(View.INVISIBLE);
                                    lottieAnimationView.pauseAnimation();
                                    startActivity(new Intent(NoticesMainActivity.this, ShowImagesActivity.class));


                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
//                            progressDialog.dismiss();
                            lottieAnimationView.setVisibility(View.INVISIBLE);
                            lottieAnimationView.pauseAnimation();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress

                        }
                    });

        } else {
            //display an error if no file is selected
        }
    }
}
