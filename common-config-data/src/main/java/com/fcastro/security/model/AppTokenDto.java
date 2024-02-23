package com.fcastro.security.model;

import com.fcastro.app.model.AccountDto;
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
