package org.sid.secservice.sec.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("attemptAuthentication");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println(username);
        System.out.println(password);
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        //authenticate qui va declencher l'operation d'auth  , va faire appel à UserDetails et ainsi de suite
        return authenticationManager.authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication");
        //getPrincipal permet de retourner l'utilisateur authentifié
        User user = (User) authResult.getPrincipal();
        //Pour calculer la signature  du JWT
        Algorithm algorithm = Algorithm.HMAC256("mySecretNeha");
        String jwtaccessToken = JWT.create()
                .withSubject(user.getUsername())
                //le pwd va expirer dans 9 min
                .withExpiresAt(new Date(System.currentTimeMillis() + 9 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(ga -> ga.getAuthority()).collect(Collectors.toList()))  //recupere la liste des roles et la convertir en liste du String
                .sign(algorithm);

        response.setHeader("Authorization", jwtaccessToken);
    }
}
