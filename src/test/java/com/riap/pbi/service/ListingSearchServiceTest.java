package com.riap.pbi.service;

import com.riap.listing.domain.model.ListingEntity;
import com.riap.listing.domain.repository.ListingRepository;
import com.riap.pbi.dbms.domain.ListingFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingSearchServiceTest {

    @Mock
    private ListingRepository listingRepository;

    private ListingSearchService service;

    @BeforeEach
    void setUp() {
        service = new ListingSearchService(listingRepository);
    }

    @Test
    void searchDelegatesToRepository() {
        ListingEntity listing = ListingEntity.builder()
                .title("Test")
                .city("台北市")
                .build();

        Page<ListingEntity> page = new PageImpl<>(List.of(listing));

        when(listingRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        ListingFilter filter = ListingFilter.builder().city("台北市").build();
        List<ListingEntity> result = service.search(filter);

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getTitle());
        verify(listingRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void searchWithNullFilterUsesEmptyFilter() {
        Page<ListingEntity> page = new PageImpl<>(List.of());
        when(listingRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        List<ListingEntity> result = service.search(null);

        assertTrue(result.isEmpty());
        verify(listingRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void searchReturnsEmptyListWhenNoResults() {
        Page<ListingEntity> page = new PageImpl<>(List.of());
        when(listingRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        List<ListingEntity> result = service.search(ListingFilter.empty());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
