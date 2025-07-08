package org.example.backend.dto.Order;

import lombok.Builder;
import org.example.backend.dto.FilteredPageDTO;

@Builder
public record KanbanOrdersDTO(FilteredPageDTO<FilteredOrderKanbanDTO> sentOrders, FilteredPageDTO<FilteredOrderKanbanDTO> inPrepareOrders, FilteredPageDTO<FilteredOrderKanbanDTO> readyOrders) { }
