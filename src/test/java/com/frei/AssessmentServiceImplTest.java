package com.frei;

import com.frei.assesment.data.*;
import com.frei.assesment.persistance.LogFileEntryRepo;
import com.frei.assesment.service.AssessmentServiceImpl;
import com.frei.common.exception.BadRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootTest
@Transactional
class AssessmentServiceImplTest {

    @Autowired
    private AssessmentServiceImpl service;

    @Autowired
    private LogFileEntryRepo repo;

    private static final String SAMPLE_DATA_FILE_1 = """
            2025-09-15T14:23:56Z | USER123 | LOGIN_SUCCESS | IP=192.168.1.10
            2025-09-15T14:25:11Z | USER456 | LOGIN_FAILURE | IP=192.168.1.11
            2025-09-15T14:30:05Z | USER123 | FILE_UPLOAD | FILE=report.pdf
            2025-09-15T14:32:47Z | USER789 | LOGIN_SUCCESS | IP=192.168.1.12
            2025-09-15T14:33:12Z | USER456 | LOGIN_FAILURE | IP=192.168.1.11
            2025-09-15T14:34:05Z | USER456 | LOGIN_FAILURE | IP=192.168.1.11
            2025-09-15T14:34:45Z | USER456 | LOGIN_FAILURE | IP=192.168.1.11
            2025-09-15T14:35:10Z | USER456 | LOGIN_FAILURE | IP=192.168.1.11
            2025-09-15T14:36:00Z | USER123 | FILE_UPLOAD | FILE=data.csv
            2025-09-15T14:37:25Z | USER123 | FILE_UPLOAD | FILE=summary.xlsx
            2025-09-15T14:39:50Z | USER789 | FILE_UPLOAD | FILE=notes.docx
            2025-09-15T14:39:50Z | USER890 | FILE_UPLOAD | FILE=passwords.docx
            """;

    private static final String SAMPLE_DATA_FILE_2 = """
            2025-09-15T14:40:10Z | USER789 | FILE_UPLOAD | FILE=invoice.pdf
            2025-09-15T14:41:30Z | USER999 | LOGIN_FAILURE | IP=192.168.1.13
            2025-09-15T14:42:00Z | USER999 | LOGIN_SUCCESS | IP=192.168.1.13
            2025-09-15T14:43:15Z | USER789 | FILE_UPLOAD | FILE=presentation.pptx
            2025-09-15T14:45:30Z | USER789 | FILE_UPLOAD | FILE=budget.csv
            2025-09-15T14:47:20Z | USER123 | LOGIN_FAILURE | IP=192.168.1.10
            2025-09-15T14:49:00Z | USER123 | LOGIN_SUCCESS | IP=192.168.1.10
            2025-09-15T14:52:45Z | USER888 | FILE_UPLOAD | FILE=project.zip
            2025-09-15T14:53:30Z | USER888 | FILE_UPLOAD | FILE=diagram.png
            """;

    private static final String SAMPLE_DATA_FILE_3 = """
            2025-09-15T14:40:10Z | USER789 | FILE_UPLOAD | FILE=invoice.pdf
            2025-09-15T14:41:30Z | USER999 | LOGIN_FAILURE | IP=192.168.1.13
            2025-09-15T14:42:00Z | USER999 | LOGIN_SUCCESS | IP=192.168.1.13
            2025-09-15T14:43:15Z | USER789 | FILE_UPLOAD | FILE=presentation.pptx
            2025-09-15T14:45:30Z | USER789 | FILE_UPLOAD | FILE=budget.csv
            2025-09-15T14:47:20Z | USER123 | LOGIN_FAILURE | IP=192.168.1.10
            2025-09-15T14:49:00Z | USER123 | LOGIN_SUCCESS | IP=192.168.1.10
            2025-09-15T14:49:00Z | USER000 | LOGIN_PENDING | IP=192.168.1.10
            2025-09-15T14:52:45Z | USER888 | FILE_UPLOAD | FILE=project.zip
            2025-09-15T14:53:30Z | USER888 | FILE_UPLOAD | FILE=diagram.png
            """;

