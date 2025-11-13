package com.frei.assesment.service;

import com.frei.assesment.AssessmentService;
import com.frei.assesment.data.FileUploadInput;
import com.frei.assesment.data.LogEntry;
import com.frei.mapper.Mapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class AssessmentServiceImpl implements AssessmentService {

    @Override
    public void processFiles(FileUploadInput fileUploadInput) {

        //TODO skip bad line boolean

        /*
        Count the number of LOGIN_SUCCESS and LOGIN_FAILURE per user.
        Identify the top 3 users with the most FILE_UPLOAD events
        Detect suspicious activity: more than 3 LOGIN_FAILURE attempts from the same IP address within a 5-minute window
         */

        //Process file one by one
        for (MultipartFile file : fileUploadInput.files()) {
            System.out.println("Processing file: " + file.getOriginalFilename());

            List<LogEntry> fileEntries = new ArrayList<>();

            //READ FILE TO BUILD ENTRY SET FOR FILE
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    fileEntries.add(Mapper.toLogEntry(line));
                }

                System.out.println(fileEntries);

            } catch (IOException e) {
                System.err.println("Error printing file contents for " + file.getOriginalFilename() + ": " + e.getMessage());
            }

        }
    }
}
