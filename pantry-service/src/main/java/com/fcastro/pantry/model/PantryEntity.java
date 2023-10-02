package com.fcastro.pantry.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PANTRY")
public class PantryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String type;
    private boolean active;

    @OneToMany(mappedBy = "pantry")
    private List<PantryProductEntity> products;
}
