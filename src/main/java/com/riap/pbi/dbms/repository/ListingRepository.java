package com.riap.pbi.dbms.repository;

import com.riap.pbi.dbms.domain.Listing;
import com.riap.pbi.dbms.domain.ListingFilter;

import java.util.List;
import java.util.Optional;

public interface ListingRepository {

    Listing save(Listing listing);

    Optional<Listing> findById(Long id);

    void deleteById(Long id);

    List<Listing> search(ListingFilter filter);
}
