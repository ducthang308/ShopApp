package com.example.ShopApp.Services;

import com.example.ShopApp.Components.JwtTokenUtils;
import com.example.ShopApp.DTO.UserDTO;
import com.example.ShopApp.Exception.DataNotFoundException;
import com.example.ShopApp.Exception.PermissonDenyException;
import com.example.ShopApp.Models.*;
import com.example.ShopApp.Repositories.RoleRepository;
import com.example.ShopApp.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements InterfaceUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public User createUser(UserDTO userDTO) throws Exception {
        //register user
        String phone = userDTO.getPhone();
        //Kiểm tra xem số điện thoại đã tồn tại chưa
        if(userRepository.existsByPhone(phone)){
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(()->new DataNotFoundException("Role not found"));
        if(role.getName().toUpperCase().equals(Role.ADMIN)){
            throw new PermissonDenyException("You cannot register an ADMIN account");
        }
        //Convert từ userDTO => user
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phone(userDTO.getPhone())
                .pass(userDTO.getPass())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFbAccountId())
                .googleAccountId(userDTO.getGgAccountId())
                .build();
        newUser.setRole(role);
        if(userDTO.getFbAccountId() == 0 && userDTO.getGgAccountId() == 0){
            String password = userDTO.getPass();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPass(encodedPassword);
        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(String phone, String pass) throws Exception {
        Optional<User> optionalUser = userRepository.findByPhone(phone);
        if(optionalUser.isEmpty()){
            throw new DataNotFoundException("Invalid phone number Or password");
        }
        User existingUser = optionalUser.get();
        //check password
        if(existingUser.getFacebookAccountId() == 0 && existingUser.getGoogleAccountId() == 0){
            if(!passwordEncoder.matches(pass, existingUser.getPassword())){
                throw new BadCredentialsException("Wrong phone number or password");
            }
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                phone, pass, existingUser.getAuthorities()
        );
        //authenticate with java spring security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
        //return optionalUser.get(); //Trả về user
    }
}
