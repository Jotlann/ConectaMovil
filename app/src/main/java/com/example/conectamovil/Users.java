package com.example.conectamovil;

public class Users {
    private String user;
    private String name;
    private String apellido;
    private String email;
    private String pass;

    public Users(String user, String name, String apellido, String email, String pass) {
        this.user = user;
        this.name = name;
        this.apellido = apellido;
        this.email = email;
        this.pass = pass;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Users() {

    }
}
