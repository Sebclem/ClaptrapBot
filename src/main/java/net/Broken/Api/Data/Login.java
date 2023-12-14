package net.Broken.Api.Data;

import jakarta.validation.constraints.NotBlank;

public record Login(
        @NotBlank String code, @NotBlank String redirectUri) {

}
