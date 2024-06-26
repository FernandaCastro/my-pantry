package com.fcastro.pantryservice.pantry;

import com.fcastro.pantryservice.pantryitem.PantryItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Type is mandatory")
    private String type;
    private Boolean isActive;

    @OneToMany(mappedBy = "pantry", fetch = FetchType.LAZY)
    private List<PantryItem> items;
}
