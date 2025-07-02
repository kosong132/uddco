package com.uddco.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatIntentService {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AuthService authService;

    public Map<String, Object> processQuery(String query) {
        String q = query.toLowerCase();
        Map<String, Object> response = new HashMap<>();

        try {
            if (q.contains("order") && q.contains("today")) {
                int count = dashboardService.getTodayOrderCount();
                response.put("text", "There have been " + count + " orders today.");

            } else if (q.contains("revenue") && q.contains("today")) {
                double revenue = dashboardService.getTodayRevenue();
                response.put("text", String.format("Today's revenue is RM%.2f", revenue));

            } else if (q.contains("top") && q.contains("product")) {
                List<Map<String, Object>> topProducts = dashboardService.getTopProducts(5, "month");
                StringBuilder sb = new StringBuilder("Top selling products this month:\n");
                for (Map<String, Object> product : topProducts) {
                    sb.append(String.format("- %s: %.0f sold\n",
                            product.get("productName"), product.get("totalSold")));
                }
                response.put("text", sb.toString());

            } else if (q.contains("compare") && q.contains("sales")) {
                Map<String, Double> comparison = dashboardService.compareSalesPeriods("lastMonth", "thisMonth");

                List<Map<String, Object>> chartData = new ArrayList<>();
                chartData.add(Map.of("name", "Last Month", "value", comparison.get("lastMonth")));
                chartData.add(Map.of("name", "This Month", "value", comparison.get("thisMonth")));

                String resultText = String.format(
                        "Sales comparison:\nLast month: RM%.2f\nThis month: RM%.2f\nDifference: RM%.2f (%.1f%%)",
                        comparison.get("lastMonth"),
                        comparison.get("thisMonth"),
                        comparison.get("difference"),
                        comparison.get("percentageChange")
                );

                response.put("text", resultText);
                response.put("chartData", chartData);

            } else if (q.contains("user") && (q.contains("info") || q.contains("detail"))) {
                String identifier = extractIdentifier(query);
                if (identifier != null) {
                    Map<String, Object> user = authService.getUserInfoByIdentifier(identifier);
                    if (user != null) {
                        response.put("text", String.format(
                                "User Information:\n- Username: %s\n- Email: %s\n- Phone: %s\n- Address: %s",
                                user.get("username"),
                                user.get("email"),
                                user.get("phoneNumber"),
                                user.get("address")
                        ));
                    } else {
                        response.put("text", "User not found.");
                    }
                } else {
                    response.put("text", "Please provide a valid username or email.");
                }

            } else if (q.contains("all") && (q.contains("user") || q.contains("customer") || q.contains("users"))) {
                List<Map<String, Object>> users = authService.getAllUsers();
                StringBuilder sb = new StringBuilder("User List:\n");
                for (Map<String, Object> user : users) {
                    sb.append(String.format("- %s (%s), Phone: %s, Role: %s\n",
                            user.get("username"),
                            user.get("email"),
                            user.get("phoneNumber"),
                            user.get("role")));
                }
                response.put("text", sb.toString());
            } else {
                response.put("text", "I can help with sales or user info. Try asking about orders, revenue, or user details.");
            }

        } catch (Exception e) {
            response.put("text", "Error processing your request.");
        }

        return response;
    }

    private String extractIdentifier(String query) {
        // Basic extraction: check for email or valid username pattern
        for (String word : query.split("\\s+")) {
            if (word.contains("@") && word.contains(".")) {
                return word; // Email

                        }if (word.matches("[a-zA-Z0-9_]{3,}")) {
                return word;         // Likely a username

                    }}
        return null;
    }
}
