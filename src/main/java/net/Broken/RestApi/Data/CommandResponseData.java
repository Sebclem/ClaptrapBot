package net.Broken.RestApi.Data;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommandResponseData {
    public String Commande;
    public String Message;

    public CommandResponseData(String commande, String message) {
        Commande = commande;
        Message = message;
    }
}
