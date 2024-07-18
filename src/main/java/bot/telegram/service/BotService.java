package bot.telegram.service;


import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
public class BotService extends TelegramLongPollingBot {
    private static BotLogicServise botLogicServise = BotLogicServise.getInstance();
    private BotService(){
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()){
            System.out.println(update.getCallbackQuery().getData());
            botLogicServise.callbackHandler(update);
        }else {
            System.out.println(update.getMessage().getText());
            botLogicServise.messageHandler(update);
        }
    }

    @Override
    public String getBotUsername() {
        return "t.me/g43_6_module_exam_bot";
    }

    @Override
    public String getBotToken() {
        return "7347981624:AAGM2jvrmof_NpJDOglDVPQrbqzB2VSo3oc";
    }

    private static BotService instance;
    public static BotService getInstance() {
        if (instance == null) {
            instance = new BotService();
        }
        return instance;
    }

    @SneakyThrows
    public Message executeMessage(SendMessage sendMessage) {
        return execute(sendMessage);
    }
}
