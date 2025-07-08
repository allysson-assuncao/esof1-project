package org.example.backend.service;

import org.example.backend.dto.LocalTable.LocalTableDTO;
import org.example.backend.dto.LocalTable.LocalTableGetDTO;
import org.example.backend.dto.LocalTable.LocalTableRequestDTO;
import org.example.backend.model.LocalTable;
import org.example.backend.model.enums.GuestTabStatus;
import org.example.backend.model.enums.LocalTableStatus;
import org.example.backend.repository.GuestTabRepository;
import org.example.backend.repository.LocalTableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocalTableServiceTest {

    @Mock
    LocalTableRepository localTableRepository;

    @Mock
    GuestTabRepository guestTabRepository;

    @InjectMocks
    LocalTableService service;

    @Test
    void findByNumber_WhenTableExists_ShouldReturnDTO(){
        // Configuração mock
        LocalTable mockTable = LocalTable.builder()
                .number(1)
                .status(LocalTableStatus.FREE)
                .build();

        when(localTableRepository.findByNumber(1)).thenReturn(Optional.of(mockTable));

        // Execução
        LocalTableGetDTO result = service.findByNumber(1);

        // Verificação
        assertNotNull(result, "DTO é null");
        assertEquals(1, result.getNumber(), "Numero da mesa diferente do esperado");
        assertEquals(LocalTableStatus.FREE, result.getStatus(), "Mesa iniciada com status incorreto");

        verify(localTableRepository, times(1)).findByNumber(1);
    }

    @Test
    void findByNumber_WhenTableDoesNotExist_ShouldReturnNull() {
        when(localTableRepository.findByNumber(99)).thenReturn(Optional.empty());

        LocalTableGetDTO result = service.findByNumber(99);

        assertNull(result, "Esperava null quando a mesa não existe");
        verify(localTableRepository, times(1)).findByNumber(99);
    }

    @Test
    void registerLocalTable_WhenTableDoesNotExist_ShouldSaveAndReturnTrue() {
        // given
        LocalTableRequestDTO req = new LocalTableRequestDTO(98);
        when(localTableRepository.findByNumber(98)).thenReturn(Optional.empty());

        // when
        boolean created = service.registerLocalTable(req);

        // then
        assertTrue(created, "Deveria retornar true ao criar nova mesa");
        verify(localTableRepository, times(1)).findByNumber(98);
        verify(localTableRepository, times(1)).save(any(LocalTable.class));
    }

    @Test
    void registerLocalTable_WhenTableExists_ShouldNotSaveAndReturnFalse() {
        // given
        LocalTable existing = LocalTable.builder()
                .number(5)
                .status(LocalTableStatus.FREE)
                .build();
        LocalTableRequestDTO req = new LocalTableRequestDTO(5);
        when(localTableRepository.findByNumber(5)).thenReturn(Optional.of(existing));

        // when
        boolean created = service.registerLocalTable(req);

        // then
        assertFalse(created, "Deveria retornar false quando mesa já existe");
        verify(localTableRepository, times(1)).findByNumber(5);
        verify(localTableRepository, never()).save(any());
    }

    @Test
    void hasOpenGuestTab_WhenExists_ReturnsTrue() {
        when(guestTabRepository.existsByLocalTable_NumberAndStatus(3, GuestTabStatus.OPEN))
                .thenReturn(true);

        boolean hasOpen = service.hasOpenGuestTab(3);

        assertTrue(hasOpen, "Deveria retornar true quando houver guest tab aberta");
        verify(guestTabRepository, times(1))
                .existsByLocalTable_NumberAndStatus(3, GuestTabStatus.OPEN);
    }

    @Test
    void hasOpenGuestTab_WhenNotExists_ReturnsFalse() {
        when(guestTabRepository.existsByLocalTable_NumberAndStatus(4, GuestTabStatus.OPEN))
                .thenReturn(false);

        boolean hasOpen = service.hasOpenGuestTab(4);

        assertFalse(hasOpen, "Deveria retornar false quando não houver guest tab aberta");
        verify(guestTabRepository, times(1))
                .existsByLocalTable_NumberAndStatus(4, GuestTabStatus.OPEN);
    }
}