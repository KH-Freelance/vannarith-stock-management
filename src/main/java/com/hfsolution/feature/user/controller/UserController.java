package com.hfsolution.feature.user.controller;


import lombok.RequiredArgsConstructor;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hfsolution.app.dto.SearchRequestDTO;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.feature.auth.dto.AuthenticationResponse;
import com.hfsolution.feature.auth.dto.RegisterRequest;
import com.hfsolution.feature.auth.services.AuthenticationService;
import com.hfsolution.feature.user.dto.ChangePasswordRequest;
import com.hfsolution.feature.user.dto.ChangeRoleRequest;
import com.hfsolution.feature.user.dto.ResetPasswordRequest;
import com.hfsolution.feature.user.entity.User;
import com.hfsolution.feature.user.service.UserService;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final AuthenticationService authService;
    private final UserService userService;
    private final UserService service;

    //ADMIN
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('admin:update')")
    @PutMapping("/change-role/{userId}")
    public ResponseEntity<?> changeRole(
          @RequestBody ChangeRoleRequest request,
          @PathVariable(value = "userId") Integer userId
    ) {
        userService.changeRole(request, userId);
        SuccessResponse<?> successResponse =  new SuccessResponse<>();
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }


    //MANAGER
    @PostMapping("/register")
    public ResponseEntity<?> register(
        @RequestBody RegisterRequest request
    ) {
        SuccessResponse<AuthenticationResponse> successResponse =  new SuccessResponse<>();
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setData(authService.register(request));
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }

    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<?> deleteUser(
          @PathVariable Integer userId
    ) {
        userService.deleteUser(userId);
        SuccessResponse<?> successResponse =  new SuccessResponse<>();
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }

    @PutMapping("/reset-password/{userId}")
    public ResponseEntity<?> resetPassword(
        @PathVariable(value = "userId") Integer userId, @RequestBody ResetPasswordRequest request
    ) {
        userService.resetPassword(request,userId);
        SuccessResponse<?> successResponse =  new SuccessResponse<>();
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }

    @PostMapping("/search")
    public ResponseEntity<?> getUsersInfo(@RequestBody SearchRequestDTO request) {
        SuccessResponse<Page<User>> successResponse =  new SuccessResponse<>();
        successResponse.setData(userService.searchUser(request));
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }

    //USER
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
          @RequestBody ChangePasswordRequest request,
          Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        SuccessResponse<?> successResponse =  new SuccessResponse<>();
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/current-info")
    public ResponseEntity<?> getInfo(
          Principal connectedUser
    ) {
        SuccessResponse<User> successResponse =  new SuccessResponse<>();
        successResponse.setData(service.getInfo(connectedUser));
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    // @GetMapping
    // // @PreAuthorize("hasAuthority('admin:read')")
    // public String get() {
    //     return "GET:: admin controller";
    // }

    // @PostMapping
    // // @PreAuthorize("hasAuthority('admin:create')")
    // @Hidden
    // public String post() {
    //     return "POST:: admin controller";
    // }
    // @PutMapping
    // // @PreAuthorize("hasAuthority('admin:update')")
    // @Hidden
    // public String put() {
    //     return "PUT:: admin controller";
    // }
    // @DeleteMapping
    // // @PreAuthorize("hasAuthority('admin:delete')")
    // @Hidden
    // public String delete() {
    //     return "DELETE:: admin controller";
    // }
}