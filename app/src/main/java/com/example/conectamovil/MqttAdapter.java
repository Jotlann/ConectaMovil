package com.example.conectamovil;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttAdapter {
    private MqttClient cliente;

    public void connect(String brokerUrl, String clientId) {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            cliente = new MqttClient(brokerUrl, clientId, persistence);

            // Set the callback to your custom callback
            cliente.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    // Handle connection completion if needed
                }

                @Override
                public void connectionLost(Throwable cause) {
                    // Handle connection lost if needed
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    if (messageCallback != null) {
                        Log.d("MqttHelper", "Mensaje recibido - Tópico: " + topic + ", Contenido: " + new String(message.getPayload()));
                        messageCallback.onMessageReceived(topic, new String(message.getPayload()));
                    }
                }


                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            cliente.connect(connectOptions);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public interface MqttMessageListener {
        void onMessageReceived(String sender, String topic, String message);
    }
    private MqttMessageListener messageListener;

    public interface MqttMessageCallback {
        void onMessageReceived(String topic, String message);
    }

    private MqttMessageCallback messageCallback;

    public void setMessageCallback(MqttMessageCallback callback) {
        this.messageCallback = callback;
    }
    public void setMessageListener(MqttMessageListener listener) {
        this.messageListener = listener;
    }
    public void disconnect() {
        try {
            cliente.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            cliente.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        try {
            cliente.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
