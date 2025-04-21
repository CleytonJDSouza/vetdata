package com.project.vetdata.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UserCreateDTO {

    private String name;
    private String email;
    private String password;

    public UserCreateDTO(){
    }

    public UserCreateDTO(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @NotBlank(message = "Campo Obrigatório")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotBlank(message = "Campo Obrigatório")
    @Email(message = "Formato de email inválido")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NotBlank(message = "Campo Obrigatório")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[%&*@]).{5,}$", message = "A senha deve conter pelo menos uma letra maiúscula, um número, um caractere especial (%&*@) e ter no mínimo de 5 caracteres.")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
