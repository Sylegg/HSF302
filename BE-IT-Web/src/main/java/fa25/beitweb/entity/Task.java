package fa25.beitweb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "title", nullable = false, columnDefinition = "NVARCHAR(150)")
    private String title;

    @Column(name = "task_description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "status", length = 20)
    private String status = "OPEN"; // OPEN, IN_PROGRESS, COMPLETED

    @Column(name = "progress_percent")
    private Integer progressPercent = 0;

    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    private Date dueDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt = new Date();

    // Người được giao task (Member)
    @OneToOne
    @JoinColumn(name = "assignee_id", unique = true)
    private User assignee;

    // Người tạo task (Staff)
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    // Task thuộc phòng nào
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = new Date();
    }
}
