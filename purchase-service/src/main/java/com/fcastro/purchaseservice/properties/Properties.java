package com.fcastro.purchaseservice.properties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "properties")
@Table(name = "properties")
public class Properties {

    @Id
    private String propertyKey;

    @NotBlank(message = "Property Value is mandatory")
    @Column(columnDefinition = "jsonb")
    private String propertyValue;

    private Long accountGroupId;
}
