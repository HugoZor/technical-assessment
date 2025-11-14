package com.frei.assesment.controller;

import com.frei.assesment.data.FileResults;
import com.frei.assesment.data.FileUploadInput;
import com.frei.assesment.data.ProcessResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/v1")
@Tag(name = "Technical Assessment")
public interface Controller {

    @PostMapping(value = "/process", consumes = "multipart/form-data")
    @Operation(summary = "Upload one or more .log files for processing")
    ProcessResult processFiles(@Valid @ModelAttribute FileUploadInput fileUploadInput);

    @PostMapping(value = "/process/download", consumes = "multipart/form-data", produces = "application/json")
    @Operation(summary = "Upload log files and download result.json")
    ResponseEntity<byte[]> processFilesAndDownload(
            @Valid @ModelAttribute FileUploadInput fileUploadInput
    );
}
