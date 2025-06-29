package org.example.backend.service;

import org.example.backend.dto.*;
import org.example.backend.model.LocalTable;
import org.example.backend.model.enums.LocalTableStatus;
import org.example.backend.repository.LocalTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocalTableService {

    private final LocalTableRepository localTableRepository;

    @Autowired
    public LocalTableService(LocalTableRepository localTableRepository) {
        this.localTableRepository = localTableRepository;
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

    public List<LocalTableDTO> getGridTables() {
        List<LocalTableDTO> gridTables = this.localTableRepository.findAll()
                .stream()
                .map(this::convertToLocalTableDTO)
                .collect(Collectors.toList());

        Collections.sort(gridTables);
        return  gridTables;
    }

    private LocalTableDTO convertToLocalTableDTO(LocalTable localTable) {
        if (localTable == null) return null;

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        int guestTabCountToday = this.localTableRepository.findGuestTabCountTodayById(localTable.getId(), startOfDay, endOfDay);

        return LocalTableDTO.builder()
                .id(localTable.getId())
                .number(localTable.getNumber())
                .status(localTable.getStatus())
                .guestTabCountToday(guestTabCountToday)
                .build();
    }

}
