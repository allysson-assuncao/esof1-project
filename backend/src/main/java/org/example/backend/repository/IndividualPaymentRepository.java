package org.example.backend.repository;

import org.example.backend.model.IndividualPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndividualPaymentRepository extends JpaRepository<IndividualPayment, Long> {
}
