package com.vaishnavi.giftportal.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.vaishnavi.giftportal.entity.User;
import com.vaishnavi.giftportal.repository.UserRepository;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // No header or not a bearer token? Just continue the chain.
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            // If token is expired/invalid, we DO NOT throw — we simply skip auth and continue.
            if (jwtUtil.isTokenExpired(token)) {
                // Clear any context and move on (endpoint may still be public).
                SecurityContextHolder.clearContext();
                chain.doFilter(request, response);
                return;
            }

            String username = jwtUtil.getUsernameFromToken(token); // subject is email
            String role = jwtUtil.getRoleFromToken(token);

            // Look up by EMAIL (subject), not name.
            Optional<User> userOpt = userRepository.findByEmail(username);
            if (userOpt.isEmpty()) {
                SecurityContextHolder.clearContext();
                chain.doFilter(request, response);
                return;
            }

            List<SimpleGrantedAuthority> authorities =
                    Collections.singletonList(new SimpleGrantedAuthority(role));

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (ExpiredJwtException e) {
            // Expired token — do not fail the request. Just proceed unauthenticated.
            SecurityContextHolder.clearContext();
        } catch (JwtException | IllegalArgumentException e) {
            // Malformed/invalid token — proceed unauthenticated.
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}
