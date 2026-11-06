package com.TiendaApp.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TiendaApp.demo.dto.HttpGlobalResponseDTO;
import com.TiendaApp.demo.dto.LoginRequestDTO;
import com.TiendaApp.demo.dto.LoginResponseDTO;
import com.TiendaApp.demo.dto.RegisterRequestDTO;
import com.TiendaApp.demo.dto.UserDTO;
import com.TiendaApp.demo.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> register(@RequestBody RegisterRequestDTO request) {
        try {
            HttpGlobalResponseDTO<UserDTO> response = authService.register(request);
            
            if (response.getMessage().contains("ya está en uso")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            HttpGlobalResponseDTO<UserDTO> errorResponse = new HttpGlobalResponseDTO<>();
            errorResponse.setMessage("Error en el registro: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<HttpGlobalResponseDTO<LoginResponseDTO>> login(@RequestBody LoginRequestDTO request) {
        try {
            HttpGlobalResponseDTO<LoginResponseDTO> response = authService.login(request);
            
            if (response.getMessage().contains("incorrectos") || response.getMessage().contains("inactivo")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            HttpGlobalResponseDTO<LoginResponseDTO> errorResponse = new HttpGlobalResponseDTO<>();
            errorResponse.setMessage("Error en el login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<HttpGlobalResponseDTO<LoginResponseDTO>> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        HttpGlobalResponseDTO<LoginResponseDTO> response = new HttpGlobalResponseDTO<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setMessage("Token is invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = authHeader.replace("Bearer ", "");

        try {
            response = authService.refreshToken(token);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Token is invalid or expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}