package org.example.backend.service;

import org.example.backend.dto.LocalTableRequestDTO;
import org.example.backend.model.LocalTable;
import org.example.backend.repository.LocalTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocalTableService {

    private final LocalTableRepository localTableRepository;

    @Autowired
    public LocalTableService(LocalTableRepository localTableRepository) {
        this.localTableRepository = localTableRepository;
    }

    // Todo...
    public boolean registerLocalTable(LocalTableRequestDTO request) {
        if (localTableRepository.findByNumber(request.number()).isPresent()) {
            return false;
        }

        LocalTable table = LocalTable.builder()
                .number(request.number())
                .build();

        localTableRepository.save(table);
        return false;
    }
}
