package com.TiendaApp.demo.dto;

import lombok.Data;

@Data
public class HttpGlobalResponseDTO<T> {
    private String message;
    private T data;
}