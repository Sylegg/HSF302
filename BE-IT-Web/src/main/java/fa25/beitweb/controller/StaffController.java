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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

        List<Room> rooms = roomService.getAllRooms();
        List<Task> allTasks = taskService.getAllTasks();

        // Lấy danh sách task do staff này tạo
        List<Task> recentTasks = taskService.getTasksByCreator(user);

        // Đếm số lượng task theo status
        long inProgressCount = allTasks.stream()
            .filter(t -> "IN_PROGRESS".equals(t.getStatus()))
            .count();
        long completedCount = allTasks.stream()
            .filter(t -> "COMPLETED".equals(t.getStatus()))
            .count();

        model.addAttribute("rooms", rooms);
        model.addAttribute("tasks", recentTasks);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("user", user);
        return "staff/dashboard";
    }

    // ============ ROOM MANAGEMENT ============
    @GetMapping("/rooms")
    public String listRooms(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STAFF".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("user", user);
        return "staff/rooms";
    }

    @GetMapping("/rooms/create")
    public String createRoomForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STAFF".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("room", new Room());
        model.addAttribute("user", user);
        return "staff/room-form";
    }

    @PostMapping("/rooms/save")
    public String saveRoom(@ModelAttribute Room room, HttpSession session, RedirectAttributes redirectAttributes) {
        User staff = (User) session.getAttribute("user");
        if (staff != null) {
            room.setCreator(staff);
            room.setCreatedAt(new Date());
            roomService.createRoom(room);
            redirectAttributes.addFlashAttribute("message", "Phòng đã được tạo thành công!");
        }
        return "redirect:/staff/rooms";
    }

    @GetMapping("/rooms/edit/{id}")
    public String editRoomForm(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STAFF".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }
        Room room = roomService.getRoomById(id);
        model.addAttribute("room", room);
        model.addAttribute("user", user);
        return "staff/room-form";
    }

    @PostMapping("/rooms/update/{id}")
    public String updateRoom(@PathVariable Long id, @ModelAttribute Room room, RedirectAttributes redirectAttributes) {
        Room existingRoom = roomService.getRoomById(id);
        if (existingRoom != null) {
            existingRoom.setRoomName(room.getRoomName());
            existingRoom.setDescription(room.getDescription());
            roomService.updateRoom(existingRoom);
            redirectAttributes.addFlashAttribute("message", "Phòng đã được cập nhật thành công!");
        }
        return "redirect:/staff/rooms";
    }

    @GetMapping("/rooms/delete/{id}")
    public String deleteRoom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roomService.deleteRoomById(id);
            redirectAttributes.addFlashAttribute("message", "Phòng đã được xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa phòng. Vui lòng xóa các task liên quan trước!");
        }
        return "redirect:/staff/rooms";
    }

    @GetMapping("/rooms/details/{id}")
    public String roomDetails(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STAFF".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }
        Room room = roomService.getRoomById(id);
        List<Task> roomTasks = taskService.getTasksByRoom(room);

        long inProgressCount = roomTasks.stream()
            .filter(t -> "IN_PROGRESS".equals(t.getStatus()))
            .count();
        long completedCount = roomTasks.stream()
            .filter(t -> "COMPLETED".equals(t.getStatus()))
            .count();

        model.addAttribute("room", room);
        model.addAttribute("tasks", roomTasks);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("user", user);
        return "staff/room-details";
    }

    @GetMapping("/tasks")
    public String listTasks(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STAFF".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("tasks", taskService.getAllTasks());
        model.addAttribute("user", user);
        return "staff/tasks";
    }

    @GetMapping("/tasks/create")
    public String createTaskForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STAFF".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("task", new Task());
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("members", userService.getAllUsers().stream()
                .filter(u -> "MEMBER".equalsIgnoreCase(u.getRole()))
                .collect(Collectors.toList()));
        model.addAttribute("user", user);
        return "staff/task-form";
    }

    @PostMapping("/tasks/save")
    public String saveTask(@ModelAttribute Task task,
                          @RequestParam Long roomId,
                          @RequestParam(required = false) Integer assigneeId,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        User staff = (User) session.getAttribute("user");
        Room room = roomService.getRoomById(roomId);

        if (staff != null && room != null) {
            task.setCreator(staff);
            task.setRoom(room);

            if (assigneeId != null) {
                User assignee = userService.getUserById(assigneeId);
                task.setAssignee(assignee);
            }

            taskService.createTask(task);
            redirectAttributes.addFlashAttribute("message", "Task đã được tạo thành công!");
        }
        return "redirect:/staff/tasks";
    }

    @GetMapping("/tasks/edit/{id}")
    public String editTaskForm(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STAFF".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }
        Task task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("members", userService.getAllUsers().stream()
                .filter(u -> "MEMBER".equalsIgnoreCase(u.getRole()))
                .collect(Collectors.toList()));
        model.addAttribute("user", user);
        return "staff/task-form";
    }

    @PostMapping("/tasks/update/{id}")
    public String updateTask(@PathVariable Long id,
                            @ModelAttribute Task task,
                            @RequestParam Long roomId,
                            @RequestParam(required = false) Integer assigneeId,
                            RedirectAttributes redirectAttributes) {
        Task existingTask = taskService.getTaskById(id);
        Room room = roomService.getRoomById(roomId);

        if (existingTask != null && room != null) {
            existingTask.setTitle(task.getTitle());
            existingTask.setDescription(task.getDescription());
            existingTask.setStatus(task.getStatus());
            existingTask.setProgressPercent(task.getProgressPercent());
            existingTask.setDueDate(task.getDueDate());
            existingTask.setRoom(room);

            if (assigneeId != null) {
                User assignee = userService.getUserById(assigneeId);
                existingTask.setAssignee(assignee);
            } else {
                existingTask.setAssignee(null);
            }

            taskService.updateTask(existingTask);
            redirectAttributes.addFlashAttribute("message", "Task đã được cập nhật thành công!");
        }
        return "redirect:/staff/tasks";
    }

    @GetMapping("/tasks/delete/{id}")
    public String deleteTask(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        taskService.deleteTaskById(id);
        redirectAttributes.addFlashAttribute("message", "Task đã được xóa thành công!");
        return "redirect:/staff/tasks";
    }

    @GetMapping("/tasks/details/{id}")
    public String taskDetails(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STAFF".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }
        Task task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        model.addAttribute("user", user);
        return "staff/task-details";
    }

    @GetMapping("/members")
    public String listMembers(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"STAFF".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        List<User> members = userService.getAllUsers().stream()
                .filter(u -> "MEMBER".equalsIgnoreCase(u.getRole()))
                .collect(Collectors.toList());

        // Lấy tất cả tasks để tính toán
        List<Task> allTasks = taskService.getAllTasks();

        // Tạo Map để lưu số công việc và hoàn thành cho từng member
        java.util.Map<Integer, Long> taskCountMap = new java.util.HashMap<>();
        java.util.Map<Integer, Long> completedCountMap = new java.util.HashMap<>();

        for (User member : members) {
            long taskCount = allTasks.stream()
                .filter(t -> t.getAssignee() != null && t.getAssignee().getUserId().equals(member.getUserId()))
                .count();

            long completedCount = allTasks.stream()
                .filter(t -> t.getAssignee() != null &&
                            t.getAssignee().getUserId().equals(member.getUserId()) &&
                            "COMPLETED".equals(t.getStatus()))
                .count();

            taskCountMap.put(member.getUserId(), taskCount);
            completedCountMap.put(member.getUserId(), completedCount);
        }

        model.addAttribute("members", members);
        model.addAttribute("taskCountMap", taskCountMap);
        model.addAttribute("completedCountMap", completedCountMap);
        model.addAttribute("user", user);
        return "staff/members";
    }
}
