package com.emma.banking_app.dtos;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


@Data
public class ClientDto {

    private Long id ;
    @NotEmpty(message = "Le nom est obligatoire")
    @Size(min = 4, max = 70, message = "Le nom doit avoir au moins 4 caractères")
    private String name ;
    @Email(message = "Le format de l'email n'est pas valide")
    private String email ;

}
