package com.fcastro.security.core.model;

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

    private String password;
    private String passwordQuestion;
    private String passwordAnswer;
}
