package com.fcastro.security.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppTokenDto {
    private String token;
    private AccountDto account;
}
