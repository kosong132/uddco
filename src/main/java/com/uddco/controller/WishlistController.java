package com.uddco.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uddco.model.Wishlist;
import com.uddco.service.WishlistService;

@RestController
@RequestMapping("/wishlist")
@CrossOrigin(origins = "http://localhost:3000") // Allow CORS for frontend (adjust origin as needed)
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    /**
     * Add a new item to the wishlist.
     */
    @PostMapping("/add-wishlist")
public ResponseEntity<String> addWishlist(@RequestBody Wishlist wishlist) {
    try {
        // Auto-generate wishlistId if not provided
        if (wishlist.getWishlistId() == null || wishlist.getWishlistId().isEmpty()) {
            wishlist.setWishlistId(java.util.UUID.randomUUID().toString());
        }

        // Optional: You can also set createdAt and status here if not handled in model or service
        if (wishlist.getCreatedAt() == null || wishlist.getCreatedAt().isEmpty()) {
            wishlist.setCreatedAt(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_DATE_TIME));
        }
        if (wishlist.getStatus() == null || wishlist.getStatus().isEmpty()) {
            wishlist.setStatus("active");
        }

        String result = wishlistService.addToWishlist(wishlist);
        return ResponseEntity.ok(result);
    } catch (ExecutionException | InterruptedException e) {
        return ResponseEntity.status(500).body("Failed to add to wishlist: " + e.getMessage());
    }
}

    /**
     * Get all wishlist items for a specific user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Wishlist>> getWishlistByUserId(@PathVariable String userId) {
        try {
            List<Wishlist> items = wishlistService.getWishlistByUserId(userId);
            return ResponseEntity.ok(items);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Delete a wishlist item by its ID.
     */
@DeleteMapping("/{wishlistId}")
public ResponseEntity<String> deleteWishlistItem(@PathVariable String wishlistId) {
    try {
        String result = wishlistService.deleteWishlistItem(wishlistId);
        return ResponseEntity.ok(result);
    } catch (ExecutionException | InterruptedException e) {
        return ResponseEntity.status(500).body("Failed to delete wishlist item: " + e.getMessage());
    }
}


    /**
     * (Optional) Get all wishlist items - for admin or debug.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Wishlist>> getAllWishlistItems() {
        try {
            List<Wishlist> items = wishlistService.getAllWishlistItems();
            return ResponseEntity.ok(items);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).build();
        }
    }
 @PutMapping("/update/{wishlistId}")
    public ResponseEntity<?> updateWishlistItem(
            @PathVariable String wishlistId,
            @RequestBody Wishlist updatedWishlist) {
        try {
            Wishlist updatedItem = wishlistService.updateWishlistItem(wishlistId, updatedWishlist);
            return ResponseEntity.ok(updatedItem);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error updating wishlist item: " + e.getMessage());
        } catch (RuntimeException e) {
            // For example, wishlist item not found
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}