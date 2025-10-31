package fa25.beitweb.entity;


import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "email",nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "full_name", nullable = false,columnDefinition = "NVARCHAR(100)")
    private String fullName;

    @Column(name = "role", nullable = false, length = 10)
    private String role;


    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Room> createdRooms = new ArrayList<>();


    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Task> createdTasks = new ArrayList<>();


    @OneToOne(mappedBy = "assignee", cascade = CascadeType.ALL)
    private Task assignedTask;
}

