package fa25.beitweb.repository;

import fa25.beitweb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import fa25.beitweb.entity.Task;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
    Task findByAssignee(User user);
}
