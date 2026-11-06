package com.TiendaApp.demo.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.TiendaApp.demo.dto.HttpGlobalResponseDTO;
import com.TiendaApp.demo.dto.LoginRequestDTO;
import com.TiendaApp.demo.dto.LoginResponseDTO;
import com.TiendaApp.demo.dto.RegisterRequestDTO;
import com.TiendaApp.demo.dto.UserDTO;
import com.TiendaApp.demo.entity.Roles;
import com.TiendaApp.demo.entity.Users;
import com.TiendaApp.demo.enums.RoleEnum;
import com.TiendaApp.demo.repository.RolesRepository;
import com.TiendaApp.demo.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public HttpGlobalResponseDTO<UserDTO> register(RegisterRequestDTO request) {
        HttpGlobalResponseDTO<UserDTO> response = new HttpGlobalResponseDTO<>();
        UserDTO userDTO = new UserDTO();

        Optional<Users> usernameFound = usersRepository.findByUsername(request.getUsername());
        Optional<Users> emailFound = usersRepository.findByEmail(request.getEmail());

        if (usernameFound.isPresent()) {
            response.setMessage("El nombre de usuario ya está en uso");
            return response;
        }

        if (emailFound.isPresent()) {
            response.setMessage("El correo de usuario ya está en uso");
            return response;
        }

        Optional<Roles> rol = rolesRepository.findById(RoleEnum.CLIENTE.getId());

        if (rol.isEmpty()) {
            response.setMessage("El rol por defecto no existe");
            return response;
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setRoles(rol.get());

        usersRepository.save(user);

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRolId(rol.get().getName());

        response.setMessage("Usuario registrado correctamente");
        response.setData(userDTO);

        return response;
    }

    public HttpGlobalResponseDTO<LoginResponseDTO> login(LoginRequestDTO request) {
        HttpGlobalResponseDTO<LoginResponseDTO> response = new HttpGlobalResponseDTO<>();
        Optional<Users> email = usersRepository.findByEmail(request.getUser());
        Optional<Users> username = usersRepository.findByUsername(request.getUser());

        if (username.isEmpty() && email.isEmpty()) {
            response.setMessage("Usuario y/o contraseña incorrectos");
            return response;
        }

        Users userFound = email.isPresent() ? email.get() : username.get();

        if (!userFound.getActive()) {
            response.setMessage("El usuario se encuentra inactivo");
            return response;
        }

        if (!passwordEncoder.matches(request.getPassword(), userFound.getPassword())) {
            response.setMessage("Usuario y/o contraseña incorrectos");
            return response;
        }

        String jwt = jwtService.generateToken(userFound.getId(), userFound.getUsername(), userFound.getRoles().getId());
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userFound.getId());
        userDTO.setUsername(userFound.getUsername());
        userDTO.setEmail(userFound.getEmail());
        userDTO.setRolId(userFound.getRoles().getName());

        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setJwt(jwt);
        loginResponse.setUser(userDTO);

        response.setMessage("Inicio de sesión exitoso");
        response.setData(loginResponse);

        return response;
    }

    public HttpGlobalResponseDTO<LoginResponseDTO> refreshToken(String token) throws Exception {
        HttpGlobalResponseDTO<LoginResponseDTO> response = new HttpGlobalResponseDTO<>();
        LoginResponseDTO refresh = new LoginResponseDTO();
        
        String jwt = jwtService.refreshToken(token);
        Long userId = jwtService.extractUserId(token);
        
        Optional<Users> userFound = usersRepository.findById(userId);
        if (userFound.isEmpty()) {
            throw new Exception("Usuario no encontrado");
        }
        
        Users user = userFound.get();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRolId(user.getRoles().getName());

        refresh.setJwt(jwt);
        refresh.setUser(userDTO);
        
        response.setMessage("Token refrescado correctamente");
        response.setData(refresh);
        return response;
    }
}