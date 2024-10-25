package com.shopbee.orderservice.repository;

import com.shopbee.orderservice.entity.Order;
import com.shopbee.orderservice.shared.enums.OrderStatus;
import com.shopbee.orderservice.shared.filter.FilterCriteria;
import com.shopbee.orderservice.shared.page.PageRequest;
import com.shopbee.orderservice.shared.sort.SortCriteria;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {

    public Optional<Order> findByIdAndUsername(Long id, String username) {
        return find("id = ?1 AND username = ?2", id, username).stream().findFirst();
    }

    public List<Order> findByCriteria(String username,
                                      FilterCriteria filterCriteria,
                                      PageRequest pageRequest,
                                      SortCriteria sortCriteria) {
        String query = buildQuery(filterCriteria);
        Parameters parameters = buildParameters(username, filterCriteria);

        Sort.Direction direction = sortCriteria.isAscending() ? Sort.Direction.Ascending : Sort.Direction.Descending;
        Sort sort = Sort.by(sortCriteria.getSortBy().getColumn(), direction);

        return find(query, sort, parameters).page(pageRequest.getPage() - 1, pageRequest.getSize()).list();
    }

    public long countBy(String username, FilterCriteria filterCriteria) {
        String query = buildQuery(filterCriteria);
        Parameters parameters = buildParameters(username, filterCriteria);
        return count(query, parameters);
    }

    private String buildQuery(FilterCriteria filterCriteria) {
        String query = "username = :username";
        String keyword = Optional.ofNullable(filterCriteria).map(FilterCriteria::getKeyword).orElse(null);
        OrderStatus status = Optional.ofNullable(filterCriteria).map(FilterCriteria::getStatus).orElse(null);
        if (status != null) {
            query += " AND orderStatus = :status";
        }

        if (StringUtils.isNotBlank(keyword)) {
            query += " AND (lower(username) like :keyword or lower(shippingAddress) like :keyword or totalAmount = :amount)";
        }
        return query;
    }

    private Parameters buildParameters(String username, FilterCriteria filterCriteria) {
        Parameters parameters = Parameters.with("username", username);
        OrderStatus status = Optional.ofNullable(filterCriteria).map(FilterCriteria::getStatus).orElse(null);
        String keyword = Optional.ofNullable(filterCriteria).map(FilterCriteria::getKeyword).orElse(null);
        if (status != null) {
            parameters.and("status", status);
        }

        if (StringUtils.isNotBlank(keyword)) {
            parameters.and("keyword", "%" + keyword.toLowerCase() + "%");
            try {
                double amount = Double.parseDouble(keyword);
                parameters.and("amount", amount);
            } catch (NumberFormatException e) {
                parameters.and("amount", -1);
            }
        }
        return parameters;
    }
}
