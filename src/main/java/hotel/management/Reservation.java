package hotel.management;

import lombok.Data;

import java.util.Date;

@Data
public class Reservation {
    int bookingId;
    Date bookingDate;
    int duration;
    ReservationStatus bookingStatus;
    Date checkIn;
    Date checkout;
    int roomId;
    int guestid;

    public Reservation(){

    }

}
