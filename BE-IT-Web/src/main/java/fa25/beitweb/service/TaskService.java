package fa25.beitweb.service;

import fa25.beitweb.entity.Task;
import fa25.beitweb.entity.User;

import java.util.List;
import java.util.Map;

public interface TaskService {
    public void createTask(Task task);
    public List<Task> getAllTasks();
    public Task getTaskById(long id);
    public void deleteTaskById(long id);
    public void updateTask(Task task);
    public Task findByAssignee(User user);
}
