package com.emma.banking_app;

import com.emma.banking_app.dtos.ClientDto;
import com.emma.banking_app.dtos.CompteCourantDto;
import com.emma.banking_app.dtos.CompteEpargneDto;
import com.emma.banking_app.dtos.TransfertDto;
import com.emma.banking_app.exceptions.ClientNotFoundException;
import com.emma.banking_app.exceptions.CompteNotFoundException;
import com.emma.banking_app.exceptions.SoldeInsufisantException;
import com.emma.banking_app.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.stream.Stream;

@SpringBootApplication
public class BankingAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingAppApplication.class, args);
    }
    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService){
        return args -> {
            Stream.of("Mr_Neya","Razack","Ibrahim","Severine","Larissa","Emmanuel","Abdoul").forEach(name ->{
                ClientDto clientDto = new ClientDto();
                clientDto.setName(name);
                clientDto.setEmail(name + "@gmail.com");
                bankAccountService.saveClient(clientDto); });

            bankAccountService.listClient().forEach(clt->{
                try {
                    bankAccountService.saveCurrentAccount(Math.random()*750000,Math.random()*7400, clt.getId());
                    bankAccountService.saveEpargneAccount(Math.random()*800000,5.5, clt.getId());
                    bankAccountService.listeCompte().forEach(cpte->{
                        for (int i=0; i < 2 ; i++){
                            try {
                                String cpteId;
                                if(cpte instanceof CompteEpargneDto){
                                    cpteId = ((CompteEpargneDto) cpte).getId();
                                }else cpteId = ((CompteCourantDto) cpte).getId();
                                TransfertDto transfertDto = new TransfertDto();
                                transfertDto.setCompteId(cpteId);
                                transfertDto.setMontant(274000+Math.random()*9875);
                                transfertDto.setDescription("credit");
                                TransfertDto transfertDto1 = new TransfertDto();
                                transfertDto1.setCompteId(cpteId);
                                transfertDto1.setMontant(3000+Math.random()*1000);
                                transfertDto1.setDescription("debit");
                                bankAccountService.credit(transfertDto);
                                bankAccountService.debit(transfertDto1);
                            } catch (CompteNotFoundException | SoldeInsufisantException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } catch (ClientNotFoundException e) {
                    e.printStackTrace();
                }

            });

        };
    }
}
