package com.example.notice_firestore.Notices;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notice_firestore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ShowImagesActivity extends AppCompatActivity {
    //recyclerview object
    private RecyclerView recyclerView;

    //adapter object
    private RecyclerView.Adapter adapter;

    //database reference
    private DatabaseReference mDatabase;

    //progress dialog
//    private ProgressDialog progressDialog;

    //list to hold all the uploaded images
    private List<Upload> uploads;

    FloatingActionButton floatingActionButtonAddNotice;

    public static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices_show_images);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        floatingActionButtonAddNotice = (FloatingActionButton)findViewById(R.id.add_notice);


//        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();

        uploads = new ArrayList<>();


        floatingActionButtonAddNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ShowImagesActivity.this,NoticesMainActivity.class);
                startActivity(i);
            }
        });


        //displaying progress dialog while fetching images
//        progressDialog.setMessage("Please wait...");
//        progressDialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

        //adding an event listener to fetch values
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //dismissing the progress dialog
//                progressDialog.dismiss();
                uploads.clear();

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    uploads.add(upload);
                }
                //creating adapter
                if (adapter != null) // it works second time and later
                    adapter.notifyDataSetChanged();
                else { // it works first time
                    adapter = new MyNoticeAdapter(getApplicationContext(), uploads);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                progressDialog.dismiss();
            }
        });

    }
}