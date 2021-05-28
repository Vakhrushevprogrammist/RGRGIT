package com.example.rgrgit.service;

import com.example.rgrgit.repository.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileUploadServiceImpl implements FileUploadService {
    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public String saveImage(MultipartFile file) throws Exception {
        System.out.println(uploadPath);
        File directory = new File(uploadPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        byte[] bytes = file.getBytes();
        Path path = Paths.get(uploadPath + "\\" + file.getOriginalFilename());
        Files.write(path, bytes);
        return file.getOriginalFilename();
    }
}

