package com.example.conectamovil;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Listado extends AppCompatActivity {

    private FirebaseAuth Auth;
    private EditText editSearch;
    private Button buttonSearch;
    private TextView textViewUser, textViewName, textViewApellido, textViewEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);
    }
}