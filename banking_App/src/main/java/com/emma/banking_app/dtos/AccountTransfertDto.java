package com.emma.banking_app.dtos;

import lombok.Data;

@Data
public class AccountTransfertDto {
    private String compteSource;
    private String compteDestinataire;
    private double montant ;
}
