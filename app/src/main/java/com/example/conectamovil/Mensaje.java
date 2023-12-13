package com.example.conectamovil;

public class Mensaje {
        private String remitente;
        private String contenido;

        public Mensaje(String remitente, String contenido) {
            this.remitente = remitente;
            this.contenido = contenido;
        }

        public String getRemitente() {
            return remitente;
        }

        public void setRemitente(String remitente) {
            this.remitente = remitente;
        }

        public String getContenido() {
            return contenido;
        }

        public void setContenido(String contenido) {
            this.contenido = contenido;
        }
}
