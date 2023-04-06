package org.kothaaa;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.*;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

public class BackEnd extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();


            String responseText = null;
            try {
                responseText = detectIntent(messageText);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(update.getMessage().getChatId().toString());
            message.setText(responseText);

            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }


        }

    }

    private String detectIntent(String message) throws IOException {
        // Generate a random session ID
        String sessionId = UUID.randomUUID().toString();

        // Path to the service account key JSON file
        String keyPath = "F:\\Java Development\\KothaaBot\\Kotha\\kothabot.json";

        // Set up the credentials
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(keyPath));

        // Set up the session client
        SessionsSettings sessionsSettings = SessionsSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();
        SessionsClient sessionsClient = SessionsClient.create(sessionsSettings);

        // Set up the session path
        String projectId = "kothabot-lawe";
        SessionName session = SessionName.of(projectId, sessionId);

        // Set up the text query input
        TextInput.Builder textInput = TextInput.newBuilder().setText(message).setLanguageCode("en-US");
        QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

        // Send the text query to Dialogflow and retrieve the response
        DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);
        String responseText = response.getQueryResult().getFulfillmentText();

        // Clean up
        sessionsClient.close();

        return responseText;
    }

    @Override
    public String getBotUsername() {
        return "kothaa_bot";
    }

    @Override
    public String getBotToken() {
        return "6177120334:AAERpsArCSdIZax6yXT-hWUERVYSp23gn1g";
    }
}
