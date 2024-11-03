package org.sid.secservice.sec.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    //s'exceute a chaque  fois y'a une requete , on intercepte la requete avant d'atteindre le dispatcher servlet
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //je suppose que request contient un header nommé authorization
        String authorizationToken = request.getHeader("Authorization");
        if (authorizationToken != null && authorizationToken.startsWith("Bearer ")) {
            try {
                String jwt = authorizationToken.substring(7); //ignorer la chaine 'bearer '
                //on a utilisé le meme secret pour signer le TOKEN , c'est la meme pour verifier  (crypter /decrypter)
                Algorithm algorithm = Algorithm.HMAC256("mySecretNeha");
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(jwt); /// contient les  CLAIMS à partir de celle ci je peux recuperer le username etc....
                String username = decodedJWT.getSubject();
                String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                for (String role : roles) {
                    authorities.add(new SimpleGrantedAuthority(role));
                }
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                //ici càd que : j'intercepte la requete avec try catch ,je fais les verifications , puis on passe au filtre suivant
                filterChain.doFilter(request, response);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (Exception e) {
                response.setHeader("error-message", e.getMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }

        }
        else {
            //tu passes sans authentification , ici spring security va  verifie si la ressource necessite une auth ou pas
            filterChain.doFilter(request, response);
        }

    }
}
