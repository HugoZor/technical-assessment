package com.frei.assesment.controller;

import com.frei.assesment.AssessmentService;
import com.frei.assesment.data.FileUploadInput;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ControllerImpl implements Controller {

    private final AssessmentService service;

    @Override
    public void processFiles(FileUploadInput fileUploadInput) {
        service.processFiles(fileUploadInput);
    }
}
