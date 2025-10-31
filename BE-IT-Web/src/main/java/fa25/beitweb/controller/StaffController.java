package fa25.beitweb.controller;

import fa25.beitweb.entity.Room;
import fa25.beitweb.entity.Task;
import fa25.beitweb.entity.User;
import fa25.beitweb.service.RoomService;
import fa25.beitweb.service.TaskService;
import fa25.beitweb.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STAFF".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("tasks", taskService.getAllTasks());
        model.addAttribute("user", user);
        return "staff/dashboard";
    }

    @GetMapping("/tasks/create")
    public String createTask(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("rooms", roomService.getAllRooms());
        return "staff/task-form";
    }

    @PostMapping("/tasks/save")
    public String saveTask(@ModelAttribute Task task, HttpSession session) {
        User staff = (User) session.getAttribute("user");
        if (staff != null) {
            task.setCreator(staff);
        }
        taskService.createTask(task);
        return "redirect:/staff/dashboard";
    }

    @GetMapping("/tasks/edit/{id}")
    public String editTask(@PathVariable Long id, Model model) {
        model.addAttribute("task", taskService.getTaskById(id));
        model.addAttribute("rooms", roomService.getAllRooms());
        return "staff/task-form";
    }

    @GetMapping("/tasks/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return "redirect:/staff/dashboard";
    }

}
