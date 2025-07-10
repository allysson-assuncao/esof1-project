package org.example.backend.service;

import org.example.backend.repository.GuestTabRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GuestTabServiceTest {

    @Mock
    private GuestTabRepository guestTabRepository;

    @InjectMocks
    private GuestTabService guestTabService;

    @Test
    void getGuestTabsByTableNumber_WhenTableNumberNotFound_ShouldReturnEmptyList() {

    }

}
