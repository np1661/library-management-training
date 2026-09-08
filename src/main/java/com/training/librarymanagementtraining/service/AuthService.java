package com.training.librarymanagementtraining.service;

import com.training.librarymanagementtraining.dto.request.RegisterMemberRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.training.librarymanagementtraining.dto.request.LoginRequest;
import com.training.librarymanagementtraining.dto.response.LoginResponse;
import com.training.librarymanagementtraining.entity.User;
import com.training.librarymanagementtraining.repository.UserRepository;
import com.training.librarymanagementtraining.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import com.training.librarymanagementtraining.entity.Member;
import com.training.librarymanagementtraining.repository.MemberRepository;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String token = jwtService.generateToken(
                user.getUsername(),
                user.getRole()
        );

        return new LoginResponse(
                token,
                user.getUsername(),
                user.getRole()
        );
    }

    @Transactional
    public void registerMember(RegisterMemberRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(
                    "Username already exists");
        }

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already exists");
        }

        // Create library member
        Member member = new Member();

        member.setName(request.getName());
        member.setEmail(request.getEmail());
        member.setPhoneNumber(request.getPhoneNumber());

        Member savedMember = memberRepository.save(member);

        // Create login user
        User user = new User();

        user.setUsername(request.getUsername());

        user.setPassword(
                passwordEncoder.encode(request.getPassword()));

        user.setRole("MEMBER");

        // Connect login user with library member
        user.setMemberId(savedMember.getId());

        userRepository.save(user);
    }
}