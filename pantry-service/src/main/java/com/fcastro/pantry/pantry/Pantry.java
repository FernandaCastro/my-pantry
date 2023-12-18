package com.fcastro.pantry.pantry;

import com.fcastro.pantry.pantryItem.PantryItem;
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
    @NotBlank(message = "IsActive is mandatory")
    private Boolean isActive;

    @OneToMany(mappedBy = "pantry", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PantryItem> items;
}
