package com.ecommerceproject.userauthservice.dtos;

import com.ecommerceproject.userauthservice.models.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private List<Role> roles;

}
