package com.nrifintech.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.nrifintech.exception.FileStorageException;
import com.nrifintech.model.DatabaseFile;

@Service
public class DatabaseFileService {
	public DatabaseFile storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            System.out.println(fileName);
            System.out.println(file.getContentType());
            DatabaseFile dbFile = new DatabaseFile(fileName, file.getContentType(), file.getBytes());

            return dbFile;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
}
