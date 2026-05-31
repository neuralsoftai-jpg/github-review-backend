package com.codereview.githubreview.security;

import com.codereview.githubreview.entity.User;
import com.codereview.githubreview.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final String frontendUrl = "https://github-review-app.vercel.app/auth/callback](https://github-review-app.vercel.app/auth/callback";

    public OAuth2LoginSuccessHandler(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String email = null;

        // 1. SAFELY Extract Email (Crash-proof logic)
        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oAuth2User.getAttributes();

            // Check lagaya taaki NPE (NullPointerException) na aaye
            if (attributes != null) {
                email = (String) attributes.get("email");

                // GitHub Edge Case: Agar email private hai, toh GitHub 'login' username deta hai
                if (email == null && attributes.containsKey("login")) {
                    email = attributes.get("login") + "@github.com"; // Fallback email
                }
            }
        }

        // Agar upar wale logic se email na mile, toh seedha authentication object se nikalo
        if (email == null) {
            email = authentication.getName();
        }

        // 2. Final Null Check: Agar sab fail ho jaye, toh frontend par safely bhejo
        if (email == null || email.isEmpty()) {
            getRedirectStrategy().sendRedirect(request, response, "http://localhost:5173/login?error=email_missing_from_provider");
            return;
        }

        // 3. Database lookup
        String finalEmail = email; // Lambda function ke liye effectively final
        User user = userRepository.findByEmail(finalEmail)
                .orElseThrow(() -> new RuntimeException("User not found in DB with email: " + finalEmail));

        // 4. Multi-tenant Webhook ID allocation
        if (user.getWebhookId() == null) {
            user.setWebhookId(UUID.randomUUID().toString());
            user.setWebhookSecret(UUID.randomUUID().toString().replace("-", ""));
            userRepository.save(user);
        }

        // 5. UserDetails object banao aur JWT Generate karo
        com.codereview.githubreview.security.CustomUserDetails userDetails = new com.codereview.githubreview.security.CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);

        // 6. Redirect URL
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
