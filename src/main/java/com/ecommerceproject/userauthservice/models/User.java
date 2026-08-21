package com.ecommerceproject.userauthservice.models;

import com.ecommerceproject.userauthservice.dtos.UserDto;
import com.ecommerceproject.userauthservice.models.enums.State;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class User extends BaseEntity {

    private String username;

    private String email;

    private String password;

    @ManyToMany
    private List<Role> roles; // user *---* roles

    public UserDto convertToDTO() {
        UserDto userDto = new UserDto();
        userDto.setId(this.getId());
        userDto.setEmail(this.email);
        userDto.setUsername(this.username);
        userDto.setRoles(this.roles);

        return userDto;
    }
}
