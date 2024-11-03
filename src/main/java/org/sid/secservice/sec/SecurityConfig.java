package org.sid.secservice.sec;

import org.sid.secservice.sec.entities.AppUser;
import org.sid.secservice.sec.filters.JwtAuthenticationFilter;
import org.sid.secservice.sec.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AccountService accountService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        // Configurer le service pour charger les utilisateurs
        auth.userDetailsService(new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

                // Récupère l'utilisateur à partir du nom d'utilisateur via un service (accountService)
                AppUser appUser = accountService.loadUserByUsername(username);

                // Crée une liste de rôles (autorisations) pour cet utilisateur
                Collection<GrantedAuthority> authorities = new ArrayList<>();

                // Ajoute chaque rôle de l'utilisateur comme une autorisation (GrantedAuthority)
                appUser.getAppRoles().forEach(role -> {
                    authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
                });

                // Retourne un objet User avec le nom d'utilisateur, le mot de passe et les rôles de l'utilisateur
                return new User(appUser.getUsername(), appUser.getPassword(), authorities);
            }
        });

        // Ce service va chercher l'utilisateur avec ses informations d'identification
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.headers().frameOptions().disable(); // Pour utiliser H2 console
        http.authorizeRequests().antMatchers("/h2-console/**").permitAll();
        http.formLogin(); // formulaire de connexion
        http.authorizeRequests().anyRequest().authenticated();
     http.addFilter(new JwtAuthenticationFilter(authenticationManagerBean()));
    }
    @Bean // càd on peut injecter l'objet authenticationManager
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
