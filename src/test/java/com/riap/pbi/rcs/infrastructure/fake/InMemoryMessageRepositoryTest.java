package com.riap.pbi.rcs.infrastructure.fake;

import com.riap.pbi.rcs.port.MessageRepository;
import com.riap.pbi.rcs.port.MessageRepositoryContractTest;
import org.junit.jupiter.api.BeforeEach;

class InMemoryMessageRepositoryTest extends MessageRepositoryContractTest {

    private InMemoryMessageRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryMessageRepository();
    }

    @Override
    protected MessageRepository getRepository() {
        return repository;
    }
}
