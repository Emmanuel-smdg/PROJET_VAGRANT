package com.emma.banking_app.dtos;

import com.emma.banking_app.entities.Client;
import com.emma.banking_app.enums.AccountStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
public class CompteDto {
    private String id ;
    private double solde ;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdAt ;
    private AccountStatus status;
    private Client clientDto ;
    private double decouvert ;
    private double taux ;
    private String type ;
}
