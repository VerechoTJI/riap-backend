package com.riap.dbms;

import com.riap.user.domain.model.UserAccountEntity;
import com.riap.user.domain.model.UserRole;
import com.riap.user.domain.model.UserStatus;
import com.riap.user.domain.repository.UserAccountRepository;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DBMS transaction and connection-pool behaviour, exercised through the Spring Boot
 * stack on the default (H2) datasource.
 *
 * <ul>
 *   <li>STD DBMS-TC04 (ACID, DBMS-N-03/N-08): a transaction that fails midway rolls
 *       back, leaving no partial data.</li>
 *   <li>STD DBMS-TC05 (DBMS-N-05/N-10): connections are served by a HikariCP pool that
 *       stays stable under more concurrent callers than pooled connections.</li>
 * </ul>
 */
@SpringBootTest
class DbmsTransactionAndPoolTest {

    @Autowired
    private UserAccountRepository users;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private DataSource dataSource;

    @Test
    void transactionRollbackLeavesNoPartialData() {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        String login = "rollback-" + UUID.randomUUID() + "@example.com";

        assertThrows(RuntimeException.class, () -> tx.executeWithoutResult(status -> {
            users.save(UserAccountEntity.builder()
                    .loginIdentifier(login)
                    .passwordHash("hash")
                    .role(UserRole.TENANT)
                    .status(UserStatus.ACTIVE)
                    .build());
            // Fail after the insert so the surrounding transaction must roll back.
            throw new RuntimeException("forced failure");
        }));

        assertFalse(users.existsByLoginIdentifier(login), "rolled-back insert must leave no row");
    }

    @Test
    void connectionsAreServedByHikariPool() {
        assertInstanceOf(HikariDataSource.class, unwrapHikari(dataSource),
                "DBMS must serve connections from a HikariCP pool");
    }

    @Test
    void poolStaysHealthyUnderConcurrentCallers() throws Exception {
        int callers = 50; // more than the default HikariCP pool size (10) to force queuing
        ExecutorService executor = Executors.newFixedThreadPool(callers);
        List<Callable<Boolean>> tasks = new ArrayList<>();
        for (int i = 0; i < callers; i++) {
            tasks.add(() -> {
                try (var connection = dataSource.getConnection();
                     var st = connection.prepareStatement("SELECT 1");
                     var rs = st.executeQuery()) {
                    return rs.next();
                }
            });
        }
        try {
            List<Future<Boolean>> results = executor.invokeAll(tasks, 30, TimeUnit.SECONDS);
            for (Future<Boolean> result : results) {
                assertTrue(result.get(), "every pooled connection query should succeed");
            }
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        }
    }

    private static DataSource unwrapHikari(DataSource ds) {
        try {
            return ds.isWrapperFor(HikariDataSource.class) ? ds.unwrap(HikariDataSource.class) : ds;
        } catch (Exception e) {
            return ds;
        }
    }
}
