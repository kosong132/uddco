package com.uddco.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uddco.model.Order;
import com.uddco.service.OrderService;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Upload image to Firebase Storage for an order. Returns the public URL.
     */
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file selected for upload.");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Invalid file type. Only images are allowed.");
            }

            String imageUrl = orderService.uploadImageToFirebase(file);
            return ResponseEntity.ok(Map.of("url", imageUrl)); // match frontend

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Image upload failed: " + e.getMessage()));
        }
    }

    @PostMapping("/place-order")
    public ResponseEntity<?> placeOrder(@RequestBody Order order) {
        try {
            // Call the service to place the order
            String response = orderService.placeOrder(order);
            return ResponseEntity.ok(response);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to place the order: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<Order> getAllOrders() throws ExecutionException, InterruptedException {
        return orderService.getAllOrders();
    }

    @PutMapping("/update-status/{orderId}")
    public String updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) throws ExecutionException, InterruptedException {
        return orderService.updateOrderStatus(orderId, status);
    }
}
