package com.shopbee.orderservice.service.impl;

import com.shopbee.orderservice.dto.ReportPeriod;
import com.shopbee.orderservice.dto.SaleReportRequest;
import com.shopbee.orderservice.dto.SaleReportResponse;
import com.shopbee.orderservice.entity.Order;
import com.shopbee.orderservice.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ApplicationScoped
public class ReportService {

    private final OrderRepository orderRepository;

    @Inject
    public ReportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public SaleReportResponse getSaleReport(SaleReportRequest saleReportRequest) {
        ReportPeriod period = Optional.ofNullable(saleReportRequest)
                .map(SaleReportRequest::getPeriod)
                .map(ReportPeriod::fromCode)
                .orElse(ReportPeriod.MONTHLY);
        int year = Optional.ofNullable(saleReportRequest).map(SaleReportRequest::getYear).orElse(Year.now().getValue());
        List<Order> orders = orderRepository.findAllCompletedByYear(year);
        List<BigDecimal> salesData;
        switch (period) {
            case DAILY -> salesData = getDailyTotalAmounts(orders, year);
            case WEEKLY -> salesData = getWeeklyTotalAmounts(orders, year);
            case QUARTERLY -> salesData = getQuarterlyTotalAmounts(orders);
            default -> salesData = getMonthlyTotalAmounts(orders);
        }
        return SaleReportResponse.builder()
                .period(period)
                .labels(getLabels(period))
                .year(year)
                .data(salesData)
                .build();
    }

    public List<BigDecimal> getDailyTotalAmounts(List<Order> orders, int year) {
        Map<Integer, BigDecimal> dailyTotals = IntStream.rangeClosed(1, 365)
                .boxed()
                .collect(Collectors.toMap(day -> day, day -> BigDecimal.ZERO, (a, b) -> b, LinkedHashMap::new));

        orders.forEach(order -> {
            int dayOfYear = order.getCreatedAt().getDayOfYear();
            dailyTotals.put(dayOfYear, dailyTotals.get(dayOfYear).add(order.getTotalAmount()));
        });

        return new ArrayList<>(dailyTotals.values()); // Returns values in day order
    }

    public List<BigDecimal> getWeeklyTotalAmounts(List<Order> orders, int year) {
        Map<Integer, BigDecimal> weeklyTotals = IntStream.rangeClosed(1, 52)
                .boxed()
                .collect(Collectors.toMap(week -> week, week -> BigDecimal.ZERO, (a, b) -> b, LinkedHashMap::new));
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        orders.forEach(order -> {
            int weekOfYear = order.getCreatedAt().get(weekFields.weekOfYear());
            weeklyTotals.put(weekOfYear, weeklyTotals.get(weekOfYear).add(order.getTotalAmount()));
        });

        return new ArrayList<>(weeklyTotals.values());
    }

    public List<BigDecimal> getMonthlyTotalAmounts(List<Order> orders) {
        Map<Month, BigDecimal> monthlyTotals = Arrays.stream(Month.values())
                .collect(Collectors.toMap(month -> month, month -> BigDecimal.ZERO, (a, b) -> b, LinkedHashMap::new));

        orders.forEach(order -> {
            Month month = order.getCreatedAt().getMonth();
            monthlyTotals.put(month, monthlyTotals.get(month).add(order.getTotalAmount()));
        });

        return Arrays.stream(Month.values())
                .map(monthlyTotals::get)
                .collect(Collectors.toList());
    }

    public List<BigDecimal> getQuarterlyTotalAmounts(List<Order> orders) {
        Map<Integer, BigDecimal> quarterlyTotals = IntStream.rangeClosed(1, 4)
                .boxed()
                .collect(Collectors.toMap(quarter -> quarter, quarter -> BigDecimal.ZERO, (a, b) -> b, LinkedHashMap::new));

        orders.forEach(order -> {
            int quarter = (order.getCreatedAt().getMonthValue() - 1) / 3 + 1;
            quarterlyTotals.put(quarter, quarterlyTotals.get(quarter).add(order.getTotalAmount()));
        });

        return IntStream.rangeClosed(1, 4)
                .mapToObj(quarterlyTotals::get)
                .collect(Collectors.toList());
    }

    private List<String> getLabels(ReportPeriod period) {
        return switch (period) {
            case DAILY -> generateDailyLabels();
            case WEEKLY -> generateWeeklyLabels();
            case MONTHLY -> List.of("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC");
            case QUARTERLY -> List.of("Q1", "Q2", "Q3", "Q4");
        };
    }

    private List<String> generateDailyLabels() {
        List<String> dailyLabels = new ArrayList<>();
        for (int day = 1; day <= 365; day++) {
            dailyLabels.add(String.valueOf(day));
        }
        return dailyLabels;
    }

    private List<String> generateWeeklyLabels() {
        List<String> weeklyLabels = new ArrayList<>();
        for (int week = 1; week <= 52; week++) {
            weeklyLabels.add("W" + week);
        }
        return weeklyLabels;
    }
}
