package com.fcastro.accountservice.account;

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

    @Column(nullable = false, length = 20)
    private String name;

    private String password;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    private String pictureUrl;

    private String passwordQuestion;

    private String passwordAnswer;

    private String theme;

    public Account(String externalProvider, String name, String email, String pictureUrl) {
        this.externalProvider = externalProvider;
        this.name = name;
        this.email = email;
        this.pictureUrl = pictureUrl;
    }

}
