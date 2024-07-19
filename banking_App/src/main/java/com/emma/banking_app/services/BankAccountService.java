package com.emma.banking_app.services;

import com.emma.banking_app.dtos.*;
import com.emma.banking_app.exceptions.ClientNotFoundException;
import com.emma.banking_app.exceptions.CompteNotFoundException;
import com.emma.banking_app.exceptions.SoldeInsufisantException;

import java.util.List;

public interface BankAccountService {
    ClientDto saveClient(ClientDto clientDto);
    CompteCourantDto saveCurrentAccount(double soldeInitial, double decouvert, Long idClient) throws ClientNotFoundException;
    CompteEpargneDto saveEpargneAccount(double soldeInitial, double taux, Long idClient) throws ClientNotFoundException;

    List<ClientDto> listClient();

    CompteDto getAccount(String accountId) throws CompteNotFoundException;
    void debit(TransfertDto transfertDto) throws CompteNotFoundException, SoldeInsufisantException;
    void credit(TransfertDto transfertDto) throws CompteNotFoundException;

    void transfert(String compteSource, String compteDestinataire, double montant) throws CompteNotFoundException, SoldeInsufisantException;

    List<CompteDto> listeCompte();

    ClientDto getClient(Long idClient) throws ClientNotFoundException;

    ClientDto updateClient(ClientDto clientDto);

    void deleteClient(Long clientId);

    List<OperationDto> historique(String compteId);

    AccountOperationsDto getAccountHistory(String id, int page, int size) throws CompteNotFoundException;

    List<ClientDto> searchClients(String motcle);
}
