package fr.nduheron.poc.springrestapi.user.repository;

import fr.nduheron.poc.springrestapi.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
