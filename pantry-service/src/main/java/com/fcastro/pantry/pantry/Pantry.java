package com.fcastro.pantry.pantry;

import com.fcastro.pantry.pantryItem.PantryItem;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "pantry")
//@Table(name = "pantry")
//@NamedEntityGraph(name = "Pantry.items",
//        attributeNodes = @NamedAttributeNode("items")
//)
public class Pantry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private Boolean isActive;

    @OneToMany(mappedBy = "pantry", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PantryItem> items;
}
