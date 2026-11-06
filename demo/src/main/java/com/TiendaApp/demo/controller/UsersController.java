package com.TiendaApp.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.TiendaApp.demo.dto.HttpGlobalResponseDTO;
import com.TiendaApp.demo.dto.RegisterRequestDTO;
import com.TiendaApp.demo.dto.UserDTO;
import com.TiendaApp.demo.enums.RoleEnum;
import com.TiendaApp.demo.security.RequiresRole;
import com.TiendaApp.demo.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @GetMapping("/me")
    @RequiresRole({RoleEnum.ADMIN, RoleEnum.CLIENTE, RoleEnum.VENDEDOR})
    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> getMyProfile(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            HttpGlobalResponseDTO<UserDTO> response = usersService.getMyProfile(userId);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping
    @RequiresRole({RoleEnum.ADMIN})
    public ResponseEntity<HttpGlobalResponseDTO<List<UserDTO>>> getAllUsers() {
        try {
            HttpGlobalResponseDTO<List<UserDTO>> response = usersService.getAllUsers();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping
    @RequiresRole({RoleEnum.ADMIN})
    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> createUser(@RequestBody RegisterRequestDTO request) {
        try {
            HttpGlobalResponseDTO<UserDTO> response = usersService.createUser(request);
            if (response.getMessage().contains("en uso")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PatchMapping("/{id}/role")
    @RequiresRole({RoleEnum.ADMIN})
    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> updateUserRole(
            @PathVariable Long id, @RequestParam Long newRolId) {
        try {
            HttpGlobalResponseDTO<UserDTO> response = usersService.updateUserRole(id, newRolId);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PatchMapping("/{id}/status")
    @RequiresRole({RoleEnum.ADMIN})
    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> updateUserStatus(@PathVariable Long id) {
        try {
            HttpGlobalResponseDTO<UserDTO> response = usersService.updateUserStatus(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}