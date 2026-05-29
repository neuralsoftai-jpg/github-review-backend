package com.codereview.githubreview.security;

import com.codereview.githubreview.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User; // NAYA IMPORT

import java.util.Collection;
import java.util.Collections;
import java.util.Map; // NAYA IMPORT

// 🚨 NAYA: Ab yeh UserDetails aur OAuth2User DONO implement karta hai
public class CustomUserDetails implements UserDetails, OAuth2User {
    private final User user;
    private Map<String, Object> attributes; // Google/GitHub se aane wala data

    // Constructor 1: Standard Email/Password Login ke liye
    public CustomUserDetails(User user) {
        this.user = user;
    }

    // Constructor 2: Google/GitHub OAuth2 Login ke liye
    public CustomUserDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    public User getUser() {
        return user;
    }

    // ==========================================
    // UserDetails Methods (Spring Security Core)
    // ==========================================
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public String getUsername() { return user.getEmail(); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    // ==========================================
    // OAuth2User Methods (Google / GitHub)
    // ==========================================
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(user.getId());
    }
}