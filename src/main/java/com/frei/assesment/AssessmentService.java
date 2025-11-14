package com.frei.assesment;

import com.frei.assesment.data.FileResults;
import com.frei.assesment.data.FileUploadInput;
import com.frei.assesment.data.ProcessResult;

import java.util.List;

public interface AssessmentService {

    ProcessResult processFiles(FileUploadInput fileUploadInput);

}
