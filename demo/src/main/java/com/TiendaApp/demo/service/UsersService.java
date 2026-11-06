package com.TiendaApp.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.TiendaApp.demo.dto.HttpGlobalResponseDTO;
import com.TiendaApp.demo.dto.RegisterRequestDTO;
import com.TiendaApp.demo.dto.UserDTO;
import com.TiendaApp.demo.entity.Roles;
import com.TiendaApp.demo.entity.Users;
import com.TiendaApp.demo.repository.RolesRepository;
import com.TiendaApp.demo.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;

    public HttpGlobalResponseDTO<UserDTO> getMyProfile(Long userId) {
        HttpGlobalResponseDTO<UserDTO> response = new HttpGlobalResponseDTO<>();
        Optional<Users> userFound = usersRepository.findById(userId);

        if (userFound.isEmpty()) {
            response.setMessage("Usuario no encontrado");
            return response;
        }

        Users u = userFound.get();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(u.getId());
        userDTO.setUsername(u.getUsername());
        userDTO.setEmail(u.getEmail());
        userDTO.setRolId(u.getRoles().getName());

        response.setMessage("Perfil obtenido correctamente");
        response.setData(userDTO);
        return response;
    }

    public HttpGlobalResponseDTO<List<UserDTO>> getAllUsers() {
        HttpGlobalResponseDTO<List<UserDTO>> response = new HttpGlobalResponseDTO<>();
        List<UserDTO> listUsers = new ArrayList<>();

        List<Users> usersFound = usersRepository.findAll();

        for (Users u : usersFound) {
            UserDTO user = new UserDTO();
            user.setId(u.getId());
            user.setUsername(u.getUsername());
            user.setEmail(u.getEmail());
            user.setRolId(u.getRoles().getName());

            listUsers.add(user);
        }

        response.setMessage("Usuarios encontrados");
        response.setData(listUsers);
        return response;
    }

    public HttpGlobalResponseDTO<UserDTO> createUser(RegisterRequestDTO request) {
        HttpGlobalResponseDTO<UserDTO> response = new HttpGlobalResponseDTO<>();

        if (usersRepository.findByUsername(request.getUsername()).isPresent()) {
            response.setMessage("El nombre de usuario ya está en uso");
            return response;
        }

        if (usersRepository.findByEmail(request.getEmail()).isPresent()) {
            response.setMessage("El correo ya está en uso");
            return response;
        }

        Optional<Roles> rol = rolesRepository.findById(request.getRolId());
        if (rol.isEmpty()) {
            response.setMessage("El rol especificado no existe");
            return response;
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setRoles(rol.get());

        usersRepository.save(user);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRolId(rol.get().getName());

        response.setMessage("Usuario creado correctamente por el administrador");
        response.setData(userDTO);
        return response;
    }

    public HttpGlobalResponseDTO<UserDTO> updateUserRole(Long id, Long newRolId) {
        HttpGlobalResponseDTO<UserDTO> response = new HttpGlobalResponseDTO<>();
        
        Optional<Users> userOpt = usersRepository.findById(id);
        if (userOpt.isEmpty()) {
            response.setMessage("Usuario no encontrado");
            return response;
        }

        Optional<Roles> rolOpt = rolesRepository.findById(newRolId);
        if (rolOpt.isEmpty()) {
            response.setMessage("El rol nuevo no existe");
            return response;
        }

        Users user = userOpt.get();
        user.setRoles(rolOpt.get());
        usersRepository.save(user);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRolId(rolOpt.get().getName());

        response.setMessage("Rol actualizado correctamente");
        response.setData(userDTO);
        return response;
    }

    public HttpGlobalResponseDTO<UserDTO> updateUserStatus(Long id) {
        HttpGlobalResponseDTO<UserDTO> response = new HttpGlobalResponseDTO<>();
        
        Optional<Users> userOpt = usersRepository.findById(id);
        if (userOpt.isEmpty()) {
            response.setMessage("Usuario no encontrado");
            return response;
        }

        Users user = userOpt.get();
        user.setActive(!user.getActive());
        usersRepository.save(user);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRolId(user.getRoles().getName());

        response.setMessage("Estado del usuario actualizado a: " + (user.getActive() ? "ACTIVO" : "INACTIVO"));
        response.setData(userDTO);
        return response;
    }
}