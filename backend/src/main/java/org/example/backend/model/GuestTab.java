package org.example.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.model.enums.GuestTabStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "guest_tabs")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GuestTab {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "status", nullable = false)
    private GuestTabStatus status;

    @Column(name = "time_opened", nullable = false, updatable = false)
    private LocalDateTime timeOpened;

    @Column(name = "time_closed")
    private LocalDateTime timeClosed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_table_id")
    private LocalTable user;

}
