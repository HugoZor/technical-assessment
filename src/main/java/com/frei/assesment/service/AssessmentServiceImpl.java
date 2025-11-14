package com.frei.assesment.service;

import com.frei.assesment.AssessmentService;
import com.frei.assesment.data.*;
import com.frei.assesment.persistance.LogFileEntryEntity;
import com.frei.assesment.persistance.LogFileEntryRepo;
import com.frei.mapper.Mapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AssessmentServiceImpl implements AssessmentService {

    private final LogFileEntryRepo logFileEntryRepo;

    @Override
    public void processFiles(FileUploadInput fileUploadInput) {

        /*
        Count the number of LOGIN_SUCCESS and LOGIN_FAILURE per user.
        Identify the top 3 users with the most FILE_UPLOAD events
        Detect suspicious activity: more than 3 LOGIN_FAILURE attempts from the same IP address within a 5-minute window
         */

        HashMap<String, List<LogEntry>> fileLogEntries = new HashMap<>();
        List<LogFileEntryEntity> listLogFileEntries = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();

        //Process file one by one
        for (MultipartFile file : fileUploadInput.files()) {
            List<LogEntry> fileEntries = new ArrayList<>();
            fileNames.add(file.getOriginalFilename());

            //READ FILE TO BUILD ENTRY SET FOR FILE
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    LogEntry singleLog = Mapper.toLogEntry(line,file.getOriginalFilename());
                    fileEntries.add(singleLog);
                    listLogFileEntries.add(Mapper.toLogFileEntry(singleLog));
                }

                fileLogEntries.put(file.getOriginalFilename(), fileEntries);


            } catch (IOException e) {
                System.err.println("Error printing file contents for " + file.getOriginalFilename() + ": " + e.getMessage());
            }

        }

        System.out.println("Log file entries: " + fileLogEntries);

        logFileEntryRepo.saveAll(listLogFileEntries);

        for(String fileName : fileNames) {
            System.out.println("Login Stats per file: " +  fileName);
            System.out.println(getLoginStats(fileName));
            System.out.println("Upload Stats per file: " +  fileName);
            System.out.println(getTop3Uploader(fileName));
            System.out.println("IP Stats per file: " +  fileName);
            System.out.println(detectSuspiciousIps(fileName));
            System.out.println("------------------------------");
        }




    }

    public List<UserLoginStats> getLoginStats(String fileName) {
        return logFileEntryRepo.countLoginStats(fileName).stream()
                .map(stats -> new UserLoginStats(
                        (String) stats[0],
                        ((Number) stats[1]).longValue(),
                        ((Number) stats[2]).longValue()
                ))
                .toList();
    }

    public List<TopUploader> getTop3Uploader(String fileName) {
        return logFileEntryRepo.findTopUploader(fileName).stream()
                .map(uploader -> new TopUploader(
                        (String) uploader[0],
                        ((Number) uploader[1]).longValue()
                ))
                .toList();
    }

    public List<SuspiciousIpEvent> detectSuspiciousIps(String fileName) {

        List<LogFileEntryEntity> entries = logFileEntryRepo.findByFileName(fileName);

        // Filter only LOGIN_FAILURE
        Map<String, List<LogFileEntryEntity>> failuresByIp = entries.stream()
                .filter(e -> e.getUserEvent() == Events.LOGIN_FAILURE)
                .collect(Collectors.groupingBy(LogFileEntryEntity::getLogInfo));

        List<SuspiciousIpEvent> suspiciousIps = new ArrayList<>();

        for (Map.Entry<String, List<LogFileEntryEntity>> entry : failuresByIp.entrySet()) {
            String ip = entry.getKey();
            List<LogFileEntryEntity> failures = entry.getValue();

            // Sort by log_time
            failures.sort(Comparator.comparing(LogFileEntryEntity::getLogTime));

            // Sliding window check
            int startIdx = 0;
            for (int endIdx = 0; endIdx < failures.size(); endIdx++) {
                while (Duration.between(failures.get(startIdx).getLogTime(), failures.get(endIdx).getLogTime()).toMinutes() > 5) {
                    startIdx++;
                }
                int windowSize = endIdx - startIdx + 1;
                if (windowSize > 3) { // more than 3 failures in 5 minutes
                    suspiciousIps.add(new SuspiciousIpEvent(
                            ip,
                            windowSize,
                            failures.get(startIdx).getLogTime(),
                            failures.get(endIdx).getLogTime()
                    ));
                    break; // only report each IP once
                }
            }
        }

        return suspiciousIps;
    }
}
