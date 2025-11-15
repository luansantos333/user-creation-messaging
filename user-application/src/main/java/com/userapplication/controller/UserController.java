package com.userapplication.controller;

import com.userapplication.dto.*;
import com.userapplication.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.CredentialExpiredException;
import java.util.List;

@RestController
@RequestMapping ("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserSecureDTO> createUser (@RequestBody UserDTO userDTO) {


        UserSecureDTO user = userService.createUser(userDTO);

        return ResponseEntity.ok().body(user);


    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserSecureDTO>> getAllUsers() {

        List<UserSecureDTO> allUsers = userService.getAllUsers();

        return ResponseEntity.ok().body(allUsers);


    }

    @GetMapping("/{username}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<UserSecureDTO> getUserByUsername(@PathVariable String username) {

        UserSecureDTO user = userService.getUserByUsername(username);

        return ResponseEntity.ok().body(user);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        userService.deleteUserById(id);

        return ResponseEntity.noContent().build();


    }

    @PatchMapping ("/grant/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> addAdminPrivilegeToUser (@PathVariable String username) {

        userService.elevateUserPrivilegesToAdmin(username);


        return ResponseEntity.noContent().build();
    }

    @PostMapping ("/reset/token")
    public ResponseEntity<Void> getResetToken (@RequestBody UserResetTokenDTO userResetTokenDTO) {

        userService.createPasswordResetToken(userResetTokenDTO.username());

        return ResponseEntity.noContent().build();

    }

    @PutMapping ("/password")
    public ResponseEntity<Void> resetPassword (@RequestBody PasswordResetDTO newPassword) throws CredentialExpiredException {


        userService.resetUserPassword(newPassword);


        return ResponseEntity.noContent().build();

    }




}
