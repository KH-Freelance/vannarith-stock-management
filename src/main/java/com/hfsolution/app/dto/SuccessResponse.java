package com.hfsolution.app.dto;

import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;

import java.util.List;

import com.hfsolution.feature.user.entity.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SuccessResponse<T> extends BaseResponse{
    T data;
    @Override
    public String getStatus() {
        return SUCCESS;
    }
}