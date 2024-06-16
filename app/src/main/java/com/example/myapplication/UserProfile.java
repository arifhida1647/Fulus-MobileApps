package com.example.myapplication;

public class UserProfile {
    private String country;
    private String noTlp;
    private String userName;
    private String nama;
    private String email;

    // Buat constructor
    public UserProfile(String country, String noTlp, String userName, String nama, String email) {
        this.country = country;
        this.noTlp = noTlp;
        this.userName = userName;
        this.nama = nama;
        this.email = email;
    }

    // Buat getter untuk setiap atribut
    public String getCountry() {
        return country;
    }

    public String getNoTlp() {
        return noTlp;
    }

    public String getUserName() {
        return userName;
    }

    public String getNama() {
        return nama;
    }

    public String getEmail() {
        return email;
    }
}
