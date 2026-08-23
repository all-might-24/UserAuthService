package com.ecommerceproject.userauthservice.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateRolesRequestDto {

    @NotEmpty
    private List<String> roleNames;
}
