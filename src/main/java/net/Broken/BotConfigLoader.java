package net.Broken;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "discord.bot")
@ConstructorBinding
public record BotConfigLoader (
    String token,
    String url,
    String mode,
    String randomApiKey
){}
