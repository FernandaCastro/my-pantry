package com.fcastro.accountservice.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class KeyPairDto {
    private String data;
}
