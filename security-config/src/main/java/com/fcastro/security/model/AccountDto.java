package com.fcastro.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private String password;
    @JsonIgnore
    private String passwordQuestion;
    @JsonIgnore
    private String passwordAnswer;
}
