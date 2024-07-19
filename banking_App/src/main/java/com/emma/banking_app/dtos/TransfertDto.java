package com.emma.banking_app.dtos;

import lombok.Data;

@Data
public class TransfertDto {
    private String compteId;
    private double montant;
    private String description;
}
