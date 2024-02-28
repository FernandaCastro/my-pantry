package com.fcastro.account.account;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "account")
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String externalProvider;

    private String name;

    private String password;

    @Column(unique = true)
    private String email;

    private String pictureUrl;

    private String roles;

    private String passwordQuestion;

    private String passwordAnswer;

    public Account(String externalProvider, String name, String email, String pictureUrl) {
        this.externalProvider = externalProvider;
        this.name = name;
        this.email = email;
        this.pictureUrl = pictureUrl;
    }

}
