package com.ecommerceproject.userauthservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Session extends BaseEntity{
    @ManyToOne
    private User user;

    private String token;
}
