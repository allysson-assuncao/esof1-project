package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.backend.model.enums.LocalTableStatus;

import java.util.List;
import java.util.UUID;

@Table(name = "tables")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LocalTable {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "status", nullable = false)
    private LocalTableStatus status;

    @OneToMany(mappedBy = "local_table", cascade = CascadeType.DETACH)
    @JsonIgnore
    private List<GuestTab> guestTabs;

}
