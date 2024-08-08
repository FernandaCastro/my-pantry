package com.fcastro.pantryservice.pantryitem;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PantryItemWizardDto {

    @NotBlank(message = "Code is mandatory")
    private String code;
    @NotBlank(message = "Size is mandatory")
    private String size;
    @NotBlank(message = "Category is mandatory")
    private String category;
    @NotBlank(message = "Ideal Qty is mandatory")
    private int idealQty;
    @NotBlank(message = "Current Qty is mandatory")
    private int currentQty;

}
