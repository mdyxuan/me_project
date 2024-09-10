package com.example.googlecalendarapi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class CalendarQuickstart extends Activity {
    private static final String APPLICATION_NAME = "Google Calendar API Android Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Activity 的初始化代码
    }

    // 使用 GoogleSignInAccount 来获取访问凭证
    public String fetchEvents(GoogleSignInAccount account) {
        StringBuilder eventDetails = new StringBuilder();

        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            String accessToken = account.getIdToken();

            HttpRequestInitializer requestInitializer = request -> {
                request.getHeaders().setAuthorization("Bearer " + accessToken);
            };

            Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, requestInitializer)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = service.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();

            List<Event> items = events.getItems();
            if (items.isEmpty()) {
                eventDetails.append("No upcoming events found.");
            } else {
                eventDetails.append("Upcoming events:\n");
                for (Event event : items) {
                    DateTime start = event.getStart().getDateTime();
                    if (start == null) {
                        start = event.getStart().getDate();
                    }
                    eventDetails.append(event.getSummary()).append(" (").append(start).append(")\n");
                }
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            eventDetails.append("Error fetching events: ").append(e.getMessage());
        }

        return eventDetails.toString();
    }

}
