package com.frei.assesment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frei.assesment.AssessmentService;
import com.frei.assesment.data.FileUploadInput;
import com.frei.assesment.data.ProcessResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@RestController
public class ControllerImpl implements Controller {

    private final AssessmentService service;
    private final ObjectMapper objectMapper;

    @Override
    public ProcessResult processFiles(FileUploadInput fileUploadInput) {
        return service.processFiles(fileUploadInput);
    }

    @Override
    public ResponseEntity<byte[]> processFilesAndDownload(FileUploadInput fileUploadInput) {

        // Get the normal result object
        ProcessResult result = service.processFiles(fileUploadInput);

        try {
            // Convert object to pretty JSON string
            String jsonString = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(result);

            byte[] jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=result.json")
                    .contentType(MediaType.APPLICATION_JSON)
                    .contentLength(jsonBytes.length)
                    .body(jsonBytes);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JSON file", e);
        }
    }
}
