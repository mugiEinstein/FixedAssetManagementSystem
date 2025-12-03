package ztt.fixedassetmanagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta. servlet.http.HttpServletRequest;
import jakarta.servlet. http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok. extern.slf4j.Slf4j;
import org. springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework. security.core.authority.SimpleGrantedAuthority;
import org.springframework.security. core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework. stereotype.Component;
import org.springframework. util.StringUtils;
import org.springframework. web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util. Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider. validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                String userId = tokenProvider.getUserIdFromToken(jwt);
                String role = tokenProvider.getRoleFromToken(jwt);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections. singletonList(new SimpleGrantedAuthority("ROLE_" + role. toUpperCase()))
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                authentication.setDetails(userId); // 存储userId供后续使用

                SecurityContextHolder.getContext(). setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken. substring(7);
        }
        return null;
    }
}