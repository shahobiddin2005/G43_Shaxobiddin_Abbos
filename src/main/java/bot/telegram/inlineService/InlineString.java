package bot.telegram.inlineService;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InlineString {
    private String message;
    private String inlineData;
}
