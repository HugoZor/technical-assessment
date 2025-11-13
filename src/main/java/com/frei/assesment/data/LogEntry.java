package com.frei.assesment.data;

import java.time.OffsetDateTime;

public record LogEntry(
        OffsetDateTime logTime,
        String user,
        Events userEvent,
        String logInfo
) {
}
