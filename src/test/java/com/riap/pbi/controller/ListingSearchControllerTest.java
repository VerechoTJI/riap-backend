package com.riap.pbi.controller;

import com.riap.pbi.dbms.domain.*;
import com.riap.pbi.service.ListingSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ListingSearchController.class)
class ListingSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ListingSearchService listingSearchService;

    @Test
    void getListingsReturns200WithEmptyArray() throws Exception {
        when(listingSearchService.search(argThat(f -> f != null))).thenReturn(List.of());

        mockMvc.perform(get("/api/listings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.listings").isArray())
                .andExpect(jsonPath("$.listings.length()").value(0));
    }

    @Test
    void getListingsWithCityParamFiltersCorrectly() throws Exception {
        Listing listing = Listing.builder()
                .id(1L).title("台北套房").rentCents(18000L).landlordId(1L)
                .city("台北市").propertyType(PropertyType.STUDIO).sizePing(8)
                .status(ListingStatus.AVAILABLE).build();

        when(listingSearchService.search(argThat(f -> "台北市".equals(f.city()))))
                .thenReturn(List.of(listing));

        mockMvc.perform(get("/api/listings").param("city", "台北市"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.listings.length()").value(1))
                .andExpect(jsonPath("$.listings[0].title").value("台北套房"))
                .andExpect(jsonPath("$.listings[0].city").value("台北市"));
    }

    @Test
    void getListingsWithRentRangeParams() throws Exception {
        when(listingSearchService.search(argThat(f ->
                f.minRentCents() != null && f.minRentCents() == 10000L &&
                f.maxRentCents() != null && f.maxRentCents() == 30000L
        ))).thenReturn(List.of());

        mockMvc.perform(get("/api/listings")
                        .param("minRent", "10000")
                        .param("maxRent", "30000"))
                .andExpect(status().isOk());
    }

    @Test
    void getListingsWithPaginationParams() throws Exception {
        when(listingSearchService.search(argThat(f -> f.page() == 1 && f.size() == 5)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/listings")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk());
    }

    @Test
    void getListingsResponseIncludesRentAndPropertyType() throws Exception {
        Listing listing = Listing.builder()
                .id(2L).title("Apt").rentCents(25000L).landlordId(1L)
                .city("新北市").propertyType(PropertyType.APARTMENT).sizePing(20)
                .status(ListingStatus.AVAILABLE).build();

        when(listingSearchService.search(argThat(f -> f != null))).thenReturn(List.of(listing));

        mockMvc.perform(get("/api/listings"))
                .andExpect(jsonPath("$.listings[0].rentCents").value(25000))
                .andExpect(jsonPath("$.listings[0].propertyType").value("APARTMENT"))
                .andExpect(jsonPath("$.listings[0].id").value(2));
    }
}
