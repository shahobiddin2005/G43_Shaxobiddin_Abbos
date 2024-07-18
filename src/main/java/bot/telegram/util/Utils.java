package bot.telegram.util;

public interface Utils {

    String BACK = "Back";

    String MY_CARD = "My card";
    String ADD_CARD = "Add card";
    String TRANSFER = "Transfer";
    String HISTORY = "History";
    String DEPOSITE = "Diposite";

    String[][] mainMenu= {{MY_CARD,ADD_CARD}, {TRANSFER,HISTORY}, {DEPOSITE}};


    //states
    String MAIN_S = "main";
    String CARD_NUMBER_S = "card_num";
    String CARD_PASS_S = "card_pass";
//    String TRANSFER = "Transfer";
//    String HISTORY = "History";
//    String DEPOSITE = "Diposite";
}
