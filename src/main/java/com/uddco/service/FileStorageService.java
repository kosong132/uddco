// package com.uddco.service;
// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.nio.file.StandardCopyOption;
// import java.util.UUID;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// @Service
// public class FileStorageService {

//     @Value("${file.upload-dir}")
//     private String uploadDir;

//     public String storeFile(MultipartFile file) throws IOException {
//         // Create upload directory if it doesn't exist
//         Path uploadPath = Paths.get(uploadDir);
//         if (!Files.exists(uploadPath)) {
//             Files.createDirectories(uploadPath);
//         }

//         // Generate a unique filename
//         String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//         Path filePath = uploadPath.resolve(fileName);

//         // Save the file
//         Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

//         return fileName; // Return the stored filename
//     }
// }