package com.cernadaniel.contestsapi.contests_api.Utils;

import java.util.TreeMap;
import java.util.Map.Entry;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

public class NotificationsManager {

    public void notificateNewContests(TreeMap<String, Integer> newContest) {
        for (Entry<String, Integer> p : newContest.entrySet()) {
            sendNotification(p.getKey(), p.getValue());
        }
    }

    private void sendNotification(String platform, int newContests) {
        try {
            String title = "New " + platform + " contest" + (newContests > 1 ? "s" : "");
            String body = "There" + (newContests > 1 ? " are " : " is ") + newContests + " new contest" + (newContests > 1 ? "s" : "");
            Notification notification = Notification.builder().setTitle(title).setBody(body).build();
            Message message = Message.builder().setNotification(notification).setTopic(platform).build();
            FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + title + " - " + body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}