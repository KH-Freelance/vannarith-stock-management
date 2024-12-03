package com.hfsolution.feature.user.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
public class RoleRequest {

    @NotEmpty(message = "Name is required")
    private String name;

    @NotEmpty(message = "Description is required")
    private String description;

    @NotNull(message = "Permission IDs list cannot be null")
    private List<Long> permissionIds;
}
