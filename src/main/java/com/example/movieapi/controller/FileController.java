package com.example.movieapi.controller;


import com.example.movieapi.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/file/")
public class FileController {

    @Autowired
    private FileService fileService;

//    @Value("${project.poster}")
    private String path;


    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestPart MultipartFile file) throws IOException{
      String uploadFileName = fileService.uploadFile(path, file);
      return ResponseEntity.ok("File uploaded: " + uploadFileName);
    }

    @GetMapping(value = "/{fileName}")
    public void serveFileHandler(@PathVariable String fileName, HttpServletResponse httpServletResponse) throws IOException {
       InputStream resourceFile =  fileService.getResourceFile(path,fileName);
       httpServletResponse.setContentType(MediaType.IMAGE_PNG_VALUE);
       StreamUtils.copy(resourceFile, httpServletResponse.getOutputStream());

    }
}
