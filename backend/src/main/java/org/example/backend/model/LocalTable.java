package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.backend.model.enums.LocalTableStatus;

import java.util.List;
import java.util.UUID;

@Table(name = "tables")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LocalTable {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "number", nullable = false, unique = true)
    private int number;

    @Column(name = "status", nullable = false)
    private LocalTableStatus status;

    @OneToMany(mappedBy = "localTable", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<GuestTab> guestTabs;

}
