package org.example.backend.service;

import org.example.backend.dto.Workstation.WorkstationRegisterDTO;
import org.example.backend.model.Workstation;
import org.example.backend.repository.CategoryRepository;
import org.example.backend.repository.WorkstationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkstationServiceTest {

    @Mock
    WorkstationRepository workstationRepository;

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    WorkstationService service;

    @Test
    void registerWorkstation_WhenWorkstationNameExists_ShouldReturnFalse(){
        // configuração mock
        Workstation mockWorkstation = Workstation.builder().name("mockWorkstation").build();
        WorkstationRegisterDTO request = new WorkstationRegisterDTO("mockWorkstation", null);
        when(workstationRepository.findByName("mockWorkstation")).thenReturn(Optional.of(mockWorkstation));

        // execução
        boolean success = service.registerWorkstation(request);

        // verificação
        assertNotNull(success, "Registro retornou nulo");
        assertFalse(success, "Registrou workstation já existente");
    }

}
