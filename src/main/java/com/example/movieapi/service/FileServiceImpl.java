package com.example.movieapi.service;


import com.example.movieapi.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService{

    @Autowired
    private final MovieRepository movieRepository;

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();

        String filePath = path + File.separator + fileName;
        File file1 = new File(path);
        if (!file1.exists()){
            file1.mkdir();
        }

        Files.copy(file.getInputStream(), Paths.get(filePath));
        return fileName;
    }

    @Override
    public InputStream getResourceFile(String path, String fileName) throws IOException {
        String filePath = path + File.separator + fileName;
        return new FileInputStream(filePath);
    }
}
