package com.cooksys.social_media_api.repositories;

import com.cooksys.social_media_api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //Need to call embedded object for derived queries. (i.e. Credentials is the embedded object)
    boolean existsByCredentialsUsername(String username);

    List<User> findAllByDeletedFalse();

    Optional<User> findByCredentialsUsername(String username);
    Optional<User> findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(String username, String password);



}
