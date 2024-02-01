package uz.pdp;

import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;

public class BotUtils {
    public static Keyboard generateButton() {
        KeyboardButton keyboardButton = new KeyboardButton("Share Phone Number");
        keyboardButton.requestContact(true);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardButton);
        replyKeyboardMarkup.oneTimeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public static Keyboard generateContinueButton() {
        KeyboardButton keyboardButton = new KeyboardButton("CONTINUE");
        return new ReplyKeyboardMarkup(keyboardButton);
    }
}
