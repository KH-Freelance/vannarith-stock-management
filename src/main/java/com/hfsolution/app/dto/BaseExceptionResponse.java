package com.hfsolution.app.dto;

import static com.hfsolution.app.constant.AppResponseStatus.FAIL;
import com.hfsolution.app.util.AppTools;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseExceptionResponse {


    String code;
    String msg;
    String devMsg;
    String status = FAIL;
    String responseTime = AppTools.getCurrentDateString();
}
