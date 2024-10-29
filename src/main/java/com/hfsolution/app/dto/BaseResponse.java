package com.hfsolution.app.dto;

import static com.hfsolution.app.constant.AppResponseStatus.FAIL;
import com.hfsolution.app.util.AppTools;
import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseResponse {

    private String code = FAIL_CODE;
    private String msg = SUCCESS;
    private String status = FAIL;
    private String txnDate = AppTools.getCurrentDateString();

}
