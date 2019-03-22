package com.example.posted;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordConfirm;
    private TextView textViewSignIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private Button bypass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        buttonRegister =  findViewById(R.id.ButtonRegister);
        editTextEmail =  findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordConfirm = findViewById(R.id.editTextRetypePassword);
        textViewSignIn =    findViewById(R.id.textViewSignIn);
        bypass = findViewById(R.id.ButtonBypass);
        bypass.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(this, MainMenu.class));
        }
    }

    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        String passwordConfirm = editTextPasswordConfirm.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(passwordConfirm)){
            Toast.makeText(this, "Please confirm password.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(passwordConfirm)){
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Registering User");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(Register.this, "Registration Success", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        FirebaseAuthException e = (FirebaseAuthException )task.getException();
                       Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            });
    }

    @Override
    public void onClick(View view){
        if(view==buttonRegister){
            registerUser();
        }

        if(view==textViewSignIn){
            startActivity(new Intent(this, Login.class));
        }
    }
}
