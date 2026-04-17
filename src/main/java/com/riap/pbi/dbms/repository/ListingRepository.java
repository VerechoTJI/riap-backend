package com.riap.pbi.dbms.repository;

import com.riap.pbi.dbms.domain.Listing;

import java.util.Optional;

public interface ListingRepository {

    Listing save(Listing listing);

    Optional<Listing> findById(Long id);

    void deleteById(Long id);
}
