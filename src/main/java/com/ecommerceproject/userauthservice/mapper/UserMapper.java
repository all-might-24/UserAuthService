package com.ecommerceproject.userauthservice.mapper;

import com.ecommerceproject.userauthservice.dtos.UserDto;
import com.ecommerceproject.userauthservice.models.Role;
import com.ecommerceproject.userauthservice.models.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        List<String> roleNames = new ArrayList<>();
        for(Role role : user.getRoles()) {
            roleNames.add(role.getRoleTitle());
        }
        dto.setRoles(roleNames);
        return dto;
    }
}
