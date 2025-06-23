package com.uddco.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uddco.service.DashboardService;
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private DashboardService dashboardService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> handleChatQuery(@RequestBody Map<String, String> request) {
        String query = request.get("query").toLowerCase();
        Map<String, Object> response = new HashMap<>();

        try {
            if (query.contains("today") && (query.contains("order") || query.contains("count"))) {
                int orders = dashboardService.getTodayOrderCount();
                response.put("text", "There have been " + orders + " orders today.");
            }
            else if (query.contains("today") && query.contains("revenue")) {
                double revenue = dashboardService.getTodayRevenue();
                response.put("text", String.format("Today's revenue is RM%.2f", revenue));
            }
            else if (query.contains("top") && query.contains("product")) {
                List<Map<String, Object>> topProducts = dashboardService.getTopProducts(5, "month");
                StringBuilder sb = new StringBuilder("Top selling products this month:\n");
                for (Map<String, Object> product : topProducts) {
                    sb.append(String.format("- %s: %.0f sold\n", 
                        product.get("productName"), product.get("totalSold")));
                }
                response.put("text", sb.toString());
            }
            else if (query.contains("compare") && query.contains("sales")) {
                // Implement comparison logic
                Map<String, Double> comparison = dashboardService.compareSalesPeriods("lastMonth", "thisMonth");
                List<Map<String, Object>> chartData = new ArrayList<>();
                chartData.add(Map.of("name", "Last Month", "value", comparison.get("lastMonth")));
                chartData.add(Map.of("name", "This Month", "value", comparison.get("thisMonth")));
                
                response.put("text", String.format(
                    "Sales comparison:\nLast month: RM%.2f\nThis month: RM%.2f\nDifference: RM%.2f (%.1f%%)",
                    comparison.get("lastMonth"), comparison.get("thisMonth"),
                    comparison.get("difference"), comparison.get("percentageChange")
                ));
                response.put("chartData", chartData);
            }
            else {
                response.put("text", "I can help with sales data. Ask about orders, revenue, or products.");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("text", "Sorry, I encountered an error processing your request.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}