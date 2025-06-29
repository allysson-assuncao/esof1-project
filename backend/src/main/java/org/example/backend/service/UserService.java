package org.example.backend.service;

import org.example.backend.dto.Waiter.SimpleWaiterDTO;
import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<SimpleWaiterDTO> selectWaitersByLocalTableId(UUID localTableID) {
        return this.userRepository.findByOrdersGuestTabLocalTableId(localTableID).stream()
                .map(this::convertToSimpleOrderDTO)
                .collect(Collectors.toList());
    }

    private SimpleWaiterDTO convertToSimpleOrderDTO(User user) {
        if (user == null) return null;
        return SimpleWaiterDTO.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .build();
    }

}
