package com.frei.assesment.data;

public record UserLoginStats(
        String user,
        long successCount,
        long failureCount) {

}

