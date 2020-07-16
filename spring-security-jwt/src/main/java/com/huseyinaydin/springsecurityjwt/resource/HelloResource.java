package com.huseyinaydin.springsecurityjwt.resource;

import com.huseyinaydin.springsecurityjwt.model.AuthenticationRequest;
import com.huseyinaydin.springsecurityjwt.model.AuthenticationResponse;
import com.huseyinaydin.springsecurityjwt.services.MyUserDetailsService;
import com.huseyinaydin.springsecurityjwt.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloResource {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @RequestMapping(path = "/hello", method = RequestMethod.GET)
    public String hello() {
        return "Hello World";
    }

    @RequestMapping(path = "/selam", method = RequestMethod.GET)
    public String selam() {
        return "Selamlar";
    }

    @RequestMapping(path = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        System.out.println("createAuthenticationToken metodu");
        System.out.println(authenticationRequest.toString());
        if (authenticationRequest == null)
            return ResponseEntity.ok("Kullanıcı adı ve şifre boş lütfen doldurun");
        if (authenticationRequest.getUserName() == null
                || authenticationRequest.getPassword() == null
                || authenticationRequest.getPassword().isEmpty()
                || authenticationRequest.getUserName().isEmpty())
            return ResponseEntity.ok("Kullanıcı adı veya şifre boş lütfen doldurun");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUserName(), authenticationRequest.getPassword()));
            System.err.println("try de");
            System.out.println("try de");
            System.err.println(authenticationRequest.getUserName() + " - " + authenticationRequest.getPassword());
        } catch (BadCredentialsException ex) {
            System.err.println("catch de " + ex.getMessage());
            throw new BadCredentialsException("Kullanıcı adı veya şifre yanlış");
            //throw new Exception("Kullanıcı adı veya şifre yanlış", ex);
        }
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticationRequest.getUserName());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
