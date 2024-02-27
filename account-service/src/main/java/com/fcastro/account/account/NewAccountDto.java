package com.fcastro.account.account;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NewAccountDto {

    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Password is mandatory")
    private String password;

    @NotBlank(message = "Question is mandatory")
    private String passwordQuestion;

    @NotBlank(message = "Answer is mandatory")
    private String passwordAnswer;

    private String pictureUrl;

    private String roles;
}
