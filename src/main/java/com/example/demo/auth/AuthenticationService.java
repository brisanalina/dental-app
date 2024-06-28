package com.example.demo.auth;


import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest registerRequest) {
            try {
                var user = User
                        .builder()
                        .firstName(registerRequest.getFirstName())
                        .lastName(registerRequest.getLastName())
                        .email(registerRequest.getEmail())
                        .password(passwordEncoder.encode(registerRequest.getPassword()))
                        .role(Role.USER)
                        .userName(registerRequest.getUserName())
                        .build();
                repository.save(user);
                var jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse
                        .builder()
                        .token(jwtToken)
                        .build();
            }catch (Exception e){
                return null;
            }


    }

    public AuthenticationResponse authenticate(AuthenticationrRequest registerRequest) {
        authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                registerRequest.getEmail()
                                ,registerRequest.getPassword()
                        ));
        var user =repository.findByEmail(registerRequest.getEmail()).orElseThrow(RuntimeException::new);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }
}
