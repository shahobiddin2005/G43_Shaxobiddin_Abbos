package bot.telegram;

import bot.telegram.service.BotService;
import bot.telegram.util.Repository;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        Repository repository = new Repository();

repository.createTables();

        TelegramBotsApi telegramBotsApi = null;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(BotService.getInstance());
        } catch (Exception e) {
        }
    }
}