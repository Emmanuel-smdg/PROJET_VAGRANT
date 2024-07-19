package com.emma.banking_app.web;

import com.emma.banking_app.dtos.ClientDto;
import com.emma.banking_app.dtos.CompteCourantDto;
import com.emma.banking_app.dtos.CompteEpargneDto;
import com.emma.banking_app.dtos.SaveAccountDto;
import com.emma.banking_app.exceptions.ClientNotFoundException;
import com.emma.banking_app.services.BankAccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
public class ClientController {
    private BankAccountService bankAccountService ;

    @GetMapping("/")
    public String home(){
        return "index";
    }
    @GetMapping("/index")
    public String index(){
        return "index";
    }

    @GetMapping("/clients")
    public String clients(Model model){
        model.addAttribute("listClients", bankAccountService.listClient());
        return "clients";
    }

    @GetMapping("/editerClient")
    public String editerClient(Model model, Long id) throws ClientNotFoundException {
        model.addAttribute("client", bankAccountService.getClient(id));
        return "editClient";
    }

    @GetMapping("/deleteClient")
    public String deleteClient(Long id) {
        bankAccountService.deleteClient(id);
        return "redirect:/clients";
    }

    @GetMapping("/formClient")
    public String formClient(Model model){
        model.addAttribute("client", new ClientDto());
        return "formClient";
    }

    @PostMapping("/saveClient")
    public String saveClient(Model model, @ModelAttribute("client") @Valid ClientDto client, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "formClient";
        }
        bankAccountService.saveClient(client);
        return "redirect:/clients";
    }

    @GetMapping("/formCompteCourant")
    public String formCompteCourant(Model model, Long id) {
        SaveAccountDto saveAccountDto = new SaveAccountDto();
        saveAccountDto.setIdClient(id);
        model.addAttribute("accountDetails", saveAccountDto);
        return "formCompteCourant";
    }

    @GetMapping("/formCompteEpargne")
    public String formCompteEpargne(Model model, Long id){
        SaveAccountDto saveAccountDto = new SaveAccountDto();
        saveAccountDto.setIdClient(id);
        model.addAttribute("accountDetails", saveAccountDto);
        return "formCompteEpargne";
    }

    @PostMapping("/saveCC")
    public String saveCC(Model model, @ModelAttribute("accountDetails") SaveAccountDto accountDetails)
            throws ClientNotFoundException {
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        System.out.println(accountDetails);
        bankAccountService.saveCurrentAccount(accountDetails.getSoldeInitial(), accountDetails.getDecouvert(),
                accountDetails.getIdClient());
        return "redirect:/clients";
    }

    @PostMapping("/saveSC")
    public String saveSC(Model model, @ModelAttribute("accountDetails") SaveAccountDto accountDetails)
            throws ClientNotFoundException {
        bankAccountService.saveEpargneAccount(accountDetails.getSoldeInitial(), accountDetails.getDecouvert(),
                accountDetails.getIdClient());
        return "redirect:/clients";
    }


}

