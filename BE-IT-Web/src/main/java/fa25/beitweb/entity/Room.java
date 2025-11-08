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

    @Column(name = "room_name", nullable = false,columnDefinition = "NVARCHAR(100)")
    private String roomName;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    // Người tạo phòng (Staff)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    //  Một phòng có thể có nhiều task

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    //  Tự động set ngày tạo nếu chưa có
    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = new Date();
        }
    }
}
