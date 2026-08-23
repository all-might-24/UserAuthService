package com.ecommerceproject.userauthservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Session extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String token;
}
