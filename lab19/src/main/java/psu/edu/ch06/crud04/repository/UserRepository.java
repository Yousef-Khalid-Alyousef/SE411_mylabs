package psu.edu.ch06.crud04.repository;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import psu.edu.ch06.crud04.model.User;
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
