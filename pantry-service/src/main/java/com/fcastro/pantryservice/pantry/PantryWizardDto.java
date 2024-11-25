package com.fcastro.pantryservice.pantry;

import com.fcastro.pantryservice.pantryitem.PantryItemWizardDto;
import com.fcastro.security.modelclient.AccountGroupDto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PantryWizardDto {

//    {
//        "name": "nova pantry",
//        "type": "R",
//        "accountGroup": { "id": 1 }
//    }

    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Type is mandatory")
    private String type;
    private AccountGroupDto accountGroup;
    private boolean analysePantry;


    //    [
//    {
//        "code": "Rice",
//        "size": "500 g",
//        "category": "grocery",
//        "idealQty": 8,
//        "currentQty": 1
//    },
//            ...
//    ]
    private List<PantryItemWizardDto> items;

}
