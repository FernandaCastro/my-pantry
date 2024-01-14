package com.fcastro.account.account;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String externalId;

    private String externalProvider;

    private String name;

    private String email;

    private String pictureUrl;

    private String roles;

    public Account(String externalProvider, String name, String email, String pictureUrl) {
        this.externalProvider = externalProvider;
        this.name = name;
        this.email = email;
        this.pictureUrl = pictureUrl;
    }

}
