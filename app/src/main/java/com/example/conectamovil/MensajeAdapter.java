package com.example.conectamovil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.conectamovil.Mensaje;

import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.ViewHolder> {
    private List<Mensaje> mensajes;

    public MensajeAdapter(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);
        holder.remitenteTextView.setText(mensaje.getRemitente());
        holder.contenidoTextView.setText(mensaje.getContenido());
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView remitenteTextView;
        public TextView contenidoTextView;

        public ViewHolder(View view) {
            super(view);
            remitenteTextView = view.findViewById(R.id.txtSender);
            contenidoTextView = view.findViewById(R.id.textMessage);
        }
    }
}
