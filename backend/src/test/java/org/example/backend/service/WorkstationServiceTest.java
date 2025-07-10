package org.example.backend.service;

import org.example.backend.dto.Workstation.SimpleWorkstationDTO;
import org.example.backend.dto.Workstation.WorkstationRegisterDTO;
import org.example.backend.model.Workstation;
import org.example.backend.repository.CategoryRepository;
import org.example.backend.repository.WorkstationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
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
        WorkstationRegisterDTO request = new WorkstationRegisterDTO("mockWorkstation", null);
        when(workstationRepository.existsByName("mockWorkstation")).thenReturn(true);

        // execução
        boolean success = service.registerWorkstation(request);

        // verificação
        assertNotNull(success, "Registro retornou nulo");
        assertFalse(success, "Teve sucesso em registrar workstation já existente");
    }

    @Test
    void registerWorkstation_WhenWorkstationNameNotExists_ShouldReturnTrue(){
        // config mock
        WorkstationRegisterDTO request = new WorkstationRegisterDTO("mockWorkstation", null);
        when(workstationRepository.existsByName("mockWorkstation")).thenReturn(false);

        // execução do teste
        boolean success = service.registerWorkstation(request);

        // verificação
        assertNotNull(success, "Registro retornou nulo");
        assertTrue(success, "Não conseguiu registrar mesmo que Workstation ainda não existisse");

    }

    @Test
    void getAllWorkstations_WhenThereAreWorkstations_ShouldReturnSimpleWorkstationDTO(){
        // config mock
        List<Workstation> mockWorkstationList = new ArrayList<>();
        mockWorkstationList.add(Workstation.builder().name("mockWorkstation1").build());
        mockWorkstationList.add(Workstation.builder().name("mockWorkstation2").build());
        when(workstationRepository.findAll()).thenReturn(mockWorkstationList);

        // execução
        List<SimpleWorkstationDTO> workstations = service.getAllWorkstations();

        // verificação
        assertNotNull(workstations, "Busca de workstations retornou null");
        assertEquals(SimpleWorkstationDTO.class, workstations.getFirst().getClass(),"Não retornou SimpleWorkstationDTO");
    }

    @Test
    void getAllWorkstations_WhenThereAreNoWorkstations_ShouldReturnEmptyList(){
        // config mock
        List<Workstation> mockWorkstationList = new ArrayList<>();
        when(workstationRepository.findAll()).thenReturn(mockWorkstationList);

        // execução
        List<SimpleWorkstationDTO> workstations = service.getAllWorkstations();

        // verificação
        assertNotNull(workstations, "Busca de workstations retornou null");
        assertArrayEquals(new SimpleWorkstationDTO[]{}, workstations.toArray(), "Não retornou lista vazia, mesmo sem dados no banco");
    }

    @Test
    void getAllWorkstationsByEmployee_WhenUserIdIsNull_ShouldReturnEmptyList(){
        List<Workstation> mockWorkstationList = new ArrayList<>();
    }

}
