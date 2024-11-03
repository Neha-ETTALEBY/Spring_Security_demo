package org.sid.secservice.sec.web;

import lombok.Data;
import org.sid.secservice.sec.entities.AppRole;
import org.sid.secservice.sec.entities.AppUser;
import org.sid.secservice.sec.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountRestController {
    private AccountService accountService;
    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }
    @GetMapping(path="/users")
    public List<AppUser> appUsers(){
     return accountService.listUsers();
    }
    @PostMapping(path = "/usersAdd")
    public AppUser saveUser(@RequestBody AppUser user){
        return accountService.addNewUser(user);
    }
    @PostMapping (path="/roles")
    public AppRole saveRole(@RequestBody AppRole role){
        return accountService.addNewRole(role);
    }
    @PostMapping(path="/addRoleToUser")
    public void addRoleToUser(@RequestBody RoleUserForm roleUserForm){
         accountService.addRoleToUser(roleUserForm.getUsername(),roleUserForm.getRoleName());
    }
    @Data
    class RoleUserForm{
        private String username;
         private String roleName;

    }



}
