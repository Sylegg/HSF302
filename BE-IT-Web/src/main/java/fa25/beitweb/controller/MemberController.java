package fa25.beitweb.controller;

import fa25.beitweb.entity.Task;
import fa25.beitweb.entity.User;
import fa25.beitweb.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

        Task task = taskService.findByAssignee(user);
        model.addAttribute("task", task);
        model.addAttribute("user", user);
        return "member/dashboard";
    }


    @PostMapping("/tasks/update-progress")
    public String updateProgress(@RequestParam Long taskId,
                                 @RequestParam Integer progressPercent,
                                 @RequestParam String status) {
        Task task = taskService.getTaskById(taskId);
        if (task != null) {
            task.setProgressPercent(progressPercent);
            task.setStatus(status);
            taskService.updateTask(task);
        }
        return "redirect:/member/dashboard";
    }
}
