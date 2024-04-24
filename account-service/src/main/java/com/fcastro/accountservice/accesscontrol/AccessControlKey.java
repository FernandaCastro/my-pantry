package com.fcastro.accountservice.accesscontrol;

import jakarta.persistence.Column;
import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AccessControlKey implements Serializable {

    @Column(name = "clazz")
    private String clazz;

    @Column(name = "clazz_id")
    private long clazzId;

}
