package fa25.beitweb.service;

import fa25.beitweb.entity.Room;
import fa25.beitweb.entity.Task;
import fa25.beitweb.entity.User;

import java.util.List;

public interface TaskService {
    public void createTask(Task task);
    public List<Task> getAllTasks();
    public Task getTaskById(long id);
    public void deleteTaskById(long id);
    public void updateTask(Task task);
    public Task findByAssignee(User user);
    public List<Task> getTasksByAssignee(User user);
    public List<Task> getTasksByRoom(Room room);
    public List<Task> getTasksByCreator(User creator);
}
