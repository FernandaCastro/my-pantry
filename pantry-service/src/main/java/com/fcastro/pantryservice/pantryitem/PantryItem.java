package com.fcastro.pantryservice.pantryitem;

import com.fcastro.pantryservice.pantry.Pantry;
import com.fcastro.pantryservice.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "pantryItem")
@Table(name = "PANTRY_ITEM")
//@IdClass(PantryItemKey.class)
//@NamedEntityGraph(name = "pantryItem.pantry",
//        attributeNodes = @NamedAttributeNode("pantry")
//)
public class PantryItem {

    //    @Id
//    private Long pantryId;
//
//    @Id
//    private Long productId;
    @EmbeddedId
    private PantryItemKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pantryId")
    @JoinColumn(name = "pantry_id")
    private Pantry pantry;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    private int idealQty;
    private int currentQty;
    private int provisionedQty;
    private LocalDateTime lastProvisioning;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PantryItem)) return false;

        PantryItem that = (PantryItem) o;

        if (!getId().equals(that.getId())) return false;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        int result = getId().getPantryId().hashCode();
        result = 31 * result + getId().getProductId().hashCode();
        return result;
    }
}

