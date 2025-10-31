package fa25.beitweb.repository;

import fa25.beitweb.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    User findByEmailAndPassword(String email, String password);
    User findByEmail(String email);

}
