package com.hfsolution.feature.stockmanagement.dto.returns;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hfsolution.app.util.BigDecimalSerializer;
import com.hfsolution.feature.stockmanagement.dto.user.User;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReturnDetailDto {

    private Long id;

    private String sourcePurchaseCode;

    private String targetPurchaseCode;

    private String returnType;

    private List<ReturnItemDto> returnItemDtos = new ArrayList<>();

    private User user; 

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM dd, yyyy h:mm a")
    private Timestamp createdDate;


}
