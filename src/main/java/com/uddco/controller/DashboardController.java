package com.uddco.controller;

import com.uddco.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
}
