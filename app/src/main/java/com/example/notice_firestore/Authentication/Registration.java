package com.example.notice_firestore.Authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notice_firestore.Notices.ShowImagesActivity;
import com.example.notice_firestore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    private EditText editTextFirstName, editTextLastName, editTextPhone, editTextEmail, editTextAddress, editTextPassword, editTextDOB, editTextUID;
    private Button btnSignIn, btnSignUp;

    private ProgressDialog progressDialog;

    FirebaseAuth auth;
    private RadioGroup radioSexGroup;
    int selectedRadioButtonID;
    private static final String TAG = "Registration";
    String first_name, last_name, phone_number, email_id, password, full_address;

    public static final String KEY_FIRSTNAME   = "first_name";
    public static final String KEY_LASTNAME    = "last_name";
    public static final String KEY_PHONE       = "phone_number";
    public static final String KEY_EMAIL       = "email_id";
    public static final String KEY_PASSWORD    = "password";
    public static final String KEY_GENDER      = "gender";
    public static final String KEY_ADDRESS     = "full_address";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(Registration.this, ShowImagesActivity.class));
            finish();
        }

        editTextFirstName       = (EditText) findViewById(R.id.first_name);
        editTextLastName        = (EditText) findViewById(R.id.last_name);
        editTextPhone           = (EditText) findViewById(R.id.phone_number);
        editTextEmail           = (EditText) findViewById(R.id.email);
        editTextAddress         = (EditText) findViewById(R.id.address);
        btnSignIn               = (Button) findViewById(R.id.sign_in_button);
        btnSignUp               = (Button) findViewById(R.id.sign_up_button);
        editTextPassword        = (EditText) findViewById(R.id.password);
        progressDialog = new ProgressDialog(this);

        radioSexGroup           = (RadioGroup) findViewById(R.id.radioSex);





        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registration.this, Login.class));
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toast.makeText(PatientRegister.this, "button pressed", Toast.LENGTH_SHORT).show();

                first_name              = editTextFirstName.getText().toString();
                last_name               = editTextLastName.getText().toString();
                phone_number            = editTextPhone.getText().toString();
                email_id                = editTextEmail.getText().toString();
                password                = editTextPassword.getText().toString();
//                uid                     = editTextUID.getText().toString();
                full_address            = editTextAddress.getText().toString();
                selectedRadioButtonID   = radioSexGroup.getCheckedRadioButtonId();
//                dob                     = editTextDOB.getText().toString();

                RadioButton selectedRadioButton = (RadioButton) findViewById(selectedRadioButtonID);
                String selectedRadioButtonText = selectedRadioButton.getText().toString();


                if (TextUtils.isEmpty(first_name)) {
                    Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(last_name)) {
                    Toast.makeText(getApplicationContext(), "Enter last name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(phone_number)) {
                    Toast.makeText(getApplicationContext(), "Enter Phone Number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email_id)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedRadioButtonID != -1) {
                    //Toast.makeText(PatientRegister.this, selectedRadioButtonText, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Registration.this, "Nothing selected from Radio Group.", Toast.LENGTH_SHORT).show();
                }


                progressDialog.setMessage("Registering ....");
                progressDialog.show();
                //create user

                auth.createUserWithEmailAndPassword(email_id, password)
                        .addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (!task.isSuccessful()) {
                                    Toast.makeText(Registration.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    //GETTING DATA FROM RADIO BUTTON
                                    selectedRadioButtonID = radioSexGroup.getCheckedRadioButtonId();
                                    RadioButton selectedRadioButton = (RadioButton) findViewById(selectedRadioButtonID);
                                    String selectedRadioButtonText = selectedRadioButton.getText().toString();


                                    Map<String, Object> note = new HashMap<>();
                                    note.put(KEY_FIRSTNAME, first_name);
                                    note.put(KEY_LASTNAME, last_name);
                                    note.put(KEY_PHONE, phone_number);
                                    note.put(KEY_EMAIL, email_id);
                                    note.put(KEY_PASSWORD, password);
                                    note.put(KEY_ADDRESS, full_address);
                                    note.put(KEY_GENDER, selectedRadioButtonText);
                                    progressDialog.setMessage("Registering....");
                                    progressDialog.show();
                                    db.collection("users").document(auth.getUid()).set(note)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                    //Toast.makeText(PatientRegister.this, "Data saved", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Registration.this, ShowImagesActivity.class));
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //Toast.makeText(PatientRegister.this, "Error!", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, e.toString());
                                                }
                                            });
                                }
                            }
                        });
            }
        });
    }
}
