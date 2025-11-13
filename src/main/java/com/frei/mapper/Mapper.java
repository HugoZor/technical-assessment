package com.frei.mapper;

import com.frei.assesment.data.LogEntry;
import com.frei.assesment.data.Events;
import com.frei.common.exception.BadRequestException;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

public class Mapper {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * @param logLine a single line of the log file
     * @return - LogEntry DTO
     */
    public static LogEntry toLogEntry(String logLine) {

        String[] values = logLine.split("\\|");

        for(int i = 0; i < values.length; i++){
            values[i] = values[i].trim();
        }

        //OPTIONAL - add boolean to endpoint to skip bad lines?
        //inaccurate log analytics as side effect since not all entries are processed
        if(values.length != 4){
            throw new BadRequestException("Malformed log line detected");
        } else if (!values[3].contains("=")){
            throw  new BadRequestException("Malformed log line detected");
        } else if (Arrays.stream(Events.values()).noneMatch(eventValue -> eventValue.name().equals(values[2]))){
            throw  new BadRequestException("Malformed log line detected");
        } else {
            isValidIsoUtc(values[0]);
        }

        String[] eventValues = values[3].split("=");
        if(eventValues.length != 2){
            throw new BadRequestException("Malformed log line detected");
        } else {
            for (int i = 0; i < eventValues.length; i++){
                eventValues[i] = eventValues[i].trim();
            }
        }

        return new LogEntry(
                OffsetDateTime.parse(values[0]),
                values[1],
                Events.valueOf(values[2]),
                eventValues[1]
        );
    }

    //HELPER FUNCTION
    private static void isValidIsoUtc(String input) {
        try {
            OffsetDateTime.parse(input);
        } catch (DateTimeParseException e) {
            throw  new BadRequestException("Malformed log line detected");
        }
    }
}
