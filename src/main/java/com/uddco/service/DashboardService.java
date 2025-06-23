package com.uddco.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
    public List<Map<String, Object>> getTopProducts(int limit, String period) {
    try {
        // Group by product and sum quantities
        List<QueryDocumentSnapshot> orders = db.collection("orders").get().get().getDocuments();
        
        Map<String, Double> productSales = new HashMap<>();
        Map<String, String> productNames = new HashMap<>();
        
        for (DocumentSnapshot order : orders) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) order.get("items");
            if (items != null) {
                for (Map<String, Object> item : items) {
                    String productId = (String) item.get("productId");
                    String productName = (String) item.get("productName");
                    Double quantity = (Double) item.get("quantity");
                    
                    productSales.merge(productId, quantity, Double::sum);
                    productNames.putIfAbsent(productId, productName);
                }
            }
        }
        
        return productSales.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> {
                Map<String, Object> result = new HashMap<>();
                result.put("productId", entry.getKey());
                result.put("productName", productNames.get(entry.getKey()));
                result.put("totalSold", entry.getValue());
                return result;
            })
            .collect(Collectors.toList());
            
    } catch (Exception e) {
        e.printStackTrace();
        return Collections.emptyList();
    }
}

public Map<String, Long> getOrderStatusDistribution() {
    try {
        List<QueryDocumentSnapshot> orders = db.collection("orders").get().get().getDocuments();
        
        return orders.stream()
            .map(doc -> doc.getString("status"))
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(
                status -> status,
                Collectors.counting()
            ));
    } catch (Exception e) {
        e.printStackTrace();
        return Collections.emptyMap();
    }
}

public Map<String, Object> getCustomerStatistics() {
    try {
        Map<String, Object> stats = new HashMap<>();
        
        // Total customers
        stats.put("totalCustomers", db.collection("users").get().get().size());
        
        // New customers this month
        LocalDate firstOfMonth = LocalDate.now().withDayOfMonth(1);
        String monthStart = firstOfMonth.format(formatter);
        
        ApiFuture<QuerySnapshot> newCustomers = db.collection("users")
            .whereGreaterThanOrEqualTo("createdAt", monthStart + "T00:00:00")
            .get();
            
        stats.put("newThisMonth", newCustomers.get().size());
        
        return stats;
    } catch (Exception e) {
        e.printStackTrace();
        return Collections.emptyMap();
    }
}

public Map<String, Double> compareSalesPeriods(String period1, String period2) {
    // Implement comparison logic (week vs week, month vs month, etc.)
    // This is a simplified version - you'll need to adjust based on your date formats
    Map<String, Double> comparison = new HashMap<>();
    
    try {
        double sales1 = getSalesForPeriod(period1);
        double sales2 = getSalesForPeriod(period2);
        
        comparison.put(period1, sales1);
        comparison.put(period2, sales2);
        comparison.put("difference", sales1 - sales2);
        comparison.put("percentageChange", (sales1 - sales2) / sales2 * 100);
        
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    return comparison;
}

private double getSalesForPeriod(String period) throws Exception {
    // Implement logic to get sales for a specific period
    // This is a placeholder - implement based on your date formats
    if (period.equals("lastMonth")) {
        LocalDate now = LocalDate.now();
        LocalDate lastMonth = now.minusMonths(1);
        return getMonthlySales(lastMonth.getMonthValue(), lastMonth.getYear());
    }
    // Add other period cases
    return 0;
}

private double getMonthlySales(int month, int year) throws Exception {
    List<Map<String, Object>> monthlyData = getSalesData(year, month);
    return monthlyData.stream()
        .mapToDouble(item -> (Double) item.get("revenue"))
        .sum();
}
}
