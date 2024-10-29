package com.hfsolution.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import java.util.List;
import com.hfsolution.app.enums.GlobalOperator;
import lombok.AccessLevel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchRequestDTO {
    List<SearchRequest> searchRequest;
    GlobalOperator globalOperator = GlobalOperator.OR;
    PageRequestDto pageRequestDto = new PageRequestDto();
}