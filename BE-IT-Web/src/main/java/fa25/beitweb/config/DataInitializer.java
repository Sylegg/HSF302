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

import java.text.SimpleDateFormat;
import java.util.*;

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

        // ========== TẠO USERS ==========
        User staff1 = new User(null, "staff1@gmail.com", "123", "Nguyễn Văn Quản Lý", "STAFF", null, null, null);
        User staff2 = new User(null, "staff2@gmail.com", "123", "Trần Thị Giám Đốc", "STAFF", null, null, null);
        userRepository.saveAll(Arrays.asList(staff1, staff2));

        User m1 = new User(null, "member1@gmail.com", "123", "Nguyễn Văn A", "MEMBER", null, null, null);
        User m2 = new User(null, "member2@gmail.com", "123", "Trần Thị B", "MEMBER", null, null, null);
        User m3 = new User(null, "member3@gmail.com", "123", "Lê Văn C", "MEMBER", null, null, null);
        User m4 = new User(null, "member4@gmail.com", "123", "Phạm Thị D", "MEMBER", null, null, null);
        User m5 = new User(null, "member5@gmail.com", "123", "Hoàng Văn E", "MEMBER", null, null, null);
        User m6 = new User(null, "member6@gmail.com", "123", "Vũ Thị F", "MEMBER", null, null, null);
        User m7 = new User(null, "member7@gmail.com", "123", "Đặng Văn G", "MEMBER", null, null, null);
        User m8 = new User(null, "member8@gmail.com", "123", "Bùi Thị H", "MEMBER", null, null, null);
        userRepository.saveAll(Arrays.asList(m1, m2, m3, m4, m5, m6, m7, m8));

        // ========== TẠO ROOMS ==========
        Date roomCreatedDate = new Date();

        Room r1 = new Room(null, "Phòng Phát triển Web", "Phòng chuyên phát triển các dự án website và ứng dụng web", roomCreatedDate, staff1, null);
        Room r2 = new Room(null, "Phòng Phát triển Mobile", "Phòng phát triển ứng dụng di động iOS và Android", roomCreatedDate, staff1, null);
        Room r3 = new Room(null, "Phòng UI/UX Design", "Phòng thiết kế giao diện và trải nghiệm người dùng", roomCreatedDate, staff2, null);
        Room r4 = new Room(null, "Phòng Kiểm thử QA", "Phòng đảm bảo chất lượng và kiểm thử phần mềm", roomCreatedDate, staff2, null);
        Room r5 = new Room(null, "Phòng DevOps", "Phòng vận hành và triển khai hệ thống", roomCreatedDate, staff1, null);
        roomRepository.saveAll(Arrays.asList(r1, r2, r3, r4, r5));

        // ========== TẠO TASKS ==========
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        Date now = new Date();

        Task t1 = new Task(null, "Thiết kế giao diện trang chủ",
                "Thiết kế UI/UX cho trang chủ website công ty, responsive trên mọi thiết bị",
                "IN_PROGRESS", 65, sdf.parse("10/12/2025"), now, now,
                m1, staff1, r1);

        Task t2 = new Task(null, "Xây dựng API REST",
                "Phát triển RESTful API cho module quản lý người dùng",
                "IN_PROGRESS", 40, sdf.parse("15/12/2025"), now, now,
                m2, staff1, r1);

        Task t3 = new Task(null, "Tích hợp thanh toán online",
                "Tích hợp cổng thanh toán VNPay và Momo vào hệ thống",
                "OPEN", 0, sdf.parse("20/12/2025"), now, now,
                m3, staff1, r1);

        Task t4 = new Task(null, "Xây dựng hệ thống báo cáo",
                "Tạo dashboard hiển thị thống kê và báo cáo theo thời gian thực",
                "OPEN", 0, sdf.parse("25/12/2025"), now, now,
                m4, staff1, r1);

        Task t5 = new Task(null, "Tối ưu hiệu năng database",
                "Optimize queries và tạo index cho các bảng lớn",
                "COMPLETED", 100, sdf.parse("05/12/2025"), now, now,
                m1, staff1, r1);

// Phòng Mobile
        Task t6 = new Task(null, "Phát triển app iOS",
                "Xây dựng ứng dụng mobile cho iOS sử dụng Swift",
                "IN_PROGRESS", 55, sdf.parse("12/12/2025"), now, now,
                m5, staff1, r2);

        Task t7 = new Task(null, "Phát triển app Android",
                "Xây dựng ứng dụng mobile cho Android sử dụng Kotlin",
                "IN_PROGRESS", 50, sdf.parse("14/12/2025"), now, now,
                m6, staff1, r2);

        Task t8 = new Task(null, "Tích hợp push notification",
                "Tích hợp Firebase Cloud Messaging cho thông báo đẩy",
                "OPEN", 0, sdf.parse("22/12/2025"), now, now,
                m5, staff1, r2);

// Phòng UI/UX
        Task t9 = new Task(null, "Nghiên cứu UX người dùng",
                "Thực hiện khảo sát và phân tích hành vi người dùng",
                "IN_PROGRESS", 75, sdf.parse("09/12/2025"), now, now,
                m7, staff2, r3);

        Task t10 = new Task(null, "Thiết kế Design System",
                "Tạo hệ thống thiết kế thống nhất cho toàn bộ sản phẩm",
                "IN_PROGRESS", 30, sdf.parse("18/12/2025"), now, now,
                m8, staff2, r3);

        Task t11 = new Task(null, "Làm prototype app mới",
                "Tạo prototype tương tác cho dự án mới sử dụng Figma",
                "OPEN", 0, sdf.parse("28/12/2025"), now, now,
                m7, staff2, r3);

// Phòng QA
        Task t12 = new Task(null, "Viết test case module đăng nhập",
                "Tạo test cases chi tiết cho chức năng đăng nhập/đăng ký",
                "COMPLETED", 100, sdf.parse("08/12/2025"), now, now,
                m3, staff2, r4);

        Task t13 = new Task(null, "Kiểm thử tích hợp API",
                "Test integration các API endpoints với frontend",
                "IN_PROGRESS", 60, sdf.parse("20/12/2025"), now, now,
                m4, staff2, r4);

        Task t14 = new Task(null, "Test automation với Selenium",
                "Viết automated test scripts cho các flows chính",
                "OPEN", 0, sdf.parse("29/12/2025"), now, now,
                m3, staff2, r4);

// Phòng DevOps
        Task t15 = new Task(null, "Setup CI/CD pipeline",
                "Cấu hình Jenkins/GitLab CI cho tự động deploy",
                "IN_PROGRESS", 80, sdf.parse("10/12/2025"), now, now,
                m2, staff1, r5);

        Task t16 = new Task(null, "Cấu hình Docker containers",
                "Dockerize các services và tạo docker-compose",
                "COMPLETED", 100, sdf.parse("05/12/2025"), now, now,
                m6, staff1, r5);

        Task t17 = new Task(null, "Giám sát hệ thống với Prometheus",
                "Setup monitoring và alerting cho production",
                "OPEN", 0, sdf.parse("22/12/2025"), now, now,
                m2, staff1, r5);

        Task t18 = new Task(null, "Backup và khôi phục database",
                "Thiết lập quy trình backup tự động và disaster recovery",
                "OPEN", 0, sdf.parse("20/12/2025"), now, now,
                m6, staff1, r5);

        taskRepository.saveAll(Arrays.asList(
                t1, t2, t3, t4, t5,
                t6, t7, t8,
                t9, t10, t11,
                t12, t13, t14,
                t15, t16, t17, t18
        ));

    }
}
