package com.hfsolution.feature.user.controller;

import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.feature.user.dto.RoleRequest;
import com.hfsolution.feature.user.entity.Permission;
import com.hfsolution.feature.user.entity.Role;
import com.hfsolution.feature.user.entity.User;
import com.hfsolution.feature.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/add")
    public ResponseEntity<?>  createRole(
            @RequestBody RoleRequest request) {
        SuccessResponse<Object> successResponse =  new SuccessResponse<>();
        Role createdRole = roleService.createRole(request.getName(), request.getDescription(), request.getPermissionIds());
        successResponse.setData(createdRole);
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchRoles(
        @RequestParam(required = false) String  q,
        @RequestParam(defaultValue = "1") int pageNo,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "ASC") Sort.Direction sort,
        @RequestParam(defaultValue = "id") String sortByColum) {
        SuccessResponse<Object> successResponse =  new SuccessResponse<>();
        successResponse.setData(roleService.search(q, pageNo, pageSize, sort, sortByColum));
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }


    @GetMapping("/permissions/{id}")
    public ResponseEntity<?>  searchRoles (@PathVariable Long id)  {
        Role role = roleService.getPermission(id);
        SuccessResponse<Object> successResponse =  new SuccessResponse<>();
        successResponse.setData(role);
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?>  deleteRole(@PathVariable Long id) {
        roleService.delete(id);
        SuccessResponse<Object> successResponse =  new SuccessResponse<>();
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }

    @PutMapping("/{id}/add-permissions")
    public ResponseEntity<?>  addPermissions(
            @PathVariable Long id,
            @RequestBody List<Long> permissionIds) {
        Role updatedRole = roleService.addPermission(id, permissionIds);
        SuccessResponse<Object> successResponse =  new SuccessResponse<>();
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setData(updatedRole);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }

    @PutMapping("/{id}/remove-permissions")
    public ResponseEntity<?>  removePermissions(
            @PathVariable Long id,
            @RequestBody List<Long> permissionIds) {
        Role updatedRole = roleService.removePermissionsFromRole(id, permissionIds);
        SuccessResponse<Object> successResponse =  new SuccessResponse<>();
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setData(updatedRole);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/permissions/search")
    public ResponseEntity<?>  searchPermissions(
        @RequestParam(required = false) String  q,
        @RequestParam(defaultValue = "1") int pageNo,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "ASC") Sort.Direction sort,
        @RequestParam(defaultValue = "id") String sortByColum) {
        Page<Permission> permissions = roleService.searchPermission(q, pageNo, pageSize, sort, sortByColum);
        SuccessResponse<Object> successResponse =  new SuccessResponse<>();
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setData(permissions);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }
}
