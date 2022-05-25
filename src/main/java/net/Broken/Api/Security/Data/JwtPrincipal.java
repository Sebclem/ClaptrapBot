package net.Broken.Api.Security.Data;

import net.Broken.DB.Entity.UserEntity;

public record JwtPrincipal(String jwtId, UserEntity user) {
}