    @BeforeEach
    void cleanup() {
        repo.deleteAll();
    }

    @Test
    void testProcessFiles_ValidFiles() {
        ProcessResult result = service.processFiles(buildValidMultiFileUploadInput());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.results().size());
    }

    @Test
    void testProcessFiles_InvalidFiles() {
        Assertions.assertThrows(BadRequestException.class, () -> {
            service.processFiles(buildInvalidFileUploadInput());
        });
    }

    @Test
    void testProcessFiles_DuplicateFileNames() {
        Assertions.assertThrows(BadRequestException.class, () -> {
            service.processFiles(buildInvalidNamesFileUploadInput());
        });
    }

    @Test
    void testUserLoginCounts_ValidFiles() {
        ProcessResult result = service.processFiles(buildValidFileUploadInput());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.results().size());

        FileResults file = result.results().get(0);
        Assertions.assertNotNull(file);

        Assertions.assertNotNull(file.userLoginStats());
        Assertions.assertTrue(file.userLoginStats().stream().anyMatch(stats -> stats.user().equals("USER123") && stats.successCount() == 1));
        Assertions.assertTrue(file.userLoginStats().stream().anyMatch(stats -> stats.user().equals("USER456") && stats.failureCount() == 5));

    }

    @Test
    void testTopUploader_ValidFiles() {
        ProcessResult result = service.processFiles(buildValidFileUploadInput());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.results().size());

        FileResults file = result.results().get(0);
        Assertions.assertNotNull(file);

        Assertions.assertNotNull(file.topUploader());
        Assertions.assertEquals(3, file.topUploader().size());
        Assertions.assertTrue(file.topUploader().stream().anyMatch(stats -> stats.user().equals("USER123") && stats.uploads() == 3));
        Assertions.assertTrue(file.topUploader().stream().anyMatch(stats -> stats.user().equals("USER789") && stats.uploads() == 1));
        Assertions.assertTrue(file.topUploader().stream().anyMatch(stats -> stats.user().equals("USER890") && stats.uploads() == 1));

    }

    @Test
    void testSuspiciousIps_ValidFiles() {
        ProcessResult result = service.processFiles(buildValidFileUploadInput());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.results().size());

        FileResults file = result.results().get(0);
        Assertions.assertNotNull(file);

        Assertions.assertNotNull(file.suspiciousIps());
        Assertions.assertEquals(1,file.suspiciousIps().size());
        Assertions.assertTrue(file.suspiciousIps().get(0).failures() >= 3);
        Assertions.assertTrue(file.suspiciousIps().stream().anyMatch(stats -> stats.ip().equals("192.168.1.11")));

    }

    //HELPER FUNCTIONS
    FileUploadInput buildValidMultiFileUploadInput() {
        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "file1.log",
                "text/plain",
                SAMPLE_DATA_FILE_1.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "file2.log",
                "text/plain",
                SAMPLE_DATA_FILE_2.getBytes(StandardCharsets.UTF_8)
        );

        return new FileUploadInput(List.of(file1, file2));

    }

    FileUploadInput buildValidFileUploadInput() {
        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "file1.log",
                "text/plain",
                SAMPLE_DATA_FILE_1.getBytes(StandardCharsets.UTF_8)
        );

        return new FileUploadInput(List.of(file1));

    }

    FileUploadInput buildInvalidFileUploadInput() {
        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "file1.log",
                "text/plain",
                SAMPLE_DATA_FILE_1.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile file3 = new MockMultipartFile(
                "files",
                "file3.log",
                "text/plain",
                SAMPLE_DATA_FILE_3.getBytes(StandardCharsets.UTF_8)
        );

        return new FileUploadInput(List.of(file1, file3));

    }

    FileUploadInput buildInvalidNamesFileUploadInput() {
        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "file1.log",
                "text/plain",
                SAMPLE_DATA_FILE_1.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "file1.log",
                "text/plain",
                SAMPLE_DATA_FILE_2.getBytes(StandardCharsets.UTF_8)
        );

        return new FileUploadInput(List.of(file1, file2));

    }

}
