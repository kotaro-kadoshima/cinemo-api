package com.cinemo.api.repository;

import com.cinemo.api.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// providers
@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
}