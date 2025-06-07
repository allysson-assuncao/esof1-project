package org.example.backend.service;

import org.example.backend.dto.GuestTabDTO;
import org.example.backend.dto.GuestTabFilterDTO;
import org.example.backend.model.GuestTab;
import org.example.backend.repository.GuestTabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class GuestTabService {

    private final GuestTabRepository guestTabRepository;
    private final GuestTabSpecificationService guestTabSpecificationService;

    @Autowired
    public GuestTabService(GuestTabRepository guestTabRepository, GuestTabSpecificationService guestTabSpecificationService) {
        this.guestTabRepository = guestTabRepository;
        this.guestTabSpecificationService = guestTabSpecificationService;
    }

    // Todo...
    public boolean registerGuestTap(String request){
        return false;
    }

    /*public Page<GuestTabDTO> getGuestTabByFilters(GuestTabFilterDTO filterDto, int page, int size, String orderBy, Sort.Direction direction) {
        Specification<GuestTab> specification = this.guestTabSpecificationService.getGuestTabSpecification(filterDto);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, orderBy));
        Page<GuestTab> guestTabPage = this.guestTabRepository.findAll(specification, pageable);
        System.out.println(guestTabPage.getTotalElements());

        return guestTabPage.map(this::convertToGuestTabDTO);
    }

    private GuestTabDTO convertToGuestTabDTO(GuestTab guestTab) {
        if (guestTab == null) return null;
        return GuestTab.builder()
                .orderId(guestTab.getId())
                .amount(guestTab.getAmount())
                .observation(guestTab.getObservation())
                .orderStatus(guestTab.getStatus() != null ? guestTab.getStatus() : null)
                .orderedTime(guestTab.getOrderedTime())
                .totalPrice(guestTab.getProduct() != null ? guestTab.getAmount() * guestTab.getProduct().getPrice() : 0.0)
                *//*.additionalOrders(
                        order.getAdditionalOrders() != null && !order.getAdditionalOrders().isEmpty()
                                ? order.getAdditionalOrders().stream()
                                .map(Order::getId)
                                .collect(Collectors.toSet())
                                : null
                )*//*
                .productName(guestTab.getProduct() != null ? guestTab.getProduct().getName() : null)
                .productUnitPrice(guestTab.getProduct() != null ? guestTab.getProduct().getPrice() : 0.0)
                .guestTabId(guestTab.getGuestTab() != null ? guestTab.getGuestTab().getId() : null)
                .guestTabStatus(guestTab.getGuestTab() != null && guestTab.getGuestTab().getStatus() != null ? guestTab.getGuestTab().getStatus() : null)
                .guestTabTimeOpened(guestTab.getGuestTab() != null ? guestTab.getGuestTab().getTimeOpened() : null)
                .waiterName(guestTab.getWaiter() != null ? guestTab.getWaiter().getName() : null)
                .localTableNumber(guestTab.getGuestTab() != null && guestTab.getGuestTab().getLocalTable() != null ? guestTab.getGuestTab().getLocalTable().getNumber() : 0)
                .build();
    }*/

}
