package org.example.backend.service;

import org.example.backend.dto.LocalTableDTO;
import org.example.backend.dto.LocalTableGetDTO;
import org.example.backend.dto.LocalTableRequestDTO;
import org.example.backend.model.LocalTable;
import org.example.backend.model.enums.LocalTableStatus;
import org.example.backend.repository.LocalTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        List<LocalTableDTO> localTableDTOList = this.localTableRepository.findAllWithGuestTabCountTodayRaw();
        System.out.println(localTableDTOList.getFirst().toString());
        return localTableDTOList;
    }

}