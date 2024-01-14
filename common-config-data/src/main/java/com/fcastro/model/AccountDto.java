package com.fcastro.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccountDto {
    private Long id;
    private String name;
    private String email;
    private String pictureUrl;
    private String roles;
}
