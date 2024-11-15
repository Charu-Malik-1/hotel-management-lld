package hotel.management;

import lombok.Data;

@Data
public abstract class Account {
    String userName;
    String password;
    AccountStatus accountStatus;
}
