package com.shopbee.orderservice.shared.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    private long totalItems;
    private int numberOfItems;
    private int page;
    private int size;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<T> items;

    public static <T> PagedResponse<T> of(long totalItems, PageRequest pageRequest, List<T> items) {
        PagedResponse<T> response = new PagedResponse<>();
        response.setTotalItems(totalItems);
        response.setPage(pageRequest.getPage());
        response.setSize(pageRequest.getSize());
        response.setItems(items);
        response.setNumberOfItems(items.size());
        response.setHasPrevious(pageRequest.getPage() > 1);
        response.setHasNext(((long) (pageRequest.getPage() - 1) * pageRequest.getSize() + items.size()) < totalItems);
        return response;
    }
}
