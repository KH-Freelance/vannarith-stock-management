package com.hfsolution.feature.stockmanagement.dto.report;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class ReportPurchase {
    List<String> columns;
    List<HashMap<String, Object>> content;
    Map<String, BigDecimal> totals = new HashMap<>();
}
