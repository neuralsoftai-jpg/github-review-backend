package com.codereview.githubreview.repository;

import com.codereview.githubreview.entity.OAuthAccount;
import com.codereview.githubreview.entity.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {
    Optional<OAuthAccount> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);
}