package com.hfsolution.feature.auth.dto;

// import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

  // @JsonProperty("access_token")
  private String accessToken;
  // @JsonProperty("refresh_token")
  private String refreshToken;
}
