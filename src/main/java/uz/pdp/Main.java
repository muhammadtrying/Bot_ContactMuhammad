package uz.pdp;

import uz.pdp.bot.MyBot;
import uz.pdp.db.DB;
import uz.pdp.entity.TelegramUser;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        while (true) {
            MyBot myBot = new MyBot();
            myBot.start();
            showUsers();
        }
    }

    private static void showUsers() {
        System.out.print("1-to show users");
        Scanner scanner = new Scanner(System.in);
        System.out.println();
        if ( scanner.nextInt() == 1 ) {
            int i = 1;
            for (TelegramUser user : DB.TELEGRAM_USERS) {
                System.out.println("================================");
                System.out.println(i++ + "  Name: " + user.getFirstName());
                System.out.println("ChatId: " + user.getChatId());
                System.out.println("Phone Number: +" + user.getPhoneNumber());
            }
        }
    }
}