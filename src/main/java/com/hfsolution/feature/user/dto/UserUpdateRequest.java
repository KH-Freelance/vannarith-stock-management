package com.hfsolution.feature.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    @NotBlank(message = "firstname is required.")
    private String firstname;
    @NotBlank(message = "lastname is required.")
    private String lastname;

}