package com.ecommerceproject.userauthservice.models;


import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Role extends BaseEntity{

    private String roleTitle;

}
