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

    public Map<String, Object> processQuery(String query) {
        String q = query.toLowerCase();
        Map<String, Object> response = new HashMap<>();

        if (q.contains("order") && q.contains("today")) {
            int count = dashboardService.getTodayOrderCount();
            response.put("text", "There have been " + count + " orders today.");
        } 
        else if (q.contains("revenue") && q.contains("today")) {
            double revenue = dashboardService.getTodayRevenue();
            response.put("text", String.format("Today's revenue is RM%.2f", revenue));
        } 
        else if (q.contains("top") && q.contains("product")) {
            List<Map<String, Object>> topProducts = dashboardService.getTopProducts(5, "month");
            StringBuilder sb = new StringBuilder("Top selling products this month:\n");
            for (Map<String, Object> product : topProducts) {
                sb.append(String.format("- %s: %.0f sold\n", 
                    product.get("productName"), product.get("totalSold")));
            }
            response.put("text", sb.toString());
        } 
        else if (q.contains("compare") && q.contains("sales")) {
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
        } 
        else {
            response.put("text", "I can help with sales data. Ask about orders, revenue, or products.");
        }

        return response;
    }
}
