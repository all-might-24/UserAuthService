package com.ecommerceproject.userauthservice.models;

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

}
