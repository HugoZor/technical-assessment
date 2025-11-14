package com.frei.assesment.data;

import java.time.OffsetDateTime;

public record SuspiciousIpEvent(
        String ip,
        long failures,
        OffsetDateTime windowStart,
        OffsetDateTime windowEnd
) {}

