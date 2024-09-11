package com.eCommerce.application.Security.Model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
public class SignupRequest {

    @NotBlank
    @Size(min = 3, max = 20)
    private String userName;

    @NotBlank
    @Size(max = 50)
    private String email;

    @Getter
    @Setter
    private Set<String> role;

    @NotBlank
    @Size(min = 6, max = 30)
    private String password;
}
