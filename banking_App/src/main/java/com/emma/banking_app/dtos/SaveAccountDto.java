package com.emma.banking_app.dtos;

import lombok.Data;

@Data
public class SaveAccountDto {
    private double soldeInitial;
    private double decouvert;
    private Long idClient;

}
