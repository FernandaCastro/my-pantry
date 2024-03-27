package com.fcastro.security.core.model;

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
