package com.frei.assesment;

import com.frei.assesment.data.FileUploadInput;
import com.frei.assesment.data.ProcessResult;

public interface AssessmentService {

    ProcessResult processFiles(FileUploadInput fileUploadInput);

}
