package com.example.conectamovil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText txtUserR, txtNameR, txtApellidoR, txtEmailR, txtPassR, txtPassRC, txtAgreeR;
    Button buttonSignR;
    FirebaseDatabase baseDatos;
    DatabaseReference referencia;
    private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Auth = FirebaseAuth.getInstance();

        txtUserR = findViewById(R.id.txtUserR);
        txtNameR = findViewById(R.id.txtNameR);
        txtApellidoR = findViewById(R.id.txtApellidoR);
        txtEmailR = findViewById(R.id.txtEmailR);
        txtPassR = findViewById(R.id.txtPassR);
        txtPassRC = findViewById(R.id.txtPassRC);
        buttonSignR = findViewById(R.id.buttonSignR);

        buttonSignR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseDatos = FirebaseDatabase.getInstance();
                referencia = baseDatos.getReference("users");

                String user = txtUserR.getText().toString();
                String name = txtNameR.getText().toString();
                String apellido = txtApellidoR.getText().toString();
                String email = txtEmailR.getText().toString();
                String pass = txtPassR.getText().toString();
                String passrc = txtPassRC.getText().toString();
                String agree = txtAgreeR.getText().toString();

                if (user.equals("") || name.equals("") || apellido.equals("") || email.equals("") || pass.equals("") || passrc.equals("")) {
                    Toast.makeText(Register.this, "Todos los campos deben ser rellenados", Toast.LENGTH_SHORT).show();
                } else if (pass.length() >= 6) {
                    if (pass.equals(agree)) {

                        Auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Users users = new Users(user, name, apellido, email, pass);
                                    referencia.child(Auth.getCurrentUser().getUid()).setValue(users);

                                    Toast.makeText(Register.this, "Creo su cuenta con exito.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Register.this, Login.class);
                                    startActivity(intent);
                                }
                            }
                        });
                    } else {
                        Toast.makeText(Register.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Register.this, "La contraseña no puede tener menos de 6 caracteres", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}