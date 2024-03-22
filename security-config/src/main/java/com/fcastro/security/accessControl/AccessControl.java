package com.fcastro.security.accessControl;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "accessControl")
@Table(name = "access_control")
@IdClass(AccessControlKey.class)
public class AccessControl {

    @Id
    @Column(nullable = false)
    private String clazz;

    @Id
    @Column(nullable = false)
    private Long clazzId;

    @Column(nullable = false)
    private Long accountGroupId;
}
