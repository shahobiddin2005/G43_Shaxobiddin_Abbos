package bot.telegram.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Monitoring {
    private String id;
    private String senderId;
    private String fromId;
    private String time;
    private String sender_u;
    private String from_u;
    private Integer amount;
}
