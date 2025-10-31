package fa25.beitweb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "room_name", columnDefinition = "NVARCHAR(100)")
    private String roomName;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt = new Date();

    // Người tạo phòng (Staff)
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    // Một phòng có thể có nhiều task
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();
}