package com.frei.assesment.persistance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogFileEntryRepo extends JpaRepository<LogFileEntryEntity, Long> {

    @Query(value = """
    SELECT user_name,
           SUM(CASE WHEN user_event = 'LOGIN_SUCCESS' THEN 1 ELSE 0 END) AS success_count,
           SUM(CASE WHEN user_event = 'LOGIN_FAILURE' THEN 1 ELSE 0 END) AS failure_count
    FROM log_entries
    WHERE file_name = :fileName
    GROUP BY user_name
""", nativeQuery = true)
    List<Object[]> countLoginStats(@Param("fileName") String fileName);

    @Query(value = """
    SELECT user_name,
           COUNT(*) AS uploads
    FROM log_entries
    WHERE user_event = 'FILE_UPLOAD'
      AND file_name = :fileName
    GROUP BY user_name
    ORDER BY uploads DESC
    LIMIT 3
""", nativeQuery = true)
    List<Object[]> findTopUploader(@Param("fileName") String fileName);

    List<LogFileEntryEntity> findByFileName(String fileName);
}
