package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.backend.model.enums.GuestTabStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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
    private LocalTable localTable;

    @OneToMany(mappedBy = "guestTab", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<GuestTab> guestTabs;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "guest_tab_products",
        joinColumns = @JoinColumn(name = "guest_tab_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @JsonIgnore
    private Set<Category> categoryList;

}
