package com.shopbee.orderservice.repository;

import com.shopbee.orderservice.entity.Order;
import com.shopbee.orderservice.shared.filter.FilterCriteria;
import com.shopbee.orderservice.shared.page.PageRequest;
import com.shopbee.orderservice.shared.sort.SortCriteria;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {

    public Optional<Order> findByIdAndUsername(Long id, String username) {
        return findByIdOptional(id).filter(order -> order.getUsername().equals(username));
    }

    public List<Order> findByCriteria(String username,
                                      FilterCriteria filterCriteria,
                                      PageRequest pageRequest,
                                      SortCriteria sortCriteria) {
        String query = buildQuery(username, filterCriteria);
        Parameters parameters = buildParameters(username, filterCriteria);

        Sort.Direction direction = sortCriteria.isAscending() ? Sort.Direction.Ascending : Sort.Direction.Descending;
        Sort sort = Sort.by(sortCriteria.getSortBy().getColumn(), direction);

        return find(query, sort, parameters).page(pageRequest.getPage() - 1, pageRequest.getSize()).list();
    }

    public long countBy(String username, FilterCriteria filterCriteria) {
        String query = buildQuery(username, filterCriteria);
        Parameters parameters = buildParameters(username, filterCriteria);
        return count(query, parameters);
    }

    private String buildQuery(String username, FilterCriteria filterCriteria) {
        StringBuilder query = new StringBuilder("1=1");

        if (StringUtils.isNotBlank(username)) {
            query.append(" AND username = :username");
        }

        if (filterCriteria == null) {
            return query.toString();
        }

        if (filterCriteria.getStatus() != null) {
            query.append(" AND orderStatus = :status");
        }

        if (StringUtils.isNotBlank(filterCriteria.getKeyword())) {
            query.append(" AND (lower(username) like :keyword OR lower(shippingAddress) like :keyword OR totalAmount = :amount)");
        }

        return query.toString();
    }

    private Parameters buildParameters(String username, FilterCriteria filterCriteria) {
        Parameters parameters = Parameters.with("username", username);
        if (filterCriteria == null) {
            return parameters;
        }

        Optional.ofNullable(filterCriteria.getStatus()).ifPresent((status) -> parameters.and("status", status));
        String keyword = filterCriteria.getKeyword();

        if (StringUtils.isNotBlank(keyword)) {
            parameters.and("keyword", "%" + keyword.toLowerCase() + "%");

            try {
                parameters.and("amount", Double.parseDouble(keyword));
            } catch (NumberFormatException e) {
                parameters.and("amount", -1);
            }
        }

        return parameters;
    }
}
