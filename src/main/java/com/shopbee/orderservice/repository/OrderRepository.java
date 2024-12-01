package com.shopbee.orderservice.repository;

import com.shopbee.orderservice.entity.Order;
import com.shopbee.orderservice.shared.enums.OrderStatus;
import com.shopbee.orderservice.shared.enums.PaymentMethod;
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

    public List<Order> findCashPaidCompletedByYear(int year) {
        return find("paymentMethod = ?1 and orderStatus = ?2 and extract(year from createdAt) = ?3", PaymentMethod.CASH, OrderStatus.COMPLETED, year).list();
    }

    public List<Order> findAllByYear(int year) {
        return find("extract(year from createdAt) = ?1", year).list();
    }

    public List<Order> findAllCompletedByYear(int year) {
        return find("orderStatus = ?1 and extract(year from createdAt) = ?2", OrderStatus.COMPLETED, year).list();
    }

    public List<Order> findByCriteria(FilterCriteria filterCriteria,
                                      PageRequest pageRequest,
                                      SortCriteria sortCriteria) {
        Map<String, Object> parameters = new HashMap<>();
        String query = buildDynamicQuery(filterCriteria, parameters);

        return find(query, sortBy(sortCriteria), parameters)
                .page(pageRequest.getPage() - 1, pageRequest.getSize())
                .list();
    }

    public long countBy(String username, FilterCriteria filterCriteria) {
        Map<String, Object> parameters = new HashMap<>();
        String query = buildDynamicQuery(filterCriteria, parameters);
        return count(query, parameters);
    }

    private String buildDynamicQuery(FilterCriteria filterCriteria,
                                     Map<String, Object> params) {
        List<String> clauses = new ArrayList<>();
        clauses.add("1=1");

        if (filterCriteria == null) {
            return String.join(" AND ", clauses);
        }

        if (StringUtils.isNotBlank(filterCriteria.getUsername())) {
            clauses.add("username = :username");
            params.put("username", filterCriteria.getUsername());
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
