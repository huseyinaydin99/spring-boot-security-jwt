package com.huseyinaydin.springsecurityjwt.filters;

import com.huseyinaydin.springsecurityjwt.services.MyUserDetailsService;
import com.huseyinaydin.springsecurityjwt.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("Do filter internal çalıştı ----");
        System.err.println("Do filter internal çalıştı ----");
        final String authorizationHeader = httpServletRequest.getHeader("Authorization");
        String userName = null;
        String jwt = null;
        if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer")){
            jwt = authorizationHeader.substring(7);
            userName = jwtUtil.extractUserName(jwt);
            System.err.println("1. ifte");
        }
        if(userName!=null && SecurityContextHolder.getContext().getAuthentication() == null){
            System.err.println("2. ifte");
            UserDetails userDetails = myUserDetailsService.loadUserByUsername(userName);
            if(jwtUtil.validateToken(jwt,userDetails)){
                System.err.println("3. ifte");
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null
                        ,userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken); // /hello ve /selam a erişimi açan kod
            }
        }
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
