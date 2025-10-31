package fa25.beitweb.config;

import fa25.beitweb.entity.Room;
import fa25.beitweb.entity.Task;
import fa25.beitweb.entity.User;
import fa25.beitweb.repository.RoomRepository;
import fa25.beitweb.repository.TaskRepository;
import fa25.beitweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.findAll().isEmpty()) {
            return;
        }

        User staff = new User(null, "staff1@gmail.com", "123", "Staff Leader", "STAFF", null, null, null);
        userRepository.save(staff);

        User m1 = new User(null, "member1@gmail.com", "123", "Nguyễn Văn A", "MEMBER", null, null, null);
        User m2 = new User(null, "member2@gmail.com", "123", "Trần Thị B", "MEMBER", null, null, null);
        User m3 = new User(null, "member3@gmail.com", "123", "Lê Văn C", "MEMBER", null, null, null);
        User m4 = new User(null, "member4@gmail.com", "123", "Phạm Thị D", "MEMBER", null, null, null);
        userRepository.save(m1);
        userRepository.save(m2);
        userRepository.save(m3);
        userRepository.save(m4);


        Room r1 = new Room(null, "Phòng Dự Án Website", "Phòng chuyên phát triển website", new Date(), staff, null);
        Room r2 = new Room(null, "Phòng Dự Án Mobile", "Phòng phát triển ứng dụng di động", new Date(), staff, null);
        roomRepository.save(r1);
        roomRepository.save(r2);


        Task t1 = new Task(null, "Thiết kế giao diện", "Làm UI cho website chính", "IN_PROGRESS", 40,
                new Date(), new Date(), new Date(), m1, staff, r1);
        Task t2 = new Task(null, "Lập trình backend", "Tạo API cho dự án", "OPEN", 0,
                new Date(), new Date(), new Date(), m2, staff, r1);
        Task t3 = new Task(null, "Kiểm thử hệ thống", "Test toàn bộ module chức năng", "OPEN", 0,
                new Date(), new Date(), new Date(), m3, staff, r1);
        Task t4 = new Task(null, "Viết tài liệu dự án", "Tạo hướng dẫn sử dụng hệ thống", "OPEN", 0,
                new Date(), new Date(), new Date(), m4, staff, r2);
        taskRepository.save(t1);
        taskRepository.save(t2);
        taskRepository.save(t3);
        taskRepository.save(t4);

    }
}
