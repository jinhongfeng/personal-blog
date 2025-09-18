package myself.potato.controller.dto;

import lombok.Data;

@Data
public class UserDTO {

    private String id;
    private String username;
    private String password;
    private String role;
    private String token;



}
