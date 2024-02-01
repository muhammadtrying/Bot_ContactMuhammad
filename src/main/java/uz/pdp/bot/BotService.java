package uz.pdp.bot;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import uz.pdp.BotUtils;
import uz.pdp.db.DB;
import uz.pdp.entity.TelegramUser;
import uz.pdp.enums.TelegramState;

import java.util.Properties;
import java.util.Random;

public class BotService {
    public static void welcomeMessageAndAskContact(TelegramUser user) {
        SendMessage sendMessage = new SendMessage(
         user.getChatId(),
         "ASSALOMU ALEYKUM! Please share your contact to use the bot!"
        );
        sendMessage.replyMarkup(BotUtils.generateButton());
        MyBot.telegramBot.execute(sendMessage);
        user.setState(TelegramState.ASKING_EMAIL);
    }

    public static void acceptContactAndAskEmail(TelegramUser telegramUser, Message message) {
        telegramUser.setPhoneNumber(message.contact().phoneNumber());
        askEmail(telegramUser);
    }

    private static void askEmail(TelegramUser telegramUser) {
        SendMessage sendMessage = new SendMessage(
         telegramUser.getChatId(),
         "Enter email address:"
        );
        sendMessage.replyMarkup(new ReplyKeyboardRemove());
        MyBot.telegramBot.execute(sendMessage);
        telegramUser.setState(TelegramState.SENDING_CODE);
    }

    public static void acceptEmailAndSendCode(TelegramUser telegramUser, Message message) throws MessagingException {
        telegramUser.setEmail(message.text());
        String subject = "Authentication";
        Integer code = generateCode();
        telegramUser.setCode(code);
        String text = "Your code is " + code + ". Don't share it with anyone!";
        sendCodeViaEmail(telegramUser.getEmail(), subject, text);
        SendMessage sendMessage = new SendMessage(
         telegramUser.getChatId(),
         "Message has been sent!"
        );
        sendMessage.replyMarkup(BotUtils.generateContinueButton());
        MyBot.telegramBot.execute(sendMessage);
        telegramUser.setState(TelegramState.ACCEPTING_CODE);
    }

    private static int generateCode() {
        return new Random().nextInt(1000, 9999);
    }

    private static void sendCodeViaEmail(String email, String subject, String text) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        String username = "muhammadtrying@gmail.com";
        String password = "qkztfffapmwhnbvt";

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        MimeMessage message = new MimeMessage(
         session
        );

        message.setSubject(subject);
        message.setText(text);
        message.setFrom(new InternetAddress(username));
        message.setRecipient(MimeMessage.RecipientType.TO, new
         InternetAddress(email));

        Transport.send(message);
        System.out.println("Message has been sent!");
    }

    public static void acceptCodeAndCheck(TelegramUser telegramUser, Message message) {
        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(),
         "Enter code");
        sendMessage.replyMarkup(new ReplyKeyboardRemove());
        MyBot.telegramBot.execute(sendMessage);
        telegramUser.setState(TelegramState.CHECKING_CODE);
    }

    public static void checkCodeAndRegister(TelegramUser telegramUser, Message message) {
        if ( telegramUser.getCode().toString().equals(message.text()) ) {
            SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), "You have been registered! Now You can use the bot!");
            MyBot.telegramBot.execute(sendMessage);
            System.out.println("Success!");
        } else {
            System.out.println("Incorrect code!");
            SendMessage sendMessage = new SendMessage(telegramUser.getChatId(),
             "Incorrect code!");
            MyBot.telegramBot.execute(sendMessage);
            DB.TELEGRAM_USERS.removeIf(user -> user.getChatId() == telegramUser.getChatId());
        }
    }
}
