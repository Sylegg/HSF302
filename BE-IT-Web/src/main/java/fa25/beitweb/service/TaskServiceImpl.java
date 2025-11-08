package fa25.beitweb.service;


import fa25.beitweb.entity.Room;
import fa25.beitweb.entity.User;
import fa25.beitweb.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import fa25.beitweb.entity.Task;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Override
    public void createTask(Task task) {
        taskRepository.save(task);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task getTaskById(long id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteTaskById(long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public void updateTask(Task task) {
        taskRepository.save(task);
    }

    @Override
    public Task findByAssignee(User user) {
        return taskRepository.findByAssignee(user);
    }

    @Override
    public List<Task> getTasksByAssignee(User user) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getAssignee() != null &&
                        task.getAssignee().getUserId().equals(user.getUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getTasksByRoom(Room room) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getRoom() != null &&
                        task.getRoom().getRoomId().equals(room.getRoomId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getTasksByCreator(User creator) {
        return taskRepository.findByCreator(creator);
    }
}
