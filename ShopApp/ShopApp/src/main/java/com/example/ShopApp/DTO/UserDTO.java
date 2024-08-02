package com.example.ShopApp.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("phone")
    @NotBlank(message = "Phone is required")
    private String phone;

    @JsonProperty("address")
    private String address;

    @JsonProperty("pass")
    @NotBlank(message = "Password cannot be blank")
    private String pass;

    @JsonProperty("retype_pass")
    private String retypePass;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("fb_account_id")
    private int fbAccountId;

    @JsonProperty("gg_account_id")
    private int ggAccountId;

    @JsonProperty("role_id")
    @NotNull(message = "Role ID is required")
    private Long roleId;


}
