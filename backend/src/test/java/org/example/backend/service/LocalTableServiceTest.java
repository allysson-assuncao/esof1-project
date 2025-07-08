package org.example.backend.service;

import org.example.backend.dto.LocalTable.LocalTableGetDTO;
import org.example.backend.model.LocalTable;
import org.example.backend.model.enums.LocalTableStatus;
import org.example.backend.repository.GuestTabRepository;
import org.example.backend.repository.LocalTableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
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

}
