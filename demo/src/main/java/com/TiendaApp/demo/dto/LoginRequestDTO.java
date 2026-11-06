package com.TiendaApp.demo.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String user;
    private String password;
}