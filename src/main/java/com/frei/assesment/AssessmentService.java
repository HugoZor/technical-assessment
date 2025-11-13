package com.frei.assesment;

import com.frei.assesment.data.FileUploadInput;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AssessmentService {

    void processFiles(FileUploadInput fileUploadInput);

}
