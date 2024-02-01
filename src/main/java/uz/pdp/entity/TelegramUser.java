package uz.pdp.entity;

import lombok.Builder;
import lombok.Data;
import uz.pdp.enums.TelegramState;

@Data
@Builder
public class TelegramUser {
    private String firstName;
    private long chatId;
    private String phoneNumber;
    private String email;
    private TelegramState state;
    private Integer code;
}
