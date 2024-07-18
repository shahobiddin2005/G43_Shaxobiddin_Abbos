package bot.telegram.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Card {
    private String id;
    private String number;
    private String password;
    private Integer balance;
    private String userId;
}
