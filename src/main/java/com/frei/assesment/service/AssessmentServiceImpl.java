package com.frei.assesment.service;

import com.frei.assesment.AssessmentService;
import com.frei.assesment.data.FileUploadInput;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@Service
public class AssessmentServiceImpl implements AssessmentService {

    @Override
    public void processFiles(FileUploadInput fileUploadInput) {

        for (MultipartFile file : fileUploadInput.files()) {
            System.out.println("Processing file: " + file.getOriginalFilename());
        }
    }
}
