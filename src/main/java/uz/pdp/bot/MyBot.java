package uz.pdp.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

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
                        handleUpdate(update);
                    }, executorService);
                } catch (Exception e) {
                    throw new RuntimeException();
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, Throwable::printStackTrace);
    }

    private void handleUpdate(Update update) {

    }
}
