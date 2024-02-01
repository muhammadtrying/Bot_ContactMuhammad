package uz.pdp.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import jakarta.mail.MessagingException;
import uz.pdp.db.DB;
import uz.pdp.entity.TelegramUser;
import uz.pdp.enums.TelegramState;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyBot {
    public static TelegramBot telegramBot = new TelegramBot("6891560524:AAEnN4GFEXSzor56BGEle-N-VI1GY-YJYjQ");
    ExecutorService executorService = Executors.newFixedThreadPool(10);

    public void start() {
        telegramBot.setUpdatesListener((updates) -> {
            for (Update update : updates) {
                try {
                    CompletableFuture.runAsync(() -> {
                        try {
                            handleUpdate(update);
                        } catch (MessagingException e) {
                            throw new RuntimeException(e);
                        }
                    }, executorService);
                } catch (Exception e) {
                    throw new RuntimeException();
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, Throwable::printStackTrace);
    }

    private void handleUpdate(Update update) throws MessagingException {
        if ( update.message() != null ) {
            Message message = update.message();
            Long id = message.chat().id();
            TelegramUser telegramUser = getUser(id, message.chat());

            if ( message.text() != null ) {
                if ( message.text().equals("/start") ) {
                    BotService.welcomeMessageAndAskContact(telegramUser);
                }else if(telegramUser.getState().equals(TelegramState.SENDING_CODE)){
                    BotService.acceptEmailAndSendCode(telegramUser,message);
                }else if(telegramUser.getState().equals(TelegramState.ACCEPTING_CODE)){
                    BotService.acceptCodeAndCheck(telegramUser,message);
                }else if(telegramUser.getState().equals(TelegramState.CHECKING_CODE)){
                    BotService.checkCodeAndRegister(telegramUser,message);
                }
            } else if ( message.contact() != null ) {
                if ( telegramUser.getState().equals(TelegramState.ASKING_EMAIL) ) {
                    BotService.acceptContactAndAskEmail(telegramUser, message);
                }
            }
        }
    }

    private TelegramUser getUser(Long id, Chat chat) {
        for (TelegramUser telegramUser : DB.TELEGRAM_USERS) {
            if ( telegramUser.getChatId() == id ) {
                return telegramUser;
            }
        }
        TelegramUser telegramUser = TelegramUser.builder()
         .chatId(id)
         .firstName(chat.firstName())
         .build();
        DB.TELEGRAM_USERS.add(telegramUser);
        return telegramUser;
    }
}
