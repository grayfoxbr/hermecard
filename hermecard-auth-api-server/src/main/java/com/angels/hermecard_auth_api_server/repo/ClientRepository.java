package com.angels.hermecard_auth_api_server.repo;

import java.util.Optional;

import com.angels.hermecard_auth_api_server.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByClientId(String clientId);
}