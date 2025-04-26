package com.example.demo.security;

import com.example.demo.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre pour gérer l'autorisation basée sur JWT
 * Vérifie la validité du token JWT et configure l'authentification
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Filtre chaque requête pour vérifier et valider le token JWT
     * Met à jour le contexte de sécurité si le token est valide
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            // Extraction du token JWT de la requête
            String jwt = parseJwt(request);

            // Vérification de la validité du token
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // Extraction du nom d'utilisateur du token
                String username = jwtUtils.getUsernameFromJwtToken(jwt);

                // Chargement des détails de l'utilisateur
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Création de l'objet d'authentification
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Ajout des détails de la requête
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Mise à jour du contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Impossible d'authentifier l'utilisateur: {}", e.getMessage());
        }

        // Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }

    /**
     * Extrait le token JWT de l'en-tête Authorization
     * @param request La requête HTTP
     * @return Le token JWT extrait ou null
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
