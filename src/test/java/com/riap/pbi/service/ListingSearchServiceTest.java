package com.riap.pbi.service;

import com.riap.pbi.dbms.domain.*;
import com.riap.pbi.dbms.repository.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        Listing listing = Listing.builder()
                .id(1L).title("Test").rentCents(20000L).landlordId(1L)
                .city("台北市").propertyType(PropertyType.STUDIO).sizePing(10)
                .status(ListingStatus.AVAILABLE).build();

        when(listingRepository.search(any(ListingFilter.class))).thenReturn(List.of(listing));

        ListingFilter filter = ListingFilter.builder().city("台北市").build();
        List<Listing> result = service.search(filter);

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getTitle());
        verify(listingRepository).search(filter);
    }

    @Test
    void searchWithNullFilterUsesEmptyFilter() {
        when(listingRepository.search(any(ListingFilter.class))).thenReturn(List.of());

        List<Listing> result = service.search(null);

        assertTrue(result.isEmpty());
        verify(listingRepository).search(argThat(f ->
                f.city() == null && f.keyword() == null && f.page() == 0
        ));
    }

    @Test
    void searchReturnsEmptyListWhenNoResults() {
        when(listingRepository.search(any())).thenReturn(List.of());

        List<Listing> result = service.search(ListingFilter.empty());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
