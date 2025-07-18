package org.example.backend.repository;

import org.example.backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository  extends JpaRepository<Payment, Long> {

    List<Payment> findByGuestTabIdIn(List<Long> guestTabIds);

}
