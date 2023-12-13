package com.example.conectamovil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private FirebaseAuth Auth;
    EditText txtEmail, txtPass;
    Button buttonLogin, buttonSign, buttonListado, buttonChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Auth = FirebaseAuth.getInstance();

        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSign = findViewById(R.id.buttonSign);
        buttonListado = findViewById(R.id.buttonListado);
        buttonChat = findViewById(R.id.buttonChat);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString();
                String pass = txtPass.getText().toString();

                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if(!pass.isEmpty()) {
                        Auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(Login.this, "Bienvenido.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(Login.this, Login.class));
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(Login.this, "Coloque su contraseña.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        buttonSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        buttonListado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Listado.class);
                startActivity(intent);
            }
        });

        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Chat.class);
                startActivity(intent);
            }
        });
    }
}