package org.example.backend.repository;

import org.example.backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PaymentRepository  extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    List<Payment> findByGuestTabIdIn(List<Long> guestTabIds);

}
