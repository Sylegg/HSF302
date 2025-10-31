package fa25.beitweb.service;


import fa25.beitweb.entity.User;
import fa25.beitweb.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import fa25.beitweb.entity.Task;
import org.springframework.stereotype.Service;

import java.util.List;
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
        Task existingTask = taskRepository.findById(task.getTaskId()).orElse(null);

        if (existingTask != null) {
          taskRepository.save(existingTask);
        }
    }

    @Override
    public Task findByAssignee(User user) {
        return taskRepository.findByAssignee(user);
    }
}
