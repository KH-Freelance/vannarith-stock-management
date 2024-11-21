package com.hfsolution.feature.user.controller;

import lombok.RequiredArgsConstructor;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.feature.auth.dto.AuthenticationResponse;
import com.hfsolution.feature.auth.services.AuthenticationService;
import com.hfsolution.feature.user.dto.ChangePasswordRequest;
import com.hfsolution.feature.user.dto.ChangeRoleRequest;
import com.hfsolution.feature.user.dto.RegisterRequest;
import com.hfsolution.feature.user.dto.ResetPasswordRequest;
import com.hfsolution.feature.user.dto.UserUpdateRequest;
import com.hfsolution.feature.user.entity.User;
import com.hfsolution.feature.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService authService;
    private final UserService userService;

    //ADMIN
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('admin:update')")
    @PutMapping("/change-role/{id}")
    public ResponseEntity<?> changeRole(
          @RequestBody ChangeRoleRequest request,
          @PathVariable(value = "id") Long id
    ) {
        userService.changeRole(request, id);
        SuccessResponse<?> successResponse =  new SuccessResponse<>();
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }


    //MANAGER
    @PostMapping("/add")
    public ResponseEntity<?> register(
        @RequestBody RegisterRequest request
    ) {
        SuccessResponse<AuthenticationResponse> successResponse =  new SuccessResponse<>();
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setData(authService.register(request));
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }

    @PostMapping(value = "/v2/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> register2(
        @ModelAttribute RegisterRequest request
    ) throws IOException {
        SuccessResponse<AuthenticationResponse> successResponse =  new SuccessResponse<>();
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setData(authService.register(request,request.getFile()));
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(
          @PathVariable Long id
    ) {
        userService.deleteUser(id);
        SuccessResponse<?> successResponse =  new SuccessResponse<>();
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }

    @PutMapping("/reset-password/{id}")
    public ResponseEntity<?> resetPassword(
        @PathVariable(value = "id") Long id, @RequestBody ResetPasswordRequest request
    ) {
        userService.resetPassword(request,id);
        SuccessResponse<?> successResponse =  new SuccessResponse<>();
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/export")
    public void exportData(
        @Parameter(description = "Query string to query resources. Supported query patterns are \"exact match(k=v)\", \"fuzzy match(k=~v)\", \"range(k=[min~max])\", \"list with union releationship(k={v1 v2 v3})\" and \"list with intersetion relationship(k=(v1 v2 v3))\". The value of range and list can be string(enclosed by \" or '), integer or time(in format \"2020-04-09 02:36:00\"). All of these query patterns should be put in the query string \"q=xxx\" and splitted by \",\". e.g. q=k1=v1,k2=~v2,k3=[min~max], Note: q is empty mean query all result.")
        @RequestParam(required = false) String q){
            userService.export(q);
    }

    @PostMapping(value = "/import", consumes = {"multipart/form-data"})
    public Object  importData(@RequestPart("file")MultipartFile file){
        return userService.importData(file);
    }

    @GetMapping("/search")
    @Operation(summary = "List users")
    public ResponseEntity<?> getUsersInfo(
            @Parameter(description = "Query string to query resources. Supported query patterns are \"exact match(k=v)\", \"fuzzy match(k=~v)\", \"range(k=[min~max])\", \"list with union releationship(k={v1 v2 v3})\" and \"list with intersetion relationship(k=(v1 v2 v3))\". The value of range and list can be string(enclosed by \" or '), integer or time(in format \"2020-04-09 02:36:00\"). All of these query patterns should be put in the query string \"q=xxx\" and splitted by \",\". e.g. q=k1=v1,k2=~v2,k3=[min~max], Note: q is empty mean query all result.")
            @RequestParam(required = false) String  q,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "ASC") Sort.Direction sort,
            @RequestParam(defaultValue = "id") String sortByColum

        ) {
        SuccessResponse<Page<User>> successResponse =  new SuccessResponse<>();
        successResponse.setData(userService.searchUser(q,pageNo,pageSize,sort,sortByColum));
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
        userService.changePassword(request, connectedUser);
        SuccessResponse<?> successResponse =  new SuccessResponse<>();
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }

    
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(
          @PathVariable int id,@Valid @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        userService.update(id, userUpdateRequest);
        SuccessResponse<?> successResponse =  new SuccessResponse<>();
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }

    @PutMapping(value = "/v2/update/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update2(
          @PathVariable int id,
          @ModelAttribute UserUpdateRequest userUpdateRequest

            // @RequestPart(value = "file", required = false) MultipartFile file,
            // // @RequestPart(value = "userUpdateRequest", required = false) UserUpdateRequest userUpdateRequest
            // @RequestParam String firstname,
            // @RequestParam String lastname
    ) {
        // UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        // userUpdateRequest.setFirstname(firstname);
        // userUpdateRequest.setLastname(lastname);
        userService.update(id, userUpdateRequest, userUpdateRequest.getFile());
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
        successResponse.setData(userService.getInfo(connectedUser));
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setMsg(SUCCESS);
        return ResponseEntity.ok(successResponse);
    }

}