package com.hfsolution.feature.stockmanagement.dto.report;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportReturnDto {
    String [] columns = new String[]{"Sales","Customer","Type","Returned To Sales","Returned By","Returned At"};
    List<ReturnDto> content = new ArrayList<>();
}
