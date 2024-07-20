package bot.telegram.util;

import bot.telegram.entity.Card;
import bot.telegram.entity.Monitoring;
import bot.telegram.entity.User;
import org.apache.commons.lang3.text.StrBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Repository {
    DbConnection testConnection = DbConnection.getInstance();


    public void save(User user) {
        Statement statement = testConnection.getStatement();
        try {
            String query = String.format("insert into users values('%s','%s','%s')",
                    user.getId(),
                    user.getName(),
                    user.getState()
            );
            statement.execute(query);
        } catch (SQLException e) {
        }
    }

    public void save(Card card) {
        Statement statement = testConnection.getStatement();
        try {
            String query = String.format("insert into card(number, password, balance, user_id) values('%s','%s',%d , '%s')",
                    card.getNumber(),
                    card.getPassword(),
                    card.getBalance(),
                    card.getUserId()
            );
            statement.execute(query);
            System.out.println("qoshildi");
        } catch (SQLException e) {
            System.out.println("xato");
        }
    }

    public void update(Monitoring monitoring) {
        if (monitoring.getSender_u().equals("1")){
            Statement statement = testConnection.getStatement();
            try {
                String query1 = String.format("update card set balance = (select balance from card where id = %d) + %d where id = %d",
                        Integer.valueOf(monitoring.getFromId()),
                        monitoring.getAmount(),
                        Integer.valueOf(monitoring.getFromId())
                );
                statement.execute(query1);
            } catch (SQLException e) {
                System.out.println("xatolik");
            }
            return;
        }
        Statement statement = testConnection.getStatement();
        try {
            String query = String.format("update card set balance = (select balance from card where id = %d) - %d where id = %d",
                    Integer.valueOf(monitoring.getSenderId()),
                    monitoring.getAmount(),
                    Integer.valueOf(monitoring.getSenderId())
            );
            statement.execute(query);
            String query1 = String.format("update card set balance = (select balance from card where id = %d) + %d where id = %d",
                    Integer.valueOf(monitoring.getFromId()),
                    monitoring.getAmount(),
                    Integer.valueOf(monitoring.getFromId())
            );
            statement.execute(query1);
        } catch (SQLException e) {
            System.out.println("xatolik");
        }
    }

    public void update(String id, String state) {
        Statement statement = testConnection.getStatement();
        try {
            String query = String.format("update users set state = '%s' where id = '%s'",
                    state,
                    id
            );
            statement.execute(query);
        } catch (SQLException e) {
        }
    }

    public void save(Monitoring monitoring) {
        Statement statement = testConnection.getStatement();
        try {
            String query = String.format("insert into monitoring(sender_id, from_id, amount, sender_u, from_u) values(%d,%d,%d, '%s', '%s')",
                    Integer.valueOf(monitoring.getSenderId()),
                    Integer.valueOf(monitoring.getFromId()),
                    monitoring.getAmount(),
                    monitoring.getSender_u(),
                    monitoring.getFrom_u()
            );
            statement.execute(query);
        } catch (SQLException e) {
            System.out.println("xato");
        }
    }


//    public List<User> getAllUsers() {
//        try {
//            Statement statement = testConnection.getStatement();
//            return getUsers(statement.executeQuery(String.format("select * from users;")));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return new ArrayList<>();
//    }

    public Optional<User> getUserById(String id) {
        Statement statement = testConnection.getStatement();
        try {
            ResultSet resultSet = statement.executeQuery(String.format("select * from users where id = '%s';", id));

            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getString("id"));
                user.setName(resultSet.getString("name"));
                user.setState(resultSet.getString("state"));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public Optional<Card> getCardByNumber(String number) {
        Statement statement = testConnection.getStatement();
        try {
            ResultSet resultSet = statement.executeQuery(String.format("select * from card where number = '%s';", number));

            if (resultSet.next()) {
                Card card = new Card();
                card.setId(resultSet.getString("id"));
                card.setNumber(resultSet.getString("number"));
                card.setPassword(resultSet.getString("password"));
                card.setUserId(resultSet.getString("user_id"));
                card.setBalance(resultSet.getInt("balance"));
                return Optional.of(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public List<Monitoring> getHistory(String userId) {
        Statement statement = testConnection.getStatement();
        List<Monitoring> monitorings = new ArrayList<>();
        try {
            ResultSet resultSet = statement.executeQuery(String.format("select * from monitoring where sender_u = '%s' or from_u = '%s';", userId, userId));

            while (resultSet.next()) {
                Monitoring monitoring = new Monitoring();
                monitoring.setId(resultSet.getString("id"));
                monitoring.setTime(resultSet.getString("time"));
                monitoring.setFromId(resultSet.getString("from_id"));
                monitoring.setSenderId(resultSet.getString("sender_id"));
                monitoring.setFrom_u(resultSet.getString("from_u"));
                monitoring.setSender_u(resultSet.getString("sender_u"));
                monitoring.setAmount(resultSet.getInt("amount"));
                monitorings.add(monitoring);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return monitorings;
    }


//
//    public List<User> getUsers(ResultSet resultSet) {
//        List<User> users = new ArrayList<>();
//        try {
//            while (true) {
//                if (!resultSet.next()) break;
//                User user = new User();
//                user.setId(resultSet.getString("id"));
//                user.setName(resultSet.getString("name"));
//                user.setState(resultSet.getString("state"));
//                users.add(user);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return users;
//    }

    private static Repository instance;

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }


    public List<Card> getCardsByUserId(User user) {
        Statement statement = testConnection.getStatement();
        List<Card> cards = new ArrayList<>();
        try {
            ResultSet resultSet = statement.executeQuery(String.format("select * from card where user_id = '%s';", user.getId()));

            while (resultSet.next()) {
                Card card = new Card();
                card.setId(resultSet.getString("id"));
                card.setNumber(resultSet.getString("number"));
                card.setPassword(resultSet.getString("password"));
                card.setUserId(resultSet.getString("user_id"));
                card.setBalance(resultSet.getInt("balance"));
                cards.add(card);
            }
        } catch (SQLException e) {
            System.out.println("xato");
            e.printStackTrace();
        }

        return cards;
    }

    public String makeCards(List<Card> cards) {
        StringBuilder sb = new StringBuilder();
        sb.append("Your cards:\n");
        for (Card card : cards) {
            sb.append("Number: ").append(card.getNumber())
                    .append("**, Balance: ")
                    .append(card.getBalance())
                    .append("\n");
        }
        return sb.toString();
    }

    public Optional<Card> getCardById(String id) {
        Statement statement = testConnection.getStatement();
        try {
            ResultSet resultSet = statement.executeQuery(String.format("select * from card where id = %d;", Integer.valueOf(id)));

            if (resultSet.next()) {
                Card card = new Card();
                card.setId(resultSet.getString("id"));
                card.setNumber(resultSet.getString("number"));
                card.setPassword(resultSet.getString("password"));
                card.setUserId(resultSet.getString("user_id"));
                card.setBalance(resultSet.getInt("balance"));
                return Optional.of(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}


