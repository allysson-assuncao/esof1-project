package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.backend.model.enums.GuestTabStatus;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "guest_tabs")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GuestTab {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    private GuestTabStatus status;

    @Column(name = "guest_name")
    private String guestName;

    @Column(name = "time_opened", nullable = false, updatable = false)
    private LocalDateTime timeOpened;

    @Column(name = "time_closed")
    private LocalDateTime timeClosed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_table_id")
    private LocalTable localTable;

    @OneToMany(mappedBy = "guestTab", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> orders;

}
