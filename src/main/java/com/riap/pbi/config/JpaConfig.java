package com.riap.pbi.config;

import com.riap.pbi.dbms.persistence.jpa.ListingJpaAdapter;
import com.riap.pbi.dbms.persistence.jpa.UserAccountJpaAdapter;
import com.riap.pbi.dbms.repository.ListingRepository;
import com.riap.pbi.dbms.repository.UserAccountRepository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class JpaConfig {

    @Value("${app.datasource.url}")
    private String jdbcUrl;

    @Value("${app.datasource.username}")
    private String username;

    @Value("${app.datasource.password}")
    private String password;

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        Map<String, String> props = Map.of(
                "jakarta.persistence.jdbc.url", jdbcUrl,
                "jakarta.persistence.jdbc.user", username,
                "jakarta.persistence.jdbc.password", password
        );
        return Persistence.createEntityManagerFactory("riap-pu", props);
    }

    @Bean
    public ListingRepository listingRepository(EntityManagerFactory emf) {
        return new ListingJpaAdapter(emf);
    }

    @Bean
    public UserAccountRepository userAccountRepository(EntityManagerFactory emf) {
        return new UserAccountJpaAdapter(emf);
    }
}
