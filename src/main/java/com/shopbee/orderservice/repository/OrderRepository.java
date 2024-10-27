package com.shopbee.orderservice.repository;

import com.shopbee.orderservice.entity.Order;
import com.shopbee.orderservice.shared.filter.FilterCriteria;
import com.shopbee.orderservice.shared.page.PageRequest;
import com.shopbee.orderservice.shared.sort.SortCriteria;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {

    public Optional<Order> findByIdAndUsername(Long id, String username) {
        return findByIdOptional(id).filter(order -> order.getUsername().equals(username));
    }

    public List<Order> findByCriteria(String username,
                                      FilterCriteria filterCriteria,
                                      PageRequest pageRequest,
                                      SortCriteria sortCriteria) {
        Map<String, Object> parameters = new HashMap<>();
        String query = buildDynamicQuery(username, filterCriteria, parameters);

        return find(query, sortBy(sortCriteria), parameters)
                .page(pageRequest.getPage() - 1, pageRequest.getSize())
                .list();
    }

    public long countBy(String username, FilterCriteria filterCriteria) {
        Map<String, Object> parameters = new HashMap<>();
        String query = buildDynamicQuery(username, filterCriteria, parameters);
        return count(query, parameters);
    }

    private String buildDynamicQuery(String username,
                                     FilterCriteria filterCriteria,
                                     Map<String, Object> params) {
        List<String> clauses = new ArrayList<>();
        clauses.add("1=1");

        if (StringUtils.isNotBlank(username)) {
            clauses.add("username = :username");
            params.put("username", username);
        }

        if (filterCriteria == null) {
            return String.join(" AND ", clauses);
        }

        if (filterCriteria.getStatus() != null) {
            clauses.add("orderStatus = :status");
            params.put("status", filterCriteria.getStatus());
        }

        if (StringUtils.isNotBlank(filterCriteria.getKeyword())) {
            String keyword = filterCriteria.getKeyword().toLowerCase();
            clauses.add("(lower(username) like :keyword OR lower(shippingAddress) like :keyword)");
            params.put("keyword", "%" + keyword + "%");
        }

        if (StringUtils.isNotBlank(filterCriteria.getProductSlug())) {
            clauses.add("EXISTS (SELECT 1 FROM OrderDetails od WHERE od.order.id = o.id AND od.productSlug = :productSlug)");
            params.put("productSlug", filterCriteria.getProductSlug());
        }

        return "FROM Order o WHERE " + String.join(" AND ", clauses);
    }

    private Sort sortBy(SortCriteria sortCriteria) {
        Sort.Direction direction = sortCriteria.isAscending() ? Sort.Direction.Ascending : Sort.Direction.Descending;
        return Sort.by(sortCriteria.getSortBy().getColumn(), direction);
    }

}
