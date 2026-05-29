package com.codereview.githubreview.security;

import com.codereview.githubreview.entity.OAuthAccount;
import com.codereview.githubreview.entity.User;
import com.codereview.githubreview.entity.enums.AuthProvider;
import com.codereview.githubreview.entity.enums.Role;
import com.codereview.githubreview.repository.OAuthAccountRepository;
import com.codereview.githubreview.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final OAuthAccountRepository oAuthAccountRepository;

    public CustomOAuth2UserService(UserRepository userRepository, OAuthAccountRepository oAuthAccountRepository) {
        this.userRepository = userRepository;
        this.oAuthAccountRepository = oAuthAccountRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Pehle default Spring method se Google/GitHub ka data fetch karo
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. Pata karo ki user Google se aaya hai ya GitHub se
        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        AuthProvider provider = AuthProvider.valueOf(registrationId);

        // 3. Data extract karo (Google aur GitHub ke JSON keys alag hote hain)
        String email = "";
        String name = "";
        String providerUserId = oAuth2User.getName(); // Usually ID hota hai

        if (provider == AuthProvider.GOOGLE) {
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("name");
        } else if (provider == AuthProvider.GITHUB) {
            // GitHub kabhi kabhi email private rakhta hai, usko handle karna zaroori hai
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("login"); // GitHub username
            if (email == null) {
                email = name + "@github.com"; // Dummy fallback agar email hidden hai
            }
        }

        // 4. Database mein check karo
        User user;
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // TODO: Yahan hum check kar sakte hain ki same email par multiple providers linked hain ya nahi
        } else {
            // NAYA USER BANAYEIN
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setProvider(provider);
            user.setRole(Role.ROLE_USER);
            user = userRepository.save(user);

            // Naya OAuth Account link karein
            OAuthAccount oAuthAccount = new OAuthAccount();
            oAuthAccount.setUser(user);
            oAuthAccount.setProvider(provider);
            oAuthAccount.setProviderUserId(providerUserId);
            oAuthAccountRepository.save(oAuthAccount);
        }

        // 5. Spring Security ko humara CustomUserDetails return karo
        return new CustomUserDetails(user); // Note: CustomUserDetails abhi OAuth2User implement nahi karta, par hum ise simply use karenge auth ke liye
    }
}