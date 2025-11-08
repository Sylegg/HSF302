package fa25.beitweb.controller;

import fa25.beitweb.entity.Task;
import fa25.beitweb.entity.User;
import fa25.beitweb.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"MEMBER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        List<Task> myTasks = taskService.getTasksByAssignee(user);

        // Đếm số lượng task theo status
        long inProgressCount = myTasks.stream()
            .filter(t -> "IN_PROGRESS".equals(t.getStatus()))
            .count();
        long completedCount = myTasks.stream()
            .filter(t -> "COMPLETED".equals(t.getStatus()))
            .count();

        model.addAttribute("tasks", myTasks);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("user", user);
        return "member/dashboard";
    }

    @GetMapping("/tasks")
    public String listMyTasks(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"MEMBER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        List<Task> myTasks = taskService.getTasksByAssignee(user);
        model.addAttribute("tasks", myTasks);
        model.addAttribute("user", user);
        return "member/tasks";
    }

    @GetMapping("/tasks/details/{id}")
    public String taskDetails(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"MEMBER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        Task task = taskService.getTaskById(id);

        // Ensure the member can only view their own tasks
        if (task != null && task.getAssignee() != null &&
            task.getAssignee().getUserId().equals(user.getUserId())) {
            model.addAttribute("task", task);
            model.addAttribute("user", user);
            return "member/task-details";
        }

        return "redirect:/member/dashboard";
    }

    @PostMapping("/tasks/update-progress/{id}")
    public String updateProgress(@PathVariable Long id,
                                 @RequestParam Integer progressPercent,
                                 @RequestParam String status,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        Task task = taskService.getTaskById(id);

        if (task != null && task.getAssignee() != null &&
            task.getAssignee().getUserId().equals(user.getUserId())) {
            task.setProgressPercent(progressPercent);
            task.setStatus(status);
            taskService.updateTask(task);
            redirectAttributes.addFlashAttribute("message", "Tiến độ đã được cập nhật thành công!");
        }
        return "redirect:/member/tasks";
    }

    @GetMapping("/tasks/update/{id}")
    public String updateTaskForm(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"MEMBER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        Task task = taskService.getTaskById(id);

        // Ensure the member can only update their own tasks
        if (task != null && task.getAssignee() != null &&
            task.getAssignee().getUserId().equals(user.getUserId())) {
            model.addAttribute("task", task);
            model.addAttribute("user", user);
            return "member/task-update";
        }

        return "redirect:/member/dashboard";
    }
}
