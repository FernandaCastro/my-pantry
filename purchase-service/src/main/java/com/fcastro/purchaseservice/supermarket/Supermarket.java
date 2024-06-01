package com.fcastro.purchaseservice.supermarket;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "supermarket")
@Table(name = "supermarket")
public class Supermarket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory.")
    private String name;

    @NotNull(message = "Categories is mandatory.")
    @Type(JsonBinaryType.class)
    @Column(name = "categories", columnDefinition = "jsonb")
    private ArrayList<String> categories;
}
