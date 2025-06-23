package com.uddco.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // 🔐 Read from environment variable
        String base64Key = System.getenv("FIREBASE_KEY_BASE64");

        if (base64Key == null || base64Key.isEmpty()) {
            throw new IllegalStateException("FIREBASE_KEY_BASE64 environment variable is not set.");
        }

        // 📥 Decode the Base64 string into JSON
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        ByteArrayInputStream serviceAccount = new ByteArrayInputStream(decodedKey);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("uddco-f3018.firebasestorage.app") // ✅ Correct Firebase storage bucket
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public Firestore firestore() throws IOException {
        return FirestoreClient.getFirestore(firebaseApp());
    }
}


