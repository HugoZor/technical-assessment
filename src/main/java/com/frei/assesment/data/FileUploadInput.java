package com.frei.assesment.data;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record FileUploadInput(
        @NotEmpty
        List<MultipartFile> files
) {}
