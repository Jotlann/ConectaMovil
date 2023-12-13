package com.example.conectamovil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.conectamovil.MensajeAdapter;
import com.example.conectamovil.Mensaje;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity {
    private static final String URL_BROKER = "tcp://test.mosquitto.org:1883";
    private static final String ID_CLIENTE = "Android";
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private MqttAdapter mqttAdapter;
    private Button enviarBoton;
    private EditText mensajeText;
    private List<Mensaje> mensajesList = new ArrayList<>();
    private MensajeAdapter mensajeAdapter;
    private RecyclerView recyclerView;
    private boolean enviandoMensajeLocal = false;
    private final Object lock = new Object();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mqttAdapter = new MqttAdapter();
        mqttAdapter.connect(URL_BROKER, ID_CLIENTE);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        enviarBoton = findViewById(R.id.buttonSend);
        mensajeText = findViewById(R.id.editMessage);
        recyclerView = findViewById(R.id.viewChat);
        mensajeAdapter = new MensajeAdapter(mensajesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mensajeAdapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String nombre = bundle.getString("nombre");
            String topico = nombre;
            subscribeTopic(topico);
        }

        mqttAdapter.setMessageCallback(new MqttAdapter.MqttMessageCallback() {
            @Override
            public void onMessageReceived(String topic, String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (lock) {
                            Log.d("ChatActivity", "Mensaje recibido - Tópico: " + topic + ", Contenido: " + message);

                            if (!enviandoMensajeLocal) {
                                Log.d("ChatActivity", "Procesando como mensaje MQTT");
                                onNuevoMensaje(topic, message, true);
                                mensajeAdapter.notifyDataSetChanged();
                            } else {
                                Log.d("ChatActivity", "Mensaje ignorado como mensaje local");
                                // Restablecer la bandera después de procesar el mensaje local
                                enviandoMensajeLocal = false;
                            }
                        }
                    }
                });
            }
        });



        enviarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = mensajeText.getText().toString();
                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    String nombre = bundle.getString("nombre");
                    if (!mensaje.isEmpty()) {
                        String topico = nombre;
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String usuario = snapshot.child("usuario").getValue(String.class);
                                    onNuevoMensaje(usuario, mensaje, false);
                                    enviandoMensajeLocal = true;
                                    publicMessage(topico, mensaje);
                                    enviandoMensajeLocal = false;
                                    mensajeText.setText("");
                                } else {
                                    Toast.makeText(Chat.this, "No se encontraron datos de usuario", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("CuentaFragment", "Error al obtener datos del usuario: " + error.getMessage());
                            }
                        });
                        mensajeText.setText("");
                    } else {
                        Toast.makeText(Chat.this, "Escriba un mensaje", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }
    @Override
    protected void onDestroy() {
        mqttAdapter.disconnect();
        super.onDestroy();
    }

    private void publicMessage(String topico, String mensaje) {
        Log.d("ChatActivity", "Publicando mensaje local: " + mensaje);

        // Marcar que estamos enviando un mensaje local
        synchronized (lock) {
            enviandoMensajeLocal = true;
        }

        mqttAdapter.publish(topico, mensaje);
    }


    private void subscribeTopic(String topic) {
        Toast.makeText(this, "Subscrito: " + topic, Toast.LENGTH_SHORT).show();
        mqttAdapter.subscribe(topic);
    }
    private void agregarMensajeAlRecyclerView(String remitente, String contenido, boolean fromMqtt) {
        Log.d("ChatActivity", "Agregando mensaje al RecyclerView - Remitente: " + remitente + ", Contenido: " + contenido);
        Mensaje nuevoMensaje = new Mensaje(remitente, contenido);

        // Use the correct sender information based on the source of the message
        mensajesList.add(nuevoMensaje);
        mensajeAdapter.notifyItemInserted(mensajesList.size() - 1);

        // Scroll hacia el último mensaje
        recyclerView.scrollToPosition(mensajesList.size() - 1);
    }
    private void onNuevoMensaje(String remitente, String contenido, boolean fromMqtt) {
        Log.d("ChatActivity", "Nuevo mensaje recibido - Remitente: " + remitente + ", Contenido: " + contenido);

        if (fromMqtt) {
            agregarMensajeAlRecyclerView(remitente, contenido, true);
        } else {
            // Manejar mensajes locales de manera diferente si es necesario
            // Por ejemplo, podrías agregar un prefijo especial al remitente o contenido
            String remitenteLocal = "Local: " + remitente;
            agregarMensajeAlRecyclerView(remitenteLocal, contenido, false);
        }
    }
}