package bot.telegram.service;

import bot.telegram.entity.Card;
import bot.telegram.inlineService.InlineMurkup;
import bot.telegram.replyServise.ReplyKeyboardMurkup;
import bot.telegram.util.Repository;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import bot.telegram.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static bot.telegram.util.Utils.*;

public class BotLogicServise {
    private BotService botService = BotService.getInstance();
    private SendMessage sendMessage = new SendMessage();
    private ReplyKeyboardMurkup keyboardMurkup = new ReplyKeyboardMurkup();
    private InlineMurkup inlineMurkup = new InlineMurkup();
    private Repository repository = Repository.getInstance();
    private Map<String, Card> cards = new HashMap<>();

    public void messageHandler(Update update) {
        User currentUser = null;
        String chatId = String.valueOf(update.getMessage().getChatId());
        Optional<bot.telegram.entity.User> optionalUser = repository.getUserById(chatId);
        if (optionalUser.isEmpty()) {
            User user = new User(chatId, update.getMessage().getChat().getFirstName(), MAIN_S);
            currentUser = user;
            repository.save(user);
        } else {
            currentUser = optionalUser.get();
        }
        String text = update.getMessage().getText();

        if (currentUser.getState().equals(CARD_NUMBER_S)) {
            if (text.length() == 4) {
                cards.get(currentUser.getId()).setNumber(text);
                cards.get(currentUser.getId()).setUserId(currentUser.getId());
                cards.get(currentUser.getId()).setBalance(0);
                sendMessage.setText("Password kiriting:");
                sendMessage.setChatId(currentUser.getId());
                currentUser.setState(CARD_PASS_S);
                repository.update(currentUser.getId(), CARD_PASS_S);
                botService.executeMessage(sendMessage);
            } else {
                sendMessage.setText("Noto'g'ri raqam!\nQayta kiriting:");
                sendMessage.setChatId(currentUser.getId());
                botService.executeMessage(sendMessage);
            }
            return;
        }

        if (currentUser.getState().equals(CARD_PASS_S)) {
            cards.get(currentUser.getId()).setPassword(text);
            sendMessage.setText("Karta qo'shildi(parol uchun uzur!):");
            sendMessage.setChatId(currentUser.getId());
            sendMessage.setReplyMarkup(keyboardMurkup.keyboardMaker(mainMenu));
            currentUser.setState(MAIN_S);
            repository.update(currentUser.getId(), MAIN_S);
            System.out.println(cards.get(currentUser.getId()));
            repository.save(cards.get(currentUser.getId()));
            botService.executeMessage(sendMessage);
            return;
        }



        switch (text) {
            case "/start" -> {
                sendMessage.setText("Botga hush kelibsiz!");
                repository.update(currentUser.getId(), MAIN_S);
                sendMessage.setChatId(chatId);
                sendMessage.setReplyMarkup(keyboardMurkup.keyboardMaker(mainMenu));
                botService.executeMessage(sendMessage);
            }
            case ADD_CARD -> {
                sendMessage.setText("Enter card number(XXXX):");
                sendMessage.setChatId(currentUser.getId());
                currentUser.setState(CARD_NUMBER_S);
                repository.update(currentUser.getId(), CARD_NUMBER_S);
                cards.put(currentUser.getId(), new Card());
                botService.executeMessage(sendMessage);
            }
            case MY_CARD -> {
                sendMessage.setText(repository.getCardsByUserId(currentUser));
                sendMessage.setChatId(currentUser.getId());
                sendMessage.setReplyMarkup(keyboardMurkup.keyboardMaker(mainMenu));
                botService.executeMessage(sendMessage);
            }
            case HISTORY -> {
                sendMessage.setText(repository.getCardsByUserId(currentUser));
                sendMessage.setChatId(currentUser.getId());
                sendMessage.setReplyMarkup(keyboardMurkup.keyboardMaker(mainMenu));
                botService.executeMessage(sendMessage);
            }
            case TRANSFER -> {
                
            }
            case DEPOSITE -> {
                sendMessage.setText("Enter amount:");
                sendMessage.setChatId(chatId);
                currentUser.setState(ENTER_AMOUNT);
                botService.executeMessage(sendMessage);
            }
        }
    }


    public void callbackHandler(Update update) {

    }

    private static BotLogicServise instance;
    ;

    private BotLogicServise() {

    }

    public static BotLogicServise getInstance() {
        if (instance == null) {
            instance = new BotLogicServise();
        }
        return instance;
    }
}
