package com.fcastro.pantryservice.product;

import com.fcastro.pantryservice.pantryitem.PantryItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "product")
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Code is mandatory")
    private String code;
    private String description;
    private String size;
    private String category;

    @Transient
    private Long accountGroupId;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<PantryItem> items;

    public Product(Long id, String code, String description, String size, String category, Long accountGroupId) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.size = size;
        this.category = category;
        this.accountGroupId = accountGroupId;
    }

}
