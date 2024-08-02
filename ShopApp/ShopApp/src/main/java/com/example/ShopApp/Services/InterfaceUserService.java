package com.example.ShopApp.Services;

import com.example.ShopApp.DTO.UserDTO;
import com.example.ShopApp.Exception.DataNotFoundException;
import com.example.ShopApp.Models.User;
import org.springframework.stereotype.Service;

public interface InterfaceUserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login(String phone, String password) throws Exception;
}
