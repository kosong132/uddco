package com.uddco.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.uddco.model.Wishlist;

@Service
public class WishlistService {

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_NAME = "wishlist";

    /**
     * Adds an item to the wishlist.
     */
    public String addToWishlist(Wishlist wishlist) throws ExecutionException, InterruptedException {
        // Set wishlistId if not already set
        if (wishlist.getWishlistId() == null || wishlist.getWishlistId().isEmpty()) {
            wishlist.setWishlistId(UUID.randomUUID().toString());
        }

        // Set createdAt if not already set
        if (wishlist.getCreatedAt() == null || wishlist.getCreatedAt().isEmpty()) {
            wishlist.setCreatedAt(new Date().toString()); // You can format it as needed
        }

        // Save to Firestore
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(wishlist.getWishlistId());
        ApiFuture<WriteResult> writeResult = docRef.set(wishlist);
        return "Wishlist item added at: " + writeResult.get().getUpdateTime();
    }

    /**
     * Retrieves all wishlist items for a user.
     */
    public List<Wishlist> getWishlistByUserId(String userId) throws ExecutionException, InterruptedException {
        CollectionReference wishlistRef = firestore.collection(COLLECTION_NAME);
        ApiFuture<QuerySnapshot> query = wishlistRef.whereEqualTo("userId", userId).get();
        List<QueryDocumentSnapshot> documents = query.get().getDocuments();

        List<Wishlist> wishlistItems = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            wishlistItems.add(doc.toObject(Wishlist.class));
        }

        return wishlistItems;
    }

    /**
     * Deletes an item from the wishlist by wishlistId.
     */
    public String deleteWishlistItem(String wishlistId) throws ExecutionException, InterruptedException {
        CollectionReference wishlistRef = firestore.collection(COLLECTION_NAME);

        // Match the document with wishlistId field (must be set when adding items)
        Query query = wishlistRef.whereEqualTo("wishlistId", wishlistId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (!documents.isEmpty()) {
            for (QueryDocumentSnapshot doc : documents) {
                doc.getReference().delete().get();
            }
            return "Wishlist item deleted successfully.";
        } else {
            return "Wishlist item not found.";
        }
    }

    /**
     * Retrieves all wishlist items (optional - for admin/debug use).
     */
    public List<Wishlist> getAllWishlistItems() throws ExecutionException, InterruptedException {
        CollectionReference wishlistRef = firestore.collection(COLLECTION_NAME);
        List<Wishlist> allItems = new ArrayList<>();
        wishlistRef.get().get().forEach(doc -> allItems.add(doc.toObject(Wishlist.class)));
        return allItems;
    }

    public Wishlist updateWishlistItem(String wishlistId, Wishlist updatedWishlist) throws ExecutionException, InterruptedException {
        CollectionReference wishlistRef = firestore.collection(COLLECTION_NAME);
        Query query = wishlistRef.whereEqualTo("wishlistId", wishlistId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) {
            throw new RuntimeException("Wishlist item not found with id: " + wishlistId);
        }

        DocumentReference docRef = documents.get(0).getReference();

        // Optional: Retrieve the existing wishlist to selectively update fields if needed
        Wishlist existingWishlist = documents.get(0).toObject(Wishlist.class);

        // Update the fields of existingWishlist with values from updatedWishlist
        existingWishlist.setProductId(updatedWishlist.getProductId());
        existingWishlist.setProductName(updatedWishlist.getProductName());
        existingWishlist.setQuantity(updatedWishlist.getQuantity());
        existingWishlist.setSelectedColor(updatedWishlist.getSelectedColor());
        existingWishlist.setSelectedSize(updatedWishlist.getSelectedSize());
        existingWishlist.setSelectedCustomization(updatedWishlist.getSelectedCustomization());
        existingWishlist.setLogoPosition(updatedWishlist.getLogoPosition());
        existingWishlist.setPricePerUnit(updatedWishlist.getPricePerUnit());
        existingWishlist.setTotalPrice(updatedWishlist.getTotalPrice());
        existingWishlist.setImageUrl(updatedWishlist.getImageUrl());
        existingWishlist.setStatus(updatedWishlist.getStatus());

        // Optionally update timestamp or other fields if needed
        existingWishlist.setCreatedAt(new Date().toString());  // add updatedAt in your model if applicable

        // Save the updated wishlist back to Firestore
        ApiFuture<WriteResult> writeResult = docRef.set(existingWishlist);
        writeResult.get(); // wait for write to complete

        return existingWishlist;
    }

}
