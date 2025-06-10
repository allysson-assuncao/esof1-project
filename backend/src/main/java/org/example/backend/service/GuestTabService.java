package org.example.backend.service;

import org.example.backend.dto.GuestTabDTO;
import org.example.backend.dto.GuestTabFilterDTO;
import org.example.backend.dto.OrderDTO;
import org.example.backend.dto.SimpleGuestTabDTO;
import org.example.backend.model.GuestTab;
import org.example.backend.model.Order;
import org.example.backend.repository.GuestTabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GuestTabService {

    private final GuestTabRepository guestTabRepository;
    private final GuestTabSpecificationService guestTabSpecificationService;

    @Autowired
    public GuestTabService(GuestTabRepository guestTapRepository, GuestTabSpecificationService guestTabSpecificationService) {
        this.guestTabRepository = guestTapRepository;
        this.guestTabSpecificationService = guestTabSpecificationService;
    }

    // Todo...
    public boolean registerGuestTap(String request){
        return false;
    }

    public List<SimpleGuestTabDTO> selectAllGuestTabs(UUID localTableID){
        return this.guestTabRepository.findByLocalTableId(localTableID).stream()
                .map(this::convertToSimpleGuestTabDTO)
                .collect(Collectors.toList());
    }

    private SimpleGuestTabDTO convertToSimpleGuestTabDTO(GuestTab guestTab) {
        if (guestTab == null) return null;
        return SimpleGuestTabDTO.builder()
                .id(guestTab.getId())
                .clientName(guestTab.getClientName())
                .build();
    }

    public Page<GuestTabDTO> getGuestTabByFilters(GuestTabFilterDTO filterDto, int page, int size, String orderBy, Sort.Direction direction) {
        Specification<GuestTab> specification = this.guestTabSpecificationService.getGuestTabSpecification(filterDto);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, orderBy));
        Page<GuestTab> guestTabPage = this.guestTabRepository.findAll(specification, pageable);
        System.out.println(guestTabPage.getTotalElements());

        return guestTabPage.map(this::convertToGuestTabDTO);
    }

    private GuestTabDTO convertToGuestTabDTO(GuestTab guestTab) {
        if (guestTab == null) return null;

        // Map orders to OrderDTO
        Set<OrderDTO> orderDTOs = guestTab.getOrders() != null
                ? guestTab.getOrders().stream()
                    .map(this::convertToOrderDTO)
                    .collect(Collectors.toSet())
                : Set.of();

        double totalPrice = orderDTOs.stream()
                .mapToDouble(orderDTO -> orderDTO.productUnitPrice() * orderDTO.amount())
                .sum();

        int localTableNumber = guestTab.getLocalTable() != null ? guestTab.getLocalTable().getNumber() : 0;

        return GuestTabDTO.builder()
                .id(guestTab.getId())
                .status(guestTab.getStatus())
                .timeOpened(guestTab.getTimeOpened())
                .timeClosed(guestTab.getTimeClosed())
                .orders(orderDTOs)
                .totalPrice(totalPrice)
                .localTableNumber(localTableNumber)
                .build();
    }

    private OrderDTO convertToOrderDTO(Order order) {
        if (order == null) return null;

        // Map additionalOrders to their IDs
        /*Set<Long> additionalOrderIds = order.getAdditionalOrders() != null
                ? order.getAdditionalOrders().stream()
                    .map(Order::getId)
                    .collect(Collectors.toSet())
                : Set.of();*/

        String productName = order.getProduct() != null ? order.getProduct().getName() : null;
        double productUnitPrice = order.getProduct() != null ? order.getProduct().getPrice() : 0.0;
        String waiterName = order.getWaiter() != null ? order.getWaiter().getName() : null;

        return OrderDTO.builder()
                .id(order.getId())
                .amount(order.getAmount())
                .status(order.getStatus())
                .observation(order.getObservation())
                .orderedTime(order.getOrderedTime())
                /*.additionalOrders(additionalOrderIds)*/
                .productName(productName)
                .productUnitPrice(productUnitPrice)
                .waiterName(waiterName)
                .build();
    }

}
