package com.example.pickyourlead;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

//firebase
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    static String branch;
    static String originalBranch;
    static String originalBatch;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static String batch;
    private ProgressBar spinner;
    FirebaseUser user;
    String email=" ";
    boolean flag=false;


    public void options_page(View view) throws InterruptedException {
        spinner.setVisibility(View.VISIBLE);
        if (flag) {
            user.reload();
            TimeUnit.SECONDS.sleep(1);
            if (user.isEmailVerified()) {
                storefire(email);
            }
            else{
                spinner.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            boolean internet = isConnected();
            if (internet) {
                re();

            } else {
                spinner.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Please check your Internet connection", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Register.this, LostConnection.class));
            }
        }
    }


    public void re() {
        EditText mail,pass;
        mail=findViewById(R.id.editTextTextEmailAddress2);
        pass=findViewById(R.id.editTextTextPassword2);
        String password=pass.getText().toString();
        email=mail.getText().toString();
        spinner.setVisibility(View.INVISIBLE);
        if  (email.isEmpty()) {
            spinner.setVisibility(View.INVISIBLE);
            mail.setError("Email is empty");
            mail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            spinner.setVisibility(View.INVISIBLE);
            mail.setError("Enter a valid email id");
            mail.requestFocus();
            return;
        }
        if(!email.contains("@snu.edu.in")){
            mail.setError("Enter your organization's email id");
            mail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            spinner.setVisibility(View.INVISIBLE);
            pass.setError("Password is empty");
            pass.requestFocus();
            return;
        }
        if (password.length()<6) {
            spinner.setVisibility(View.INVISIBLE);
            pass.setError("Length of password should be more than 6");
            pass.requestFocus();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user=mAuth.getCurrentUser();
                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    flag=true;
                                    Toast.makeText(Register.this, "Verification email has been sent", Toast.LENGTH_LONG).show();
                                    return ;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Register.this, "Please try again", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(Register.this, "Sorry,but you are already registered", Toast.LENGTH_SHORT).show();
                            spinner.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }


    public void storefire(String email)
    {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("branch",branch);
        user.put("batch", batch);
        user.put("CLASS REPRESENTATIVEflag",0);
        user.put("PRESIDENTflag",0);
        user.put("VICE PRESIDENTflag",0);
        user.put("CULTURAL SECRETARYflag",0);
        user.put("SPORTS SECRETARYflag",0);
        user.put("SECRETARY OF ACADEMIC AFFAIRSflag",0);
        user.put("SECRETARY OF EXTERNAL AFFAIRSflag",0);
        user.put("SECRETARY OF SENATEflag",0);

        user.put("CLASS REPRESENTATIVEcand",0);
        user.put("COUNCILcand", 0);

        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(user);
        Intent next = new Intent(this, Options.class);
        startActivity(next);
        spinner.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        Spinner mySpinner = (Spinner) findViewById(R.id.spinner2);
        Spinner mySpinner2 = (Spinner) findViewById(R.id.spinner5);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Register.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.branches));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(Register.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.batch));
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner2.setAdapter(myAdapter2);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?>arg0, View view, int arg2, long arg3) {
                branch = mySpinner.getSelectedItem().toString();
                originalBranch=branch;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(Register.this, "Please select your branch", Toast.LENGTH_SHORT).show();
            }
        });

        mySpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?>arg0, View view, int arg2, long arg3) {
                batch = mySpinner2.getSelectedItem().toString();
                originalBatch=batch;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(Register.this, "Please select your batch", Toast.LENGTH_SHORT).show();
            }
        });
    }


    boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if(networkInfo.isConnected())
                return true;
            else
                return false;
        } else
            return false;
    }

}