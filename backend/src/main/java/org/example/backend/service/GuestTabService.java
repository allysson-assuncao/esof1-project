package org.example.backend.service;

import org.example.backend.repository.GuestTapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GuestTabService {

    private final GuestTapRepository guestTapRepository;

    @Autowired
    public GuestTabService(GuestTapRepository guestTapRepository) {
        this.guestTapRepository = guestTapRepository;
    }

    // Todo...
    public boolean registerGuestTap(String request){
        return false;
    }
}
