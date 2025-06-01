package org.example.backend.service;

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
    public boolean registerLocalTable(String request){
        return false;
    }

}
