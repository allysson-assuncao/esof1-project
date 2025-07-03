package org.example.backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.backend.dto.DrillDownOrderDTO;
import org.example.backend.dto.GuestTab.*;
import org.example.backend.dto.Order.OrderGroupDTO;
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
import java.util.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
    public boolean registerGuestTab(GuestTabRequestDTO request) {
        LocalTable table = localTableRepository.findById(request.localTableId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Table number " + request.localTableId() + " not found."
                ));

        GuestTab guestTab = GuestTab.builder()
                .guestName(request.guestName())
                .localTable(table)
                .status(GuestTabStatus.OPEN)
                .timeOpened(LocalDateTime.now())
                .build();

        guestTabRepository.save(guestTab);
        return true;
    }

    @Transactional
    public String closeTabById(Long id) {
        GuestTab tab = guestTabRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        StringBuilder output = new StringBuilder();
        double accum = 0.0;
        List<Order> orders = tab.getOrders();
        output.append(tab.getGuestName());
        output.append("\n");
        output.append("Nome         Quantidade          Preço\n");
        for (Order it : orders) {
            output.append(it.getProduct().getName());
            output.append("         ");
            output.append(it.getAmount());
            output.append("         R$");
            output.append(it.getProduct().getPrice());
            output.append("\n");
            accum += it.getProduct().getPrice();
        }
        output.append("\n" + "Preço total: R$ " + accum);
        tab.setStatus(GuestTabStatus.CLOSED);
        return output.toString();
    }

    //Retorna todas as GuestTabs
    @Transactional
    public List<GuestTabGetDTO> getGuestTabs() {
        return guestTabRepository.findAll().stream().map(x -> new GuestTabGetDTO(
                x.getId(),
                x.getGuestName(),
                x.getStatus().name(),
                x.getTimeOpened(),
                x.getLocalTable().getNumber())).toList();
    }

    //Retorna todas as GuestTabs relacionadas a uma determinada mesa
    @Transactional
    public List<GuestTabGetDTO> getGuestTabsByTableNumber(int tableNumber) {
        List<GuestTabGetDTO> result = guestTabRepository.findByLocalTable(
                localTableRepository.findByNumber(tableNumber).stream().findFirst().orElse(null)
        ).stream().map(this::convertGuestTabToGetDTO).collect(Collectors.toList());

        return result;
    }

    public GuestTabGetDTO convertGuestTabToGetDTO(GuestTab guestTab) {
        return GuestTabGetDTO.builder()
                .id(guestTab.getId())
                .tableNumber(guestTab.getLocalTable().getNumber())
                .name(guestTab.getGuestName())
                .status(guestTab.getStatus().name())
                .timeOpened(guestTab.getTimeOpened())
                .build();
    }

    /*public List<GuestTabGetDTO> getGuestTabsByTableNumber(int tableId) {
        return guestTabRepository.get.stream().map(x -> new GuestTabGetDTO(
                x.getId(),
                x.getName(),
                x.getTimeOpened(),
                x.getLocalTable().getNumber())).toList();
    }*/

    public List<SimpleGuestTabDTO> selectGuestTabsByLocalTableId(UUID localTableID) {
        return this.guestTabRepository.findByLocalTableId(localTableID).stream()
                .map(this::convertToSimpleGuestTabDTO)
                .collect(Collectors.toList());
    }

    private SimpleGuestTabDTO convertToSimpleGuestTabDTO(GuestTab guestTab) {
        if (guestTab == null) return null;
        return SimpleGuestTabDTO.builder()
                .id(guestTab.getId())
                .clientName(guestTab.getGuestName())
                .build();
    }

    public Page<GuestTabDTO> getGuestTabByFilters(GuestTabFilterDTO filterDto, int page, int size, String orderBy, Sort.Direction direction) {
        Specification<GuestTab> specification = this.guestTabSpecificationService.getGuestTabSpecification(filterDto);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, orderBy));
        Page<GuestTab> guestTabPage = this.guestTabRepository.findAll(specification, pageable);

        return guestTabPage.map(this::convertToGuestTabDTOWithNestedOrders);
    }

    private GuestTabDTO convertToGuestTabDTOWithNestedOrders(GuestTab guestTab) {
        if (guestTab == null) return null;

        List<Order> allOrders = new ArrayList<>(guestTab.getOrders());

        Map<Long, List<Order>> subOrdersByParentId = allOrders.stream()
                .filter(order -> order.getParentOrder() != null)
                .collect(Collectors.groupingBy(order -> order.getParentOrder().getId()));

        List<Order> topLevelOrders = allOrders.stream()
                .filter(order -> order.getParentOrder() == null)
                .toList();

        Map<LocalDateTime, List<Order>> ordersGroupedByTime = topLevelOrders.stream()
                .collect(Collectors.groupingBy(Order::getOrderedTime));

        Set<OrderGroupDTO> orderGroupDTOs = ordersGroupedByTime.entrySet().stream()
                .map(entry -> {
                    LocalDateTime groupTime = entry.getKey();
                    List<Order> ordersInGroup = entry.getValue();

                    Set<DrillDownOrderDTO> drillDownOrders = ordersInGroup.stream()
                            .map(order -> convertToDrillDownOrderDTO(order, subOrdersByParentId))
                            .collect(Collectors.toSet());

                    double groupTotalPrice = drillDownOrders.stream()
                            .mapToDouble(this::calculateOrderTotal)
                            .sum();

                    return OrderGroupDTO.builder()
                            .representativeTime(groupTime)
                            .groupTotalPrice(groupTotalPrice)
                            .numberOfItems(drillDownOrders.size())
                            .orders(drillDownOrders)
                            .build();
                })
                .collect(Collectors.toSet());

        double totalGuestTabPrice = orderGroupDTOs.stream()
                .mapToDouble(OrderGroupDTO::groupTotalPrice)
                .sum();

        int localTableNumber = guestTab.getLocalTable() != null ? guestTab.getLocalTable().getNumber() : 0;

        return GuestTabDTO.builder()
                .id(guestTab.getId())
                .status(guestTab.getStatus())
                .guestName(guestTab.getGuestName())
                .timeOpened(guestTab.getTimeOpened())
                .timeClosed(guestTab.getTimeClosed())
                .orderGroups(orderGroupDTOs)
                .totalPrice(totalGuestTabPrice)
                .localTableNumber(localTableNumber)
                .build();
    }

    private DrillDownOrderDTO convertToDrillDownOrderDTO(Order order, Map<Long, List<Order>> subOrdersMap) {
        if (order == null) return null;

        List<Order> children = subOrdersMap.getOrDefault(order.getId(), Collections.emptyList());

        Set<DrillDownOrderDTO> additionalOrderDTOs = children.stream()
                .map(child -> convertToDrillDownOrderDTO(child, subOrdersMap))
                .collect(Collectors.toSet());

        String productName = order.getProduct() != null ? order.getProduct().getName() : null;
        double productUnitPrice = order.getProduct() != null ? order.getProduct().getPrice() : 0.0;
        String waiterName = order.getWaiter() != null ? order.getWaiter().getName() : null;

        return DrillDownOrderDTO.builder()
                .id(order.getId())
                .amount(order.getAmount())
                .status(order.getStatus())
                .observation(order.getObservation())
                .orderedTime(order.getOrderedTime())
                .additionalOrders(additionalOrderDTOs)
                .productName(productName)
                .productUnitPrice(productUnitPrice)
                .waiterName(waiterName)
                .build();
    }

    private double calculateOrderTotal(DrillDownOrderDTO orderDTO) {
        double selfTotal = orderDTO.productUnitPrice() * orderDTO.amount();
        double childrenTotal = orderDTO.additionalOrders().stream()
                .mapToDouble(this::calculateOrderTotal)
                .sum();
        return selfTotal + childrenTotal;
    }

}
