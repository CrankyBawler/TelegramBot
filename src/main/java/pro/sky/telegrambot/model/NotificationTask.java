package pro.sky.telegrambot.model;

import lombok.*;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.time.LocalDateTime;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//public class NotificationTask {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    @Column(name = "User_id")
//    private Long userId;
//    private String text;
//    private LocalDateTime date;

//}
@Entity
@Table (name = "notification_Task")

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Getter
    private Long id;
    @Getter
    private Long chatId;
    @Getter
    private String taskText;
    @Getter
    private LocalDateTime performDate;

    public NotificationTask(Long chatId, Long id, String taskText, LocalDateTime performDate) {
        this.chatId = chatId;
        this.taskText = taskText;
        this.performDate = performDate;
    }
}
