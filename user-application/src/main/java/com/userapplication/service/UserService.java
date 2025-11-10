package com.userapplication.service;

import com.userapplication.dto.PasswordResetTokenDTO;
import com.userapplication.dto.RoleDTO;
import com.userapplication.dto.UserDTO;
import com.userapplication.dto.UserSecureDTO;
import com.userapplication.dto.kafka.PasswordResetTokenEvent;
import com.userapplication.dto.kafka.UserAdminAccessGrant;
import com.userapplication.dto.kafka.UserCreatedEvent;
import com.userapplication.entity.PassswordResetTokenEntity;
import com.userapplication.entity.RoleEntity;
import com.userapplication.entity.UserEntity;
import com.userapplication.repository.RoleRepository;
import com.userapplication.repository.TokenRepository;
import com.userapplication.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final KafkaProducerService kafkaProducerService;
    private final TokenRepository tokenRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, KafkaProducerService kafkaProducerService,
                       TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.tokenRepository = tokenRepository;
    }

    @Transactional(readOnly = false)
    public UserSecureDTO createUser(UserDTO userDTO) {

        UserEntity userEntity = new UserEntity();
        mapUserDTOToUser(userDTO, userEntity);

        UserEntity user = userRepository.save(userEntity);



        kafkaProducerService.sendUserCreatedEvent("user-created", new UserCreatedEvent(user.getId(), Instant.now(), user.getUsername()));


        return new UserSecureDTO(user.getId(), user.getUsername(), user.getRoles().stream().map(x -> new RoleDTO(x.getRoleName())).collect(Collectors.toSet()));


    }


    private void mapUserDTOToUser(UserDTO userDTO, UserEntity entity) {

        entity.setUsername(userDTO.username());
        entity.setPassword(passwordEncoder.encode(userDTO.password()));
        entity.getRoles().add(roleRepository.findByRoleName("ROLE_USER").orElseThrow(NoSuchElementException::new));

    }

    @Transactional(readOnly = true)
    public List<UserSecureDTO> getAllUsers() {

        List<UserEntity> allUsers = userRepository.findAll();

        return allUsers.stream().map(user -> new UserSecureDTO(user.getId(), user.getUsername(),
                user.getRoles().stream().map(role -> new RoleDTO(role.getRoleName())).collect(Collectors.toSet()))).collect(Collectors.toList());

    }


    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("No User found with this username"));

        if (authentication.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals("ROLE_ADMIN") || authentication.getName().equals(user.getUsername()))) {


            return new UserDTO(user.getUsername(), user.getPassword(), user.getRoles().stream().map(role -> new RoleDTO(role.getRoleName())).collect(Collectors.toSet()));

        }


        throw new AccessDeniedException("You do not have permission to access this resource");

    }

    @Transactional
    public void deleteUserById(Long id) {

        if (!userRepository.existsById(id)) {

            throw new NoSuchElementException("No User found with this id");

        }

        UserEntity user = userRepository.findById(id).get();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals("ROLE_ADMIN")) || authentication.getName().equals(user.getUsername())) {
            userRepository.deleteById(id);
        } else throw new AccessDeniedException("You do not have permission to access this resource");


    }

    @Transactional
    public void elevateUserPrivilegesToAdmin(String username) {

        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("No User found with this username"));
        RoleEntity roleEntity = roleRepository.findByRoleName("ROLE_ADMIN").orElseThrow(() -> new AccessDeniedException("You do not have permission to access this resource"));
        userEntity.getRoles().add(new RoleEntity(roleEntity.getRoleId(), roleEntity.getRoleDescription(),  roleEntity.getRoleName()));
        kafkaProducerService.sendUserHasBeenGrantedAdminAccess("admin-grant", new UserAdminAccessGrant(userEntity.getUsername(), "Your user has been granted administrator permissions.", Instant.now()));
        userRepository.save(userEntity);


    }

    @Transactional
    public PasswordResetTokenDTO createPasswordResetToken(String email) {

        UserEntity userEntity = userRepository.findByUsername(email).orElseThrow(() -> new NoSuchElementException("No User found with this username"));
        PassswordResetTokenEntity passswordResetTokenEntity = new PassswordResetTokenEntity();
        passswordResetTokenEntity.setUser(userEntity);
        passswordResetTokenEntity.setToken(UUID.randomUUID().toString());
        passswordResetTokenEntity.setExpirationTime(Instant.now().plus(PassswordResetTokenEntity.expirationTimeInMinutes, ChronoUnit.MINUTES));
        PassswordResetTokenEntity token = tokenRepository.save(passswordResetTokenEntity);
        kafkaProducerService.sendPasswordResetTokenEvent("password-reset", new PasswordResetTokenEvent(passswordResetTokenEntity.getToken(), passswordResetTokenEntity.getExpirationTime(),
                userEntity.getUsername()));
        return new PasswordResetTokenDTO(token.getToken());

    }


}
