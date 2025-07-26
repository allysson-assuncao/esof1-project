package org.example.backend.repository;

import org.example.backend.dto.Payment.PaymentGroupProjection;
import org.example.backend.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    List<Payment> findByGuestTabIdIn(List<Long> guestTabIds);

    @Query(
            value = """
                    SELECT
                        CAST(p.created_at AS DATE) -
                            (CASE WHEN CAST(p.created_at AS TIME) < CAST(:businessDayStart AS TIME) THEN 1 ELSE 0 END) * INTERVAL '1 day' AS businessDay,
                        SUM(p.total_amount) AS totalAmount,
                        COUNT(p.id) AS paymentCount
                    FROM
                        payments p
                    WHERE
                        (CAST(:startDate AS timestamp) IS NULL OR p.created_at >= :startDate)
                        AND (CAST(:endDate AS timestamp) IS NULL OR p.created_at <= :endDate)
                    GROUP BY
                        businessDay
                    """,
            countQuery = """
                    SELECT COUNT(*) FROM (
                        SELECT
                            1
                        FROM
                            payments p
                        WHERE
                            (CAST(:startDate AS timestamp) IS NULL OR p.created_at >= :startDate)
                            AND (CAST(:endDate AS timestamp) IS NULL OR p.created_at <= :endDate)
                        GROUP BY
                            CAST(p.created_at AS DATE) - (CASE WHEN CAST(p.created_at AS TIME) < CAST(:businessDayStart AS TIME) THEN 1 ELSE 0 END) * INTERVAL '1 day'
                    ) AS grouped_payments
                    """,
            nativeQuery = true
    )
    Page<PaymentGroupProjection> findGroupedPayments(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("businessDayStart") String businessDayStart,
            Pageable pageable
    );

    @Query(
            value = """
                    SELECT
                        p.*
                    FROM
                        payments p
                    WHERE
                        (CAST(p.created_at AS DATE) - (CASE WHEN CAST(p.created_at AS TIME) < CAST(:businessDayStart AS TIME) THEN 1 ELSE 0 END) * INTERVAL '1 day')
                        IN (:businessDays)
                    AND (CAST(:startDate AS timestamp) IS NULL OR p.created_at >= :startDate)
                    AND (CAST(:endDate AS timestamp) IS NULL OR p.created_at <= :endDate)
                    """,
            nativeQuery = true
    )
    List<Payment> findPaymentsByBusinessDays(
            @Param("businessDays") List<LocalDate> businessDays,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("businessDayStart") LocalTime businessDayStart
    );

}
