package com.hfsolution.feature.stockmanagement.dto.report;


import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReportSaleDto {

   String[] columns = new String[]{"Type","Date","Purchase Code","Customer","Product","INN","Phone","Location","Qty","Sales Price","Total Amount"};
   List<SaleDto> content = new ArrayList<>();

}
