package net.Broken.Api.Data;

import javax.validation.constraints.NotBlank;

public record Login(
        @NotBlank String code, @NotBlank String redirectUri) {

}
