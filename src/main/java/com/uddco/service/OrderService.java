package com.uddco.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import com.uddco.model.Order;

@Service
public class OrderService {

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_NAME = "orders";

    /**
     * Uploads an image to Firebase Storage and returns the public media link.
     */
    public String uploadImageToFirebase(MultipartFile file) throws IOException {
        // Generate a unique filename using UUID and the original file's name
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        // Create a temporary file
        File tempFile = File.createTempFile("temp", file.getOriginalFilename());

        // Transfer the uploaded file data into the temp file
        file.transferTo(tempFile);

        // Upload the image to Firebase Storage under the "orders" directory
        StorageClient.getInstance()
                .bucket()
                .create("orders/" + fileName, Files.readAllBytes(tempFile.toPath()), file.getContentType());
        // Get the bucket name
        String bucketName = StorageClient.getInstance().bucket().getName();
        // Return the public URL of the uploaded image
        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucketName,
                ("orders/" + fileName).replace("/", "%2F")
        );
    }

    /**
     * Places a new order into Firestore.
     */
    public String placeOrder(Order order) throws ExecutionException, InterruptedException {
        // Set default order status
        order.setOrderStatus("Preparing");
        order.setTotalPrice(order.getQuantity() * order.getPricePerUnit());
// 
        // Firestore instance
        Firestore db = FirestoreClient.getFirestore();

        // Create a new document in Firestore and set the order
        DocumentReference docRef = db.collection(COLLECTION_NAME).document();

        // Set a unique order ID (can be any unique identifier)
        order.setOrderId(UUID.randomUUID().toString());

        // Save the order document in Firestore
        WriteResult result = docRef.set(order).get();

        return "Order placed at: " + result.getUpdateTime();
    }

    public List<Order> getOrdersByUserId(String userId) throws ExecutionException, InterruptedException {
        CollectionReference orders = firestore.collection("orders");
        ApiFuture<QuerySnapshot> query = orders.whereEqualTo("userId", userId).get();
        List<QueryDocumentSnapshot> documents = query.get().getDocuments();

        List<Order> orderList = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            orderList.add(doc.toObject(Order.class));
        }
        return orderList;
    }

    /**
     * Retrieves all orders from Firestore.
     */
    public List<Order> getAllOrders() throws ExecutionException, InterruptedException {
        CollectionReference ordersRef = firestore.collection(COLLECTION_NAME);
        List<Order> orders = new ArrayList<>();
        ordersRef.get().get().forEach(doc -> orders.add(doc.toObject(Order.class)));
        return orders;
    }


    public String deleteOrder(String orderId) throws ExecutionException, InterruptedException {
        CollectionReference ordersRef = firestore.collection(COLLECTION_NAME);
        for (var doc : ordersRef.get().get().getDocuments()) {
            Order order = doc.toObject(Order.class);
            if (order.getOrderId().equals(orderId)) {
                try {
                    doc.getReference().delete().get();
                    return "Order deleted successfully.";
                } catch (Exception e) {
                    return "Failed to delete order: " + e.getMessage();
                }
            }
        }
        return "Order not found.";
    }

    public String updateOrderStatus(String orderId, String status) throws ExecutionException, InterruptedException {
        CollectionReference ordersRef = firestore.collection(COLLECTION_NAME);
        Query query = ordersRef.whereEqualTo("orderId", orderId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        if (!documents.isEmpty()) {
            DocumentReference docRef = documents.get(0).getReference();
            docRef.update("orderStatus", status).get();
            return "Order status updated to: " + status;
        } else {
            return "Order with ID " + orderId + " not found.";
        }
    }

}
    /**
     * Updates the status of an existing order.
     */
    // public String updateOrderStatus(String orderId, String status) throws ExecutionException, InterruptedException {
    //     CollectionReference ordersRef = firestore.collection(COLLECTION_NAME);
    //     for (var doc : ordersRef.get().get().getDocuments()) {
    //         Order order = doc.toObject(Order.class);
    //         if (order.getOrderId().equals(orderId)) {
    //             try {
    //                 doc.getReference().update("orderStatus", status).get();
    //                 return "Order status updated to: " + status;
    //             } catch (Exception e) {
    //                 return "Failed to update status: " + e.getMessage();
    //             }
    //         }
    //     }
    //     return "Order not found";
    // }