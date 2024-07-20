package bot.telegram.inlineService;

import bot.telegram.entity.Card;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineMurkup {
    public InlineKeyboardMarkup inlineMarkup(InlineString[][] buttons) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> markup = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(markup);
        for (InlineString[] button : buttons) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (InlineString s : button) {
                InlineKeyboardButton inlineButton = new InlineKeyboardButton();
                inlineButton.setText(s.getMessage());
                inlineButton.setCallbackData(s.getInlineData());
                row.add(inlineButton);
            }
            markup.add(row);
        }
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup inlineMarkup(List<Card> cards, String query) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> markup = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(markup);
        for (Card card : cards) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton inlineButton = new InlineKeyboardButton();
            inlineButton.setText(card.getNumber());
            inlineButton.setCallbackData(query+" " + card.getNumber());
            row.add(inlineButton);
            markup.add(row);
        }
        return inlineKeyboardMarkup;
    }
}
