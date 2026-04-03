package com.example.wasla.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials_path}")
    private String credentialPath;

    @Value("${firebase.credentials_json}")
    private String credentialJson;

    @Value("${firebase.project_id}")
    private String projectId;


    @PostConstruct
    public void initialize() {

        if (!FirebaseApp.getApps().isEmpty()) {
            log.info("firebase already initialized ");
            return;
        }

        try {
            GoogleCredentials credentials =  loadCredentials();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setProjectId(projectId)
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("firebase initialize successfully");
        } catch (IOException e)
        {
            throw new IllegalArgumentException("filed to initialize firebase");
        }
    }





    // load credentials as a json string in an enviroment variable
    private GoogleCredentials loadCredentials() throws IOException {


        // method 1: as a json content via environment variable
        if (StringUtils.hasText(credentialJson)) {
            log.info("loading firebase credentials form environment variable");
            InputStream stream = new ByteArrayInputStream(
                    credentialJson.getBytes(StandardCharsets.UTF_8)
            );
            return GoogleCredentials.fromStream(stream);
        }


        // method 2: as a file path via environment variable
        if (StringUtils.hasText(credentialPath)) {
            log.info("loading Credentials path form environment variable");
            return GoogleCredentials.fromStream(new FileInputStream(credentialPath));
        }

        // if fail
        throw new IllegalArgumentException("firebase configuration not work");

    }















}
