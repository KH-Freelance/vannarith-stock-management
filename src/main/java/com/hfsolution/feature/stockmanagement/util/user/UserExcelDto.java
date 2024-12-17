package com.hfsolution.feature.stockmanagement.util.user;

import com.hfsolution.feature.user.entity.User;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class UserExcelDto extends User{
    String roleName;
}
