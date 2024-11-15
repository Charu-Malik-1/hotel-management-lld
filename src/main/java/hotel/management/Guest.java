package hotel.management;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Guest extends Person {
    List<Reservation> userBookingLog;

    public Guest() {
        userBookingLog = new ArrayList<>();
    }

    public void updateUserBookingLog(Reservation roomBooking){
        userBookingLog.add(roomBooking);
    }
}
