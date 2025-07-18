package demo02.demo.jwtutils;

import demo02.demo.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// This filter is for checking whether the request is authenticated and what it is authorized to do (thorugh token)
@Component
public class JwtFilter extends OncePerRequestFilter { // So this filter goes off once per request
    @Autowired private JwtUserDetailsService userDetailsService;
    @Autowired private TokenManager tokenManager;
    @Autowired private UserRepository userRepo;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException
    {
        String path = request.getServletPath();

        // Skip JWT check for login and register urls
        if (path.equals("/api/users/login") || path.equals("/api/users/register")) {
            System.out.println("skipping");
            filterChain.doFilter(request, response);
            return;
        }


        // Raise an exception when token is not found
        String tokenHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")){
            token = tokenHeader.substring(7);
            try {
                username = tokenManager.getUsernameFromToken(token); // ?
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        } else System.out.println("Bearer String not found in token");
        System.out.println("tokenHeader: "+ tokenHeader);
        System.out.println("token: "+ token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (tokenManager.validateJwtToken(token, userDetails)){ // ??? userDetails is found using
                // the username decoded from token. Then validateJwtToken() returns true if
                // username decoded from token param matches username from userDetails??
                UsernamePasswordAuthenticationToken
                        authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        if (path.matches("/api/users/[0-9]+")){
            Long userId = Long.parseLong(path.substring(path.lastIndexOf("/")+1));
            System.out.println("User ID: "+ userId);
            Long tokenId = userRepo.findByUsername(tokenManager.getUsernameFromToken(token)).get().getId();
            System.out.println("User ID in token: "+tokenId);

            if (userId.compareTo(tokenId) != 0){
                System.out.println("You are not authorized to access this information");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
