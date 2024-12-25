package com.hfsolution.feature.stockmanagement.dto.report;


import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReportSaleDto {

   List<String> columns = new ArrayList<>();
   List<SaleDto> content = new ArrayList<>();

}
