package com.example.rgrgit.repository;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    String saveImage(MultipartFile file) throws Exception;
}
