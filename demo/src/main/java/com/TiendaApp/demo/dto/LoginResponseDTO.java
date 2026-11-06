package com.TiendaApp.demo.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String jwt;
    private UserDTO user;
}