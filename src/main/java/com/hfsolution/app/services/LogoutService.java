package com.hfsolution.app.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.feature.auth.dto.AuthenticationResponse;
import com.hfsolution.feature.token.repository.TokenRepository;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final TokenRepository tokenRepository;

  @Override
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) {
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    jwt = authHeader.substring(7);
    var storedToken = tokenRepository.findByToken(jwt)
        .orElse(null);
    if (storedToken != null) {
      storedToken.setExpired(true);
      storedToken.setRevoked(true);
      tokenRepository.save(storedToken);
      SecurityContextHolder.clearContext();
      SuccessResponse<AuthenticationResponse> successResponse =  new SuccessResponse<>();
      // successResponse.setData(authResponse);
      successResponse.setCode(SUCCESS_CODE);
      successResponse.setMsg(SUCCESS);
      response.setContentType("application/json");
      try {
        new ObjectMapper().writeValue(response.getOutputStream(), successResponse);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
}
