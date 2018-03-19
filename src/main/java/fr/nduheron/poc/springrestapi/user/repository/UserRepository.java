package fr.nduheron.poc.springrestapi.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.nduheron.poc.springrestapi.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
