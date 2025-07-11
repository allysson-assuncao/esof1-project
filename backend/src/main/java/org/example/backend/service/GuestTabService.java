package org.example.backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.backend.dto.DrillDownOrderDTO;
import org.example.backend.dto.GuestTab.*;
import org.example.backend.dto.Order.FlatOrderDTO;
import org.example.backend.dto.Order.OrderGroupDTO;
import org.example.backend.model.GuestTab;
import org.example.backend.model.LocalTable;
import org.example.backend.model.Order;
import org.example.backend.model.enums.GuestTabStatus;
import org.example.backend.repository.GuestTabRepository;
import org.example.backend.repository.LocalTableRepository;
import org.example.backend.repository.OrderRepository;
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
    private final OrderRepository orderRepository;

    @Autowired
    public GuestTabService(GuestTabRepository guestTapRepository,
                           GuestTabSpecificationService guestTabSpecificationService,
                           LocalTableRepository localTableRepository, OrderRepository orderRepository) {
        this.guestTabRepository = guestTapRepository;
        this.guestTabSpecificationService = guestTabSpecificationService;
        this.localTableRepository = localTableRepository;
        this.orderRepository = orderRepository;
    }

    //Registra nova guest tab
    @Transactional
    public boolean registerGuestTab(GuestTabRequestDTO request) {
        LocalTable table = localTableRepository.findById(request.localTableId())
                .orElse(null);

        if(table==null){
            return false;
        }

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

        List<Long> guestTabIdsOnPage = guestTabPage.getContent().stream()
                .map(GuestTab::getId)
                .toList();

        if (guestTabIdsOnPage.isEmpty()) {
            return guestTabPage.map(gt -> convertToGuestTabDTOWithNestedOrders(gt, Collections.emptyList()));
        }

        List<FlatOrderDTO> flatOrders = this.orderRepository.findFlatOrderDTOsByGuestTabIds(guestTabIdsOnPage);

        Map<Long, List<FlatOrderDTO>> ordersByGuestTabId = flatOrders.stream()
                .collect(Collectors.groupingBy(FlatOrderDTO::guestTabId));

        return guestTabPage.map(guestTab ->
                convertToGuestTabDTOWithNestedOrders(guestTab, ordersByGuestTabId.getOrDefault(guestTab.getId(), Collections.emptyList()))
        );
    }

    private GuestTabDTO convertToGuestTabDTOWithNestedOrders(GuestTab guestTab, List<FlatOrderDTO> flatOrders) {
        if (guestTab == null) return null;

        Map<Long, List<FlatOrderDTO>> subOrdersByParentId = flatOrders.stream()
                .filter(order -> order.parentOrderId() != null)
                .collect(Collectors.groupingBy(FlatOrderDTO::parentOrderId));

        List<FlatOrderDTO> topLevelOrders = flatOrders.stream()
                .filter(order -> order.parentOrderId() == null)
                .toList();

        Map<LocalDateTime, List<FlatOrderDTO>> ordersGroupedByTime = topLevelOrders.stream()
                .collect(Collectors.groupingBy(FlatOrderDTO::orderedTime));

        Set<OrderGroupDTO> orderGroupDTOs = ordersGroupedByTime.entrySet().stream()
                .map(entry -> {
                    LocalDateTime groupTime = entry.getKey();
                    List<FlatOrderDTO> ordersInGroup = entry.getValue();

                    Set<DrillDownOrderDTO> drillDownOrders = ordersInGroup.stream()
                            .map(flatOrder -> convertToDrillDownOrderDTO(flatOrder, subOrdersByParentId))
                            .collect(Collectors.toSet());

                    double groupTotalPrice = drillDownOrders.stream()
                            .mapToDouble(this::calculateOrderTotal)
                            .sum();

                    return new OrderGroupDTO(groupTime, groupTotalPrice, drillDownOrders.size(), drillDownOrders);
                })
                .collect(Collectors.toSet());

        double totalGuestTabPrice = orderGroupDTOs.stream().mapToDouble(OrderGroupDTO::groupTotalPrice).sum();

        return GuestTabDTO.builder()
                .id(guestTab.getId())
                .status(guestTab.getStatus())
                .guestName(guestTab.getGuestName())
                .timeOpened(guestTab.getTimeOpened())
                .timeClosed(guestTab.getTimeClosed())
                .orderGroups(orderGroupDTOs)
                .totalPrice(totalGuestTabPrice)
                .localTableNumber(guestTab.getLocalTable() != null ? guestTab.getLocalTable().getNumber() : 0)
                .build();
    }

    private DrillDownOrderDTO convertToDrillDownOrderDTO(FlatOrderDTO flatOrder, Map<Long, List<FlatOrderDTO>> subOrdersMap) {
        if (flatOrder == null) return null;

        Set<DrillDownOrderDTO> additionalOrderDTOs = subOrdersMap.getOrDefault(flatOrder.id(), Collections.emptyList()).stream()
                .map(child -> convertToDrillDownOrderDTO(child, subOrdersMap))
                .collect(Collectors.toSet());

        return DrillDownOrderDTO.builder()
                .id(flatOrder.id())
                .amount(flatOrder.amount())
                .status(flatOrder.status())
                .observation(flatOrder.observation())
                .orderedTime(flatOrder.orderedTime())
                .additionalOrders(additionalOrderDTOs)
                .productName(flatOrder.productName())
                .productUnitPrice(flatOrder.productUnitPrice())
                .waiterName(flatOrder.waiterName())
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
