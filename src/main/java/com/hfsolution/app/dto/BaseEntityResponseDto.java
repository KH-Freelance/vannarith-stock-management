package com.hfsolution.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import static com.hfsolution.app.constant.AppResponseCode.*;
import static com.hfsolution.app.constant.AppResponseStatus.*;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

@Getter
@Setter
public class BaseEntityResponseDto<T> {
  String code = FAIL_CODE;
  String status = FAIL;
  String msgDev;
	@JsonIgnore
	String summaryExecInfo;
 
  T entity;
  Page<T> page;
  List<T> entityList;
  List<Map<String, String>> dataRows;

  @JsonIgnore
  public boolean isSucceed() {
    return status.equals(SUCCESS);
  }
}