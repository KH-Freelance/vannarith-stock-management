package com.hfsolution.feature.user.dto;

import com.hfsolution.feature.user.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class RegisterRequest {

  @NotBlank(message = "firstname is required.")
  private String firstname;
  @NotBlank(message = "lastname is required.")
  private String lastname;
  @Email(message = "Invalid email format")
  @NotBlank(message = "email is required.")
  private String email;
  @NotBlank(message = "password is required.")
  private String password;
  private Role role;
}
