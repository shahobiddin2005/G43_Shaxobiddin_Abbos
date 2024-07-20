package bot.telegram.service;

import bot.telegram.entity.Card;
import bot.telegram.entity.Monitoring;
import bot.telegram.inlineService.InlineMurkup;
import bot.telegram.replyServise.ReplyKeyboardMurkup;
import bot.telegram.util.Repository;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
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
    private Map<String, Monitoring> transfers = new HashMap<>();

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
                if (repository.getCardByNumber(text).isPresent()) {
                    sendMessage.setText("Karta mavjud boshqa raqam kiriting:");
                    sendMessage.setChatId(currentUser.getId());
                    currentUser.setState(CARD_NUMBER_S);
                    repository.update(currentUser.getId(), CARD_NUMBER_S);
                    botService.executeMessage(sendMessage);
                    return;
                }
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

        if (currentUser.getState().equals(ENTER_CARD_NUM)) {
            Optional<Card> optionalCard = repository.getCardByNumber(text);
            if (optionalCard.isPresent()) {
                Monitoring transfer = new Monitoring(null, null, optionalCard.get().getId(), null, currentUser.getId(), optionalCard.get().getUserId(), 0);
                transfers.put(currentUser.getId(), transfer);
                sendMessage.setText("Qaysi kartadan o'tkazasiz:");
                sendMessage.setChatId(currentUser.getId());
                sendMessage.setReplyMarkup(inlineMurkup.inlineMarkup(repository.getCardsByUserId(currentUser), "card"));
                currentUser.setState(MAIN_S);
                repository.update(currentUser.getId(), MAIN_S);
                botService.executeMessage(sendMessage);
                return;
            } else {
                sendMessage.setText("Karta topilmadi\nQayta kiriting:");
                sendMessage.setChatId(currentUser.getId());
                sendMessage.setReplyMarkup(null);
                botService.executeMessage(sendMessage);
            }
        }


        if (currentUser.getState().equals(ENTER_AMOUNT)) {
            try {
                transfers.get(currentUser.getId()).setAmount(Math.abs(Integer.parseInt(text)));

            } catch (Exception e) {
                System.out.println(e.getMessage());
                sendMessage.setText("Noto'g'ri format\nQayta kiriting:");
                sendMessage.setChatId(chatId);
                botService.executeMessage(sendMessage);
                return;
            }
            if (repository.getCardById(transfers.get(currentUser.getId()).getSenderId()).get().getBalance() < transfers.get(currentUser.getId()).getAmount()) {
                sendMessage.setText("Mablag' yetarli emas!\nQayta kiriting:");
                sendMessage.setChatId(chatId);
                botService.executeMessage(sendMessage);
                return;
            }

            sendMessage.setText("Parolni kiriting:");
            sendMessage.setChatId(currentUser.getId());
            sendMessage.setReplyMarkup(null);
            currentUser.setState(ENTER_PASS);
            repository.update(currentUser.getId(), ENTER_PASS);
            botService.executeMessage(sendMessage);
            return;

        }

        if (currentUser.getState().equals(ENTER_PASS)) {
            Optional<Card> optionalCard = repository.getCardById(transfers.get(currentUser.getId()).getSenderId());
            if (optionalCard.isPresent()) {
                if (optionalCard.get().getPassword().equals(text)) {
                    repository.save(transfers.get(currentUser.getId()));
                    repository.update(transfers.get(currentUser.getId()));
                    sendMessage.setText("Success!");
                    sendMessage.setChatId(currentUser.getId());
                    sendMessage.setReplyMarkup(keyboardMurkup.keyboardMaker(mainMenu));
                    currentUser.setState(MAIN_S);
                    repository.update(currentUser.getId(), MAIN_S);
                    botService.executeMessage(sendMessage);
                    return;
                } else {
                    sendMessage.setText("Parol xato!\nQayta kiriting:");
                    sendMessage.setChatId(currentUser.getId());
                    sendMessage.setReplyMarkup(null);
                    botService.executeMessage(sendMessage);
                    return;
                }
            }
        }

        if (currentUser.getState().equals(DEPO_AMOUNT)){
            try {
                transfers.get(currentUser.getId()).setAmount(Math.abs(Integer.parseInt(text)));

            } catch (Exception e) {
                System.out.println(e.getMessage());
                sendMessage.setText("Noto'g'ri format\nQayta kiriting:");
                sendMessage.setChatId(chatId);
                botService.executeMessage(sendMessage);
                return;
            }
            repository.save(transfers.get(currentUser.getId()));
            repository.update(transfers.get(currentUser.getId()));

            sendMessage.setText("Success!");
            sendMessage.setChatId(currentUser.getId());
            sendMessage.setReplyMarkup(keyboardMurkup.keyboardMaker(mainMenu));
            repository.update(currentUser.getId(), MAIN_S);
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
                sendMessage.setText(repository.makeCards(repository.getCardsByUserId(currentUser)));
                sendMessage.setChatId(currentUser.getId());
                sendMessage.setReplyMarkup(keyboardMurkup.keyboardMaker(mainMenu));
                botService.executeMessage(sendMessage);
            }
            case HISTORY -> {
                sendMessage.setChatId(chatId);
                sendMessage.setReplyMarkup(keyboardMurkup.keyboardMaker(mainMenu));
                for (Monitoring monitoring : repository.getHistory(currentUser.getId())) {
                    StringBuilder sb = new StringBuilder();
                    if (monitoring.getSender_u().equals("1")&&monitoring.getFrom_u().equals(currentUser.getId())){
                        sb.append("Deposite: ")
                                .append("\namount: +")
                                .append(monitoring.getAmount())
                                .append("\ntime: ")
                                .append(monitoring.getTime());
                    }
                    else if (monitoring.getSender_u().equals(currentUser.getId())) {
                        sb.append("Qabul qiluvchi: ")
                                .append(repository.getUserById(monitoring.getFrom_u()).get().getName())
                                .append("\namount: -")
                                .append(monitoring.getAmount())
                                .append("\ntime: ")
                                .append(monitoring.getTime());
                    } else {
                        sb.append("Yuboruvchi: ")
                                .append(repository.getUserById(monitoring.getSender_u()).get().getName())
                                .append("\namount: +")
                                .append(monitoring.getAmount())
                                .append("\ntime: ")
                                .append(monitoring.getTime());
                    }
                    sendMessage.setText(sb.toString());
                    botService.executeMessage(sendMessage);
                }
            }
            case TRANSFER -> {
                sendMessage.setText("Enter receiving card number:");
                sendMessage.setChatId(chatId);
                sendMessage.setReplyMarkup(null);
                currentUser.setState(ENTER_CARD_NUM);
                repository.update(currentUser.getId(), ENTER_CARD_NUM);
                botService.executeMessage(sendMessage);
            }
            case DEPOSITE -> {
                sendMessage.setText("Choose card:");
                sendMessage.setChatId(chatId);
                sendMessage.setReplyMarkup(inlineMurkup.inlineMarkup(repository.getCardsByUserId(currentUser), "deposit"));
                botService.executeMessage(sendMessage);
            }
        }
    }

    @SneakyThrows
    public void callbackHandler(Update update) {
        User currentUser = null;
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        Optional<bot.telegram.entity.User> optionalUser = repository.getUserById(chatId);
        currentUser = optionalUser.get();
        String[] texts = update.getCallbackQuery().getData().split(" ");
        if (texts[0].equals("card")) {
            DeleteMessage deleteMessage = new DeleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
            botService.execute(deleteMessage);
            transfers.get(currentUser.getId()).setSenderId(repository.getCardByNumber(texts[1]).get().getId());
            sendMessage.setText("Enter amount:");
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(null);
            repository.update(currentUser.getId(), ENTER_AMOUNT);
            botService.executeMessage(sendMessage);
        }

        if (texts[0].equals("deposit")) {
            DeleteMessage deleteMessage = new DeleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
            botService.execute(deleteMessage);
            transfers.put(currentUser.getId(), new Monitoring(null, repository.getCardByNumber("2222").get().getId(), repository.getCardByNumber(texts[1]).get().getId(), null, "1", currentUser.getId(), 0));
            sendMessage.setText("Enter amount:");
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(null);
            repository.update(currentUser.getId(), DEPO_AMOUNT);
            botService.executeMessage(sendMessage);
        }
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
