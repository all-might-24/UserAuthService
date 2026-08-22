package com.ecommerceproject.userauthservice.dtos;

import com.ecommerceproject.userauthservice.models.Role;
import com.ecommerceproject.userauthservice.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignRolesRequestDto {
    private User user;
    private List<Role> roles;
}
