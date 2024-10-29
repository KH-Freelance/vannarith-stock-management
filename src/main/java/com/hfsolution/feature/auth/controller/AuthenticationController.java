package com.hfsolution.feature.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.feature.auth.dto.AuthenticationRequest;
import com.hfsolution.feature.auth.dto.AuthenticationResponse;
import com.hfsolution.feature.auth.services.AuthenticationService;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  @PostMapping("/authenticate")
  public ResponseEntity<?> authenticate(
      @RequestBody AuthenticationRequest request
  ) {

    SuccessResponse<AuthenticationResponse> successResponse =  new SuccessResponse<>();
    successResponse.setData(service.authenticate(request));
    successResponse.setCode(SUCCESS_CODE);
    successResponse.setMsg(SUCCESS);
    return ResponseEntity.ok(successResponse);
    
  }

  @PostMapping(path = "/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }
}
