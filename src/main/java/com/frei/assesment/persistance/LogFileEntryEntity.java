package com.frei.assesment.persistance;

import com.frei.assesment.data.Events;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;


@Accessors(chain = true)
@Setter
@Getter

@Entity
@Table(name = "log_entries")
public class LogFileEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private OffsetDateTime logTime;

    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_event")
    private Events userEvent;

    private String logInfo;

    private String fileName;

}
