package com.example.demo.security;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.AuthenticationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Filtre pour gérer l'authentification JWT
 * Traite les tentatives de connexion et génère un token JWT en cas de succès
 */
@RequiredArgsConstructor
public class  JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Tente d'authentifier l'utilisateur à partir de la requête
     * @param request La requête HTTP
     * @param response La réponse HTTP
     * @return L'objet Authentication en cas de succès
     * @throws AuthenticationException Si l'authentification échoue
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
        try {
            // Lecture du corps de la requête pour extraire les identifiants JSON
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword());

            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException("Échec de l'analyse de la demande d'authentification", e);
        }
    }

    /**
     * Appelé en cas d'authentification réussie
     * Génère un token JWT et l'envoie dans la réponse
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
        throws IOException, ServletException {

        // Récupération de l'utilisateur authentifié
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();

        // Génération du token JWT
        String jwt = jwtUtils.generateJwtToken(authResult);

        // Création de la réponse
        AuthenticationResponse authResponse = new AuthenticationResponse(
            jwt,
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail());

        // Configuration de la réponse
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Écriture de la réponse
        objectMapper.writeValue(response.getOutputStream(), authResponse);
    }

    /**
     * Appelé en cas d'échec d'authentification
     * Envoie un message d'erreur dans la réponse
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed)
        throws IOException, ServletException {

        // Configuration de la réponse
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Création du message d'erreur
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", "Authentification échouée: " + failed.getMessage());
        errorDetails.put("status", HttpServletResponse.SC_UNAUTHORIZED);

        // Écriture de la réponse
        objectMapper.writeValue(response.getOutputStream(), errorDetails);
    }
}
