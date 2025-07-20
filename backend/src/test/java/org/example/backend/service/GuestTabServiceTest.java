package org.example.backend.service;

import org.example.backend.dto.GuestTab.GuestTabGetDTO;
import org.example.backend.dto.GuestTab.GuestTabRequestDTO;
import org.example.backend.model.GuestTab;
import org.example.backend.model.LocalTable;
import org.example.backend.model.enums.GuestTabStatus;
import org.example.backend.repository.GuestTabRepository;
import org.example.backend.repository.LocalTableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GuestTabServiceTest {

    @Mock
    private GuestTabRepository guestTabRepository;

    @Mock
    private LocalTableRepository localTableRepository;

    @InjectMocks
    private GuestTabService guestTabService;

    @Test
    void registerGuestTab_WhenTableExists_ShouldReturnTrue(){
        // config mock
        LocalTable mockLocalTable = Mockito.mock(LocalTable.class);
        GuestTabRequestDTO mockGuestTabRequestDTO = Mockito.mock(GuestTabRequestDTO.class);
        when(localTableRepository.findById(mockGuestTabRequestDTO.localTableId())).thenReturn(Optional.of(mockLocalTable));

        // execução
        boolean result = guestTabService.registerGuestTab(mockGuestTabRequestDTO);

        // verificação
        assertTrue(result);
    }

    @Test
    void registerGuestTab_WhenTableNotExists_ShouldReturnFalse(){
        // config mock
        GuestTabRequestDTO mockGuestTabRequestDTO = Mockito.mock(GuestTabRequestDTO.class);

        // execução
        boolean result = guestTabService.registerGuestTab(mockGuestTabRequestDTO);

        // verificação
        assertFalse(result, "GuestTab registrada em mesa inexistente");
    }

    @Test
    void getGuestTabsByTableNumber_WhenTableNumberNotFound_ShouldReturnEmptyList() {
        // config mock
        LocalTable mockLocalTable = Mockito.mock(LocalTable.class);

        // execução
        List<GuestTabGetDTO> result = guestTabService.getGuestTabsByTableNumber(mockLocalTable.getNumber());

        // verificação
        assertNotNull(result, "Resultado retornou null");
        assertArrayEquals(new GuestTabGetDTO[]{}, result.toArray(), "Lista de mesa inexistente veio preenchida");

    }

    @Test
    void getGuestTabsByTableNumber_WhenTableNumberExists_ShouldReturnGuestTabGetDTOs(){
        // config mock
        LocalTable mockLocalTable = LocalTable.builder()
                .number(new Random().nextInt(1000)+1)
                .build();
        List<GuestTab> mockGuestTabs = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            GuestTab mockGuestTab = GuestTab.builder()
                    .localTable(mockLocalTable)
                    .status(GuestTabStatus.OPEN)
                    .build();
            mockGuestTabs.add(mockGuestTab);
        }
        when(localTableRepository.findByNumber(mockLocalTable.getNumber()))
                .thenReturn(Optional.of(mockLocalTable));
        when(guestTabRepository.findByLocalTable(mockLocalTable)).thenReturn(mockGuestTabs);

        // execução
        List<GuestTabGetDTO> result = guestTabService.getGuestTabsByTableNumber(mockLocalTable.getNumber());

        // verificação
        assertNotNull(result, "Resultado retornou null");
        assertNotEquals(new ArrayList<>(), result, "Retornou lista vazia");
        assertEquals(GuestTabGetDTO.class, result.getFirst().getClass(), "");

    }


}
