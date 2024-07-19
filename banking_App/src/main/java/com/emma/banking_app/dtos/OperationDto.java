package com.emma.banking_app.dtos;

import com.emma.banking_app.enums.OperationType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Data
public class OperationDto {
    private Long id ;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date operationDate = new Date();
    private double montant;
    private OperationType type ;
    private String description ;
}
