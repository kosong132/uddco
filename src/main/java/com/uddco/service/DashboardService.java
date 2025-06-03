package com.uddco.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class DashboardService {

    private final Firestore db = FirestoreClient.getFirestore();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public int getTodayOrderCount() {
        try {
            LocalDate today = LocalDate.now();
            String todayStr = today.format(formatter);

            ApiFuture<QuerySnapshot> future = db.collection("orders")
                    .whereGreaterThanOrEqualTo("createdAt", todayStr + "T00:00:00")
                    .whereLessThanOrEqualTo("createdAt", todayStr + "T23:59:59")
                    .get();

            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            return docs.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double getTodayRevenue() {
        try {
            LocalDate today = LocalDate.now();
            String todayStr = today.format(formatter);

            ApiFuture<QuerySnapshot> future = db.collection("orders")
                    .whereGreaterThanOrEqualTo("createdAt", todayStr + "T00:00:00")
                    .whereLessThanOrEqualTo("createdAt", todayStr + "T23:59:59")
                    .get();

            double total = 0;
            for (DocumentSnapshot doc : future.get().getDocuments()) {
                Double price = doc.getDouble("totalPrice");
                if (price != null) total += price;
            }
            return total;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public int getTotalRegisteredUsers() {
        try {
            ApiFuture<QuerySnapshot> future = db.collection("users").get();
            return future.get().getDocuments().size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<Map<String, Object>> getSalesData(int year, int month) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            List<QueryDocumentSnapshot> docs = db.collection("orders").get().get().getDocuments();

            Map<String, Double> salesMap = new LinkedHashMap<>();

            for (DocumentSnapshot doc : docs) {
                String createdAt = doc.getString("createdAt");
                Double totalPrice = doc.getDouble("totalPrice");

                if (createdAt == null || totalPrice == null) continue;

                LocalDate date = LocalDate.parse(createdAt.substring(0, 10), formatter);
                if (date.getYear() != year) continue;

                if (month == 0) {
                    // Monthly revenue
                    String monthName = date.getMonth().name(); // e.g., "JANUARY"
                    salesMap.put(monthName, salesMap.getOrDefault(monthName, 0.0) + totalPrice);
                } else if (date.getMonthValue() == month) {
                    // Weekly revenue for selected month
                    int weekNum = (date.getDayOfMonth() - 1) / 7 + 1;
                    String weekLabel = "Week " + weekNum;
                    salesMap.put(weekLabel, salesMap.getOrDefault(weekLabel, 0.0) + totalPrice);
                }
            }

            for (Map.Entry<String, Double> entry : salesMap.entrySet()) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", entry.getKey());
                map.put("revenue", entry.getValue());
                result.add(map);
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }
}
