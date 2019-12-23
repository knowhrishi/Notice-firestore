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


    private static final String TAG = "NoticeMainActivity";

    //uri to store file
    private Uri filePath = null;

    MaterialSpinner year;
    String notif_year;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth;
    private DocumentReference noteRef;
    private DatabaseReference databaseReference, myRef;
    private FirebaseDatabase mFirebaseDatabase;
    String authorname,notificationText="";
    String currenttimestamp;
    LottieAnimationView lottieAnimationView;

    //firebase objects
    private StorageReference storageReference;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices_main);

        lottieAnimationView=findViewById(R.id.upload);
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        imageView = (ImageView) findViewById(R.id.imageView);
        editTextName = (EditText) findViewById(R.id.editText);
//        textViewShow = (TextView) findViewById(R.id.textViewShow);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();


        databaseReference = FirebaseDatabase.getInstance().getReference("notices");

        year = (MaterialSpinner) findViewById(R.id.spinnerClass);

        lottieAnimationView.setVisibility(View.INVISIBLE);
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);



        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();

            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notificationText = editTextName.getText().toString();
                notificationText= notificationText.replaceAll("\\*"," <b>");


                editTextName.setText(notificationText);
               /* if(!notificationText.equals("")){
                    StringBuilder myName = new StringBuilder("domanokz");
                    int FLG=0;
                    for (int i=0;i<notificationText.length();i++){
                        if(notificationText.charAt(i)=='*'){
                            FLG=1;
                            StringBuilder.setCa
                        }
                    }
                }*/

                if(filePath!=null){
                    uploadFile();
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    lottieAnimationView.playAnimation();
                }else {
                    Upload upload = new Upload(
                            auth.getUid(),
                            notificationText,
                            "empty",
                            authorname,
                            currenttimestamp,
                            notif_year);
                    String uploadID = mDatabase.push().getKey();
                    mDatabase.child(uploadID).setValue(upload);

                }

            }
        });



        //year = (MaterialSpinner) findViewById(R.id.spinnerYear);
        year.setItems("FE-1", "FE-2", "FE-3", "SE-1", "SE-2", "SE-3", "TE-1", "TE-2", "TE-3", "BE-1", "BE-2", "BE-3");
        year.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                notif_year = item;
            }
        });


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        currenttimestamp = dateFormat.format(new Date());

        noteRef = db.collection("faculties").document(auth.getUid());
        noteRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()) {
                            authorname = documentSnapshot.getString("name");
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


    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            filePath = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                imageView.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            Picasso.get().load(filePath).resize(400,400).into(imageView);
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
                                    Upload upload = new Upload(
                                            auth.getUid(),
                                            notificationText,
                                            uri.toString(),
                                            authorname,
                                            currenttimestamp,
                                            notif_year);
                                    String uploadID = mDatabase.push().getKey();
                                    mDatabase.child(uploadID).setValue(upload);


                                    //FIRESTORE UPLOAD
                                    Map<String, Object> note = new HashMap<>();
                                    note.put("id", auth.getUid());
                                    note.put("description", notificationText);
                                    note.put("image_url", uri.toString());
                                    note.put("timestamp", currenttimestamp);
                                    note.put("notice_to", notif_year);
                                    db.collection("notices").document(uploadID).set(note)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Toast.makeText(NoticesMainActivity.this, "Upload successfully to firestore", Toast.LENGTH_LONG).show();

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(NoticesMainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, e.toString());
                                        }
                                    });

                                    Toast.makeText(NoticesMainActivity.this, "Upload successfully", Toast.LENGTH_LONG).show();

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
