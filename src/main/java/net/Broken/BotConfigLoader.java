package net.Broken;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discord.bot")
public record BotConfigLoader(
                String token,
                String url,
                String mode,
                String randomApiKey) {
}
