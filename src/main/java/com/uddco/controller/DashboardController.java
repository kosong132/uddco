package com.uddco.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uddco.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/today-orders")
    public int getTodayOrders() {
        return dashboardService.getTodayOrderCount();
    }

    @GetMapping("/today-revenue")
    public double getTodayRevenue() {
        return dashboardService.getTodayRevenue();
    }

    @GetMapping("/total-users")
    public int getTotalRegisteredUsers() {
        return dashboardService.getTotalRegisteredUsers();
    }

    @GetMapping("/sales")
    public List<Map<String, Object>> getSalesData(
            @RequestParam int year,
            @RequestParam int month) {
        return dashboardService.getSalesData(year, month);
    }
    // Add these new endpoints to your existing DashboardController

    @GetMapping("/top-products")
    public List<Map<String, Object>> getTopProducts(
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "month") String period) {
        return dashboardService.getTopProducts(limit, period);
    }

    @GetMapping("/order-status")
    public Map<String, Long> getOrderStatusStats() {
        return dashboardService.getOrderStatusDistribution();
    }

    @GetMapping("/customer-stats")
    public Map<String, Object> getCustomerStatistics() {
        return dashboardService.getCustomerStatistics();
    }

    @GetMapping("/sales-comparison")
    public Map<String, Double> compareSalesPeriods(
            @RequestParam String period1,
            @RequestParam String period2) {
        return dashboardService.compareSalesPeriods(period1, period2);
    }
}
