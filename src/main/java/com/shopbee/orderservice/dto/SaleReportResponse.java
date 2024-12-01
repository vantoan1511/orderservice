package com.shopbee.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleReportResponse {
    private ReportPeriod period;
    private Integer year;
    private List<String> labels;
    private List<BigDecimal> data;

    public SaleReportResponse(Builder builder) {
        this.period = builder.period;
        this.year = builder.year;
        this.labels = builder.labels;
        this.data = builder.data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ReportPeriod period;
        private Integer year;
        private List<String> labels;
        private List<BigDecimal> data;

        public Builder period(ReportPeriod period) {
            this.period = period;
            return this;
        }

        public Builder year(Integer year) {
            this.year = year;
            return this;
        }

        public Builder labels(List<String> labels) {
            this.labels = labels;
            return this;
        }

        public Builder data(List<BigDecimal> data) {
            this.data = data;
            return this;
        }

        public SaleReportResponse build() {
            return new SaleReportResponse(this);
        }
    }
}
