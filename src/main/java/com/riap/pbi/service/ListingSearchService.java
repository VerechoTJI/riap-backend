package com.riap.pbi.service;

import com.riap.pbi.dbms.domain.Listing;
import com.riap.pbi.dbms.domain.ListingFilter;
import com.riap.pbi.dbms.repository.ListingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListingSearchService {

    private final ListingRepository listingRepository;

    public ListingSearchService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public List<Listing> search(ListingFilter filter) {
        ListingFilter effective = filter != null ? filter : ListingFilter.empty();
        return listingRepository.search(effective);
    }
}
