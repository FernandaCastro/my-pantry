package com.fcastro.pantryInventory.pantry;

import com.fcastro.pantryInventory.pantryItem.PantryItem;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pantry")
public class Pantry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private Boolean isActive;

    @OneToMany(mappedBy = "pantry", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PantryItem> items;
}
