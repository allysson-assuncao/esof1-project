package org.example.backend.service;

import org.example.backend.dto.GuestTab.GuestTabRequestDTO;
import org.example.backend.model.LocalTable;
import org.example.backend.repository.GuestTabRepository;
import org.example.backend.repository.LocalTableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
        LocalTable mockLocalTable = Mockito.mock(LocalTable.class);
        GuestTabRequestDTO mockGuestTabRequestDTO = Mockito.mock(GuestTabRequestDTO.class);
        when(localTableRepository.findById(mockGuestTabRequestDTO.localTableId())).thenReturn(null);

        // execução
        boolean result = guestTabService.registerGuestTab(mockGuestTabRequestDTO);

        // verificação
        assertFalse(result);
    }

    @Test
    void getGuestTabsByTableNumber_WhenTableNumberNotFound_ShouldReturnEmptyList() {

    }

}
