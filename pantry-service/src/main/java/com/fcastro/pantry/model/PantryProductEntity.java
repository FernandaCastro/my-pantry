package com.fcastro.pantry.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PANTRY_PRODUCT")
public class PantryProductEntity {

    @EmbeddedId
    private PantryProductPk pantryProductPK;

    private int idealQty;
    private int currentQty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({@JoinColumn(name = "PANTRY_ID", referencedColumnName = "id", insertable = false, updatable = false)})
    @JsonIgnore
    private PantryEntity pantry;
}

