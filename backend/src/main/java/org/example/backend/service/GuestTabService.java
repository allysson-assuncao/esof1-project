package org.example.backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.backend.dto.*;
import org.example.backend.model.GuestTab;
import org.example.backend.model.LocalTable;
import org.example.backend.model.Order;
import org.example.backend.model.enums.GuestTabStatus;
import org.example.backend.repository.GuestTabRepository;
import org.example.backend.repository.LocalTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GuestTabService {

    private final GuestTabRepository guestTabRepository;
    private final GuestTabSpecificationService guestTabSpecificationService;
    private final LocalTableRepository localTableRepository;

    @Autowired
    public GuestTabService(GuestTabRepository guestTapRepository,
                           GuestTabSpecificationService guestTabSpecificationService,
                           LocalTableRepository localTableRepository) {
        this.guestTabRepository = guestTapRepository;
        this.guestTabSpecificationService = guestTabSpecificationService;
        this.localTableRepository = localTableRepository;
    }

    //Registra nova guest tab
    @Transactional
    public boolean registerGuestTab(GuestTabRequestDTO request){
        LocalTable table = localTableRepository.findByNumber(request.tableNumber())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Table number " + request.tableNumber() + " not found."
        ));

        GuestTab guestTab = GuestTab.builder()
                .name(request.guestName())
                .localTable(table)
                .status(GuestTabStatus.OPEN)
                .timeOpened(LocalDateTime.now())
                .build();

        guestTabRepository.save(guestTab);
        return true;
    }

    //Retorna todas as GuestTabs
    @Transactional
    public List<GuestTabGetDTO> getGuestTabs() {
        return guestTabRepository.findAll().stream().map(GuestTabGetDTO::new).toList();
    }

    //Retorna todas as GuestTabs relacionadas a uma determinada mesa
    @Transactional
    public List<GuestTabGetDTO> getGuestTabsByTableNumber(int tableNumber) {
        List<GuestTabGetDTO> result = guestTabRepository.findByLocalTable(
                localTableRepository.findByNumber(tableNumber).stream().findFirst().orElse(null)
        ).stream().map(x -> new GuestTabGetDTO(
                x.getId(),
                x.getName(),
                x.getTimeOpened(),
                x.getLocalTable().getNumber()
        )).toList();

        return result;
    }

    /*public List<GuestTabGetDTO> getGuestTabsByTableNumber(int tableNumber) {
        return guestTabRepository.get.stream().map(x -> new GuestTabGetDTO(
                x.getId(),
                x.getName(),
                x.getTimeOpened(),
                x.getLocalTable().getNumber())).toList();
    }*/

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
                //.waiterName(waiterName)
                .build();
    }

}
