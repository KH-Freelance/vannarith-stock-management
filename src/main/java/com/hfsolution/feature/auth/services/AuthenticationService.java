package com.hfsolution.feature.auth.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.enums.TokenType;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.properties.CloudinaryProperties;
import com.hfsolution.app.services.JwtService;
import com.hfsolution.feature.auth.dto.AuthenticationRequest;
import com.hfsolution.feature.auth.dto.AuthenticationResponse;
import com.hfsolution.feature.token.entity.Token;
import com.hfsolution.feature.token.repository.TokenRepository;
import com.hfsolution.feature.user.dto.RegisterRequest;
import com.hfsolution.feature.user.entity.User;
import com.hfsolution.feature.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;
import static com.hfsolution.app.constant.AppConstant.USERID;
import static com.hfsolution.app.constant.AppConstant.USERNAME;
import static com.hfsolution.app.constant.AppConstant.STOCK_USER;
@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final CloudinaryProperties cloudinaryProperties;
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request) {
    Optional<User> existUser = userRepository.findByEmail(request.getEmail());
    if(!existUser.isPresent()){
      var user = User.builder()
      .firstname(request.getFirstname())
      .lastname(request.getLastname())
      .email(request.getEmail())
      .imageUrl(cloudinaryProperties.getDefaultImage())
      .password(passwordEncoder.encode(request.getPassword()))
      .role(request.getRole())
      .build();
      var savedUser = repository.save(user);
      Map<String, Object> extraClaims = new HashMap<>();
      extraClaims.put(USERID, user.getId());
      extraClaims.put(USERNAME, user.getFirstname()+" "+user.getLastname());
      var jwtToken = jwtService.generateToken(extraClaims,user);
      var refreshToken = jwtService.generateRefreshToken(user);
      saveUserToken(savedUser, jwtToken);
      return AuthenticationResponse.builder()
          .accessToken(jwtToken)
              .refreshToken(refreshToken)
          .build();
    }else{
       throw new AppException("005");
    }
     
  }

  public AuthenticationResponse register(RegisterRequest request, MultipartFile file) throws IOException {
    Optional<User> existUser = userRepository.findByEmail(request.getEmail());
    if(!existUser.isPresent()){
      String imageUrl = null;
      if(file == null){
        imageUrl = cloudinaryProperties.getDefaultImage();
      }else{
        imageUrl = (String)uploadImage(file, STOCK_USER+"/"+request.getFirstname()+request.getLastname()).get("secure_url");
      }
      var user = User.builder()
      .firstname(request.getFirstname())
      .lastname(request.getLastname())
      .email(request.getEmail())
      .imageUrl(imageUrl)
      .password(passwordEncoder.encode(request.getPassword()))
      .role(request.getRole())
      .build();
      var savedUser = repository.save(user);
      Map<String, Object> extraClaims = new HashMap<>();
      extraClaims.put(USERID, user.getId());
      extraClaims.put(USERNAME, user.getFirstname()+" "+user.getLastname());
      var jwtToken = jwtService.generateToken(extraClaims,user);
      var refreshToken = jwtService.generateRefreshToken(user);
      saveUserToken(savedUser, jwtToken);
      return AuthenticationResponse.builder()
          .accessToken(jwtToken)
              .refreshToken(refreshToken)
          .build();
    }else{
       throw new AppException("005");
    }
     
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();
    Map<String, Object> extraClaims = new HashMap<>();
    extraClaims.put(USERID, user.getId());
    extraClaims.put(USERNAME, user.getFirstname()+" "+user.getLastname());
    var jwtToken = jwtService.generateToken(extraClaims,user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
        SuccessResponse<AuthenticationResponse> successResponse =  new SuccessResponse<>();
        successResponse.setData(authResponse);
        successResponse.setCode(SUCCESS_CODE);
        successResponse.setMsg(SUCCESS);
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), successResponse);
      }
    }
  }
   private Map uploadImage(MultipartFile file, String imageName) throws IOException{
  
        Cloudinary cloudinary = new Cloudinary(cloudinaryProperties.getUrl());
        System.out.println(cloudinary.config.cloudName);
        // Upload the image
        Map params1 = ObjectUtils.asMap(
            "use_filename", true,
            "unique_filename", false,
            "overwrite", true,
            "quality", "auto",
            "public_id", imageName

        );
        return cloudinary.uploader().upload(file.getBytes(), params1);
    }
}
