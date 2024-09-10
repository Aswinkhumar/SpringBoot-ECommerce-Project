package com.eCommerce.application.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
@Service
public class FileServiceImpl implements FileService{
    @Override
    public String saveImageToServer(String path, MultipartFile image) throws IOException {
        // getting the complete file name of the image
        String fileName = image.getOriginalFilename();

        //updating the file name to a unique name
        //Creating a unique uuid
        String uniqueName = UUID.randomUUID().toString();
        //getting the extension of the image ex: .png
        String uniqueImageName = uniqueName.concat(fileName.substring(fileName.lastIndexOf(".")));
        // Creating the new file path

        String imageFilePath = path + File.separator + uniqueImageName;

        // Creating the images folder if not exists
        File folder = new File(path);
        if(!folder.exists()){
            System.out.println("Images folder crated newly ? " + folder.mkdir());
        }

        // Copying the image to the server
        Files.copy(image.getInputStream(), Paths.get(imageFilePath));

        return uniqueImageName;
    }
}
