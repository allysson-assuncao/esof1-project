package org.example.backend.service;

import org.example.backend.dto.LocalTable.LocalTableBulkRegisterDTO;
import org.example.backend.dto.LocalTable.LocalTableDTO;
import org.example.backend.dto.LocalTable.LocalTableGetDTO;
import org.example.backend.dto.LocalTable.LocalTableRequestDTO;
import org.example.backend.model.LocalTable;
import org.example.backend.model.enums.GuestTabStatus;
import org.example.backend.model.enums.LocalTableStatus;
import org.example.backend.repository.GuestTabRepository;
import org.example.backend.repository.LocalTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LocalTableService {

    private final LocalTableRepository localTableRepository;
    private final GuestTabRepository guestTabRepository;

    @Autowired
    public LocalTableService(LocalTableRepository localTableRepository, GuestTabRepository guestTabRepository) {
        this.localTableRepository = localTableRepository;
        this.guestTabRepository = guestTabRepository;
    }

    @Transactional(readOnly = true)
    public LocalTableGetDTO findByNumber(int number) {
        return localTableRepository
                .findByNumber(number)
                .map(LocalTableGetDTO::new)
                .orElse(null);
    }

    @Transactional
    public boolean registerLocalTable(LocalTableRequestDTO request) {
        // se já existe mesa com esse número, não cria
        if (localTableRepository.findByNumber(request.number()).isPresent()) {
            return false;
        }

        // monta a entidade com status inicial LIVRE
        LocalTable table = LocalTable.builder()
                .number(request.number())
                .status(LocalTableStatus.FREE)
                .build();

        // persiste via JPA
        localTableRepository.save(table);
        return true;
    }

    public int bulkRegisterLocalTable(LocalTableBulkRegisterDTO request) {
        LocalTableRequestDTO iterator;
        int created=0;
        for (int current_number= request.start(); current_number<=request.end(); current_number++) {
            iterator = new LocalTableRequestDTO(current_number);
            if(registerLocalTable(iterator)){
                created++;
            }
        }

        return created;
    }

    public List<LocalTableDTO> getGridTables() {

        return this.localTableRepository.findAll()
                .stream()
                .map(this::convertToLocalTableDTO).sorted().collect(Collectors.toList());
    }

    private LocalTableDTO convertToLocalTableDTO(LocalTable localTable) {
        if (localTable == null) return null;

        int guestTabCountOpen = this.localTableRepository.findActiveGuestTabCountById(localTable.getId());

        return LocalTableDTO.builder()
                .id(localTable.getId())
                .number(localTable.getNumber())
                .status(localTable.getStatus())
                .guestTabCountToday(guestTabCountOpen)
                .build();
    }

    public boolean hasOpenGuestTab(int tableNumber) {
        return guestTabRepository.existsByLocalTable_NumberAndStatus(tableNumber, GuestTabStatus.OPEN);
    }

    @Transactional
    public void updateTableStatusBasedOnGuestTabs(UUID tableId) {
        LocalTable table = localTableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Mesa não encontrada com id: " + tableId));

        long openTabsCount = guestTabRepository.countByLocalTableAndStatus(table, GuestTabStatus.OPEN);

        if (openTabsCount > 0) {
            table.setStatus(LocalTableStatus.OCCUPIED);
            System.out.println("Comanda fechada!");
        } else {
            table.setStatus(LocalTableStatus.FREE);
            System.out.println("Comanda liberada!");
        }
        localTableRepository.save(table);
    }

}
