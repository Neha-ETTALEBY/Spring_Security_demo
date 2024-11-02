package org.sid.secservice;

import org.sid.secservice.sec.entities.AppRole;
import org.sid.secservice.sec.entities.AppUser;
import org.sid.secservice.sec.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication
public class SecServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(AccountService accountService) {
        return args -> {
            accountService.addNewRole(new AppRole(1, "USER"));
            accountService.addNewRole(new AppRole(2, "ADMIN"));
            accountService.addNewRole(new AppRole(3, "CUSTOMER_MANAGER"));
            accountService.addNewRole(new AppRole(4, "PRODUCT_MANAGER"));
            accountService.addNewRole(new AppRole(5, "BILLS_MANAGER"));

            accountService.addNewUser(new AppUser(1L, "user1", "1234", new ArrayList<>()));
            accountService.addNewUser(new AppUser(2L, "admin", "1234", new ArrayList<>()));
            accountService.addNewUser(new AppUser(3L, "user2", "1234", new ArrayList<>()));
            accountService.addNewUser(new AppUser(4L, "user3", "1234", new ArrayList<>()));
            accountService.addNewUser(new AppUser(5L, "user4", "1234", new ArrayList<>()));
            accountService.addRoleToUser("user1", "USER");
            accountService.addRoleToUser("admin", "ADMIN");
            accountService.addRoleToUser("admin", "USER");
            accountService.addRoleToUser("user2", "USER");
            accountService.addRoleToUser("user2", "CUSTOMER_MANAGER");
            accountService.addRoleToUser("user3", "USER");
            accountService.addRoleToUser("user3", "PRODUCT_MANAGER");
            accountService.addRoleToUser("user4", "USER");
            accountService.addRoleToUser("user4", "BILLS_MANAGER");

        };

    }
}
