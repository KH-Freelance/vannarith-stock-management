package com.hfsolution.feature.stockmanagement.dto.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User  {

  
  private Long id;

  private String firstname;

  private String lastname;
 
  private String email;

  private String password;

  // private Role role;

  private String imageUrl;


}
