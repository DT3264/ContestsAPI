package com.cernadaniel.contestsapi.contests_api;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        initFirebase();
        SpringApplication.run(DemoApplication.class, args);
    }

    private static void initFirebase() {
        FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(getCredentials()).build();
        try {
            FirebaseApp.initializeApp(options);
        } catch (IllegalStateException e) {
            // Already Init
        } finally {
            System.out.println("Firebase initialized");
        }
    }

    private static ServiceAccountCredentials getCredentials() {
        String credentials = System.getenv("GOOGLE_CREDENTIALS");
        try {
            return ServiceAccountCredentials.fromStream(new ByteArrayInputStream(credentials.getBytes()));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
