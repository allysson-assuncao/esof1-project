package org.example.backend.service;

import org.example.backend.dto.Workstation.SimpleWorkstationDTO;
import org.example.backend.dto.Workstation.WorkstationRegisterDTO;
import org.example.backend.model.User;
import org.example.backend.model.Workstation;
import org.example.backend.repository.WorkstationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkstationServiceTest {

    @Mock
    private WorkstationRepository workstationRepository;

    @InjectMocks
    private WorkstationService service;

    @Test
    void registerWorkstation_WhenWorkstationNameExists_ShouldReturnFalse(){
        // configuração mock
        WorkstationRegisterDTO request = Mockito.mock(WorkstationRegisterDTO.class);
        when(workstationRepository.existsByName(request.name())).thenReturn(true);

        // execução
        boolean success = service.registerWorkstation(request);

        // verificação
        assertNotNull(success, "Registro retornou nulo");
        assertFalse(success, "Teve sucesso em registrar workstation já existente");
    }

    @Test
    void registerWorkstation_WhenWorkstationNameNotExists_ShouldReturnTrue(){
        // config mock
        WorkstationRegisterDTO request = Mockito.mock(WorkstationRegisterDTO.class);
        when(workstationRepository.existsByName(request.name())).thenReturn(false);

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
        for(int i=0;i<10;i++){
            mockWorkstationList.add(Mockito.mock(Workstation.class));
        }
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
        // execução
        List<SimpleWorkstationDTO> workstations = service.getAllWorkstationsByEmployee(null);

        // verificação
        assertNotNull(workstations, "Busca de workstations retornou null");
        assertArrayEquals(new SimpleWorkstationDTO[]{}, workstations.toArray(), "Retornou workstations para usuário nulo");
    }

    @Test
    void getAllWorkstationsByEmployee_WhenUserIdNotFound_ShouldReturnEmptyList(){
        // config mock
        User mockUser = Mockito.mock(User.class);

        // execução
        List<SimpleWorkstationDTO> workstations = service.getAllWorkstationsByEmployee(mockUser);

        // verificação
        assertNotNull(workstations, "Busca de workstations retornou null");
        assertArrayEquals(new SimpleWorkstationDTO[]{}, workstations.toArray(), "Retornou workstations de usuário inexistente");
    }

    @Test
    void getAllWorkstationsByEmployee_WhenUserIdIsFound_ShouldReturnSimpleWorkstationDTOs(){
        // config mock
        Set<Workstation> mockWorkstationList = new HashSet<>();
        for(int i=0;i<10;i++){
            mockWorkstationList.add(Mockito.mock(Workstation.class));
        }
        User mockUser = Mockito.mock(User.class);
        when(workstationRepository.findWorkstationsByUserId(mockUser.getId()))
                .thenReturn(mockWorkstationList.stream().toList());
        List<SimpleWorkstationDTO> model = new ArrayList<>();

        // execução
        List<SimpleWorkstationDTO> workstations = service.getAllWorkstationsByEmployee(mockUser);


        // verificação
        assertNotNull(workstations, "Busca de workstations retornou null");
        assertNotEquals(model, workstations, "Retornou lista vazia, mesmo com dados no banco");
        assertEquals(ArrayList.class, workstations.getClass(), "Não retornou ArrayList");
        if(!workstations.isEmpty()){
            assertEquals(SimpleWorkstationDTO.class, workstations.getFirst().getClass(), "Elementos do ArrayList não são SimpleWorkstationDTO");
        }
    }

}
