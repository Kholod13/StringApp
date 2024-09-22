package org.example.storage;

import org.example.service.FileSaveFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    void init() throws IOException;
    String save(MultipartFile file) throws IOException;
    String saveImage(MultipartFile file, FileSaveFormat format) throws IOException;
    String saveImage(String fileUrl, FileSaveFormat format) throws IOException;
}