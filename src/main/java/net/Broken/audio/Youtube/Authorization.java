package net.Broken.audio.Youtube;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.util.Preconditions;
import net.Broken.Tools.PrivateMessage;
import net.dv8tion.jda.core.entities.Guild;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Authorization extends AuthorizationCodeInstalledApp {

    private Guild guild;
    private Logger logger = LogManager.getLogger();

    /**
     * @param flow     authorization code flow
     * @param receiver verification code receiver
     */
    public Authorization(AuthorizationCodeFlow flow, VerificationCodeReceiver receiver, Guild guild) {
        super(flow, receiver);
        this.guild = guild;
    }

    @Override
    protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
        notify(authorizationUrl.build());
    }

    protected void notify(String url){
        Preconditions.checkNotNull(url);
        // Ask user to open in their browser using copy-paste
        logger.fatal("Please open this URL: "+url);
        PrivateMessage.send(guild.getOwner().getUser(),"Please open this url to confirm google api account acces : " + url,null);


    }
}
