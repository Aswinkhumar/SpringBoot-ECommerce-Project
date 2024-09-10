package com.eCommerce.application.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String saveImageToServer(String path, MultipartFile image) throws IOException;
}
