package com.uddco.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import com.uddco.model.Product;

@Service
public class ProductService {

    private static final String COLLECTION_NAME = "products";

    public String uploadModelToFirebase(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "_" + (originalFileName != null ? originalFileName : "model");

        // Store in the 'clothingModel' folder in Firebase Storage
        String firebasePath = "clothingModel/" + fileName;

        InputStream content = file.getInputStream();

        // ✅ Fix the content type
        String contentType = file.getContentType();
        if (contentType == null || contentType.equals("application/octet-stream")) {
            if (originalFileName != null && originalFileName.toLowerCase().endsWith(".glb")) {
                contentType = "model/gltf-binary";
            } else {
                contentType = "application/octet-stream";
            }
        }

        // 🔥 Upload to Firebase
        StorageClient.getInstance()
                .bucket()
                .create(firebasePath, content, contentType);

        String bucketName = StorageClient.getInstance().bucket().getName();

        // ✅ Return public URL (properly encoded)
        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucketName,
                firebasePath.replace("/", "%2F")
        );
    }

    public String uploadImageToFirebase(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "_" + (originalFileName != null ? originalFileName : "image");

        InputStream content = file.getInputStream();

        StorageClient.getInstance()
                .bucket()
                .create(fileName, content, file.getContentType());

        String bucketName = StorageClient.getInstance().bucket().getName();

        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucketName,
                fileName.replace("/", "%2F")
        );
    }

    public String addProduct(Product product) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        if (product.getId() == null || product.getId().isEmpty()) {
            product.setId(UUID.randomUUID().toString());
        }

        db.collection(COLLECTION_NAME)
                .document(product.getId())
                .set(product)
                .get();

        return "Product added successfully!";
    }

    public List<Product> getAllProducts() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        List<QueryDocumentSnapshot> documents = db.collection(COLLECTION_NAME).get().get().getDocuments();
        List<Product> productList = new ArrayList<>();
        for (DocumentSnapshot doc : documents) {
            Product product = doc.toObject(Product.class);
            if (product != null) {
                product.setId(doc.getId()); // Set Firestore document ID
                productList.add(product);
            }
        }

        return productList;
    }

    public List<Product> getAvailableProducts() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        List<QueryDocumentSnapshot> documents = db.collection(COLLECTION_NAME).get().get().getDocuments();
        List<Product> productList = new ArrayList<>();

        for (DocumentSnapshot doc : documents) {
            Product product = doc.toObject(Product.class);
            if (product != null && product.isAvailable()) { // ✅ Only add available products
                product.setId(doc.getId());
                productList.add(product);
            }
        }

        return productList;
    }

    /**
     * Fetches a single Product by ID.
     */
    public Product getProductById(String productId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot doc = db.collection(COLLECTION_NAME)
                .document(productId)
                .get()
                .get();

        if (doc.exists()) {
            Product product = doc.toObject(Product.class);
            if (product != null) {
                product.setId(doc.getId()); // Also set ID here
            }
            return product;
        }

        return null;
    }


    public String updateProduct(Product product) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(product.getId());

        ApiFuture<com.google.cloud.firestore.WriteResult> writeResult = docRef.set(product);
        return "Product updated at: " + writeResult.get().getUpdateTime();
    }

    /**
     * Deletes a Product document by ID.
     */
    public String deleteProduct(String productId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        db.collection(COLLECTION_NAME)
                .document(productId)
                .delete()
                .get();
        return "Product deleted successfully!";
    }
    // https://firebasestorage.googleapis.com/v0/b/uddco-f3018.firebasestorage.app/o/PoloT.glb?alt=media&token=d4138e4e-aa3d-44d2-935f-649ccc7acf79;
}
