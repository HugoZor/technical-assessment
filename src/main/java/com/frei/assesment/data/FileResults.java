package com.frei.assesment.data;

import java.util.List;

public record FileResults(
        String fileName,
        List<UserLoginStats> userLoginStats,
        List<TopUploader> topUploader,
        List<SuspiciousIpEvent> suspiciousIps
) {
}
