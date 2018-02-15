package net.Broken.RestApi.Data;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommandResponseData {
    public String Commande;
    public String Message;
    public String error;

    public CommandResponseData(String commande, String message) {
        Commande = commande;
        Message = message;
    }

    public CommandResponseData(String commande, String message, String error) {
        Commande = commande;
        Message = message;
        this.error = error;
    }
}
