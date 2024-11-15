package hotel.management;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Room {
    int roomId;
    int price;
    RoomStatus roomStatus;
    List<Reservation> roomHistoryLog;

    public Room() {
        roomHistoryLog = new ArrayList<>();
    }

    public boolean isRoomAvailable(Date startDate, Date endDate) {
        for (int i = 0; i < roomHistoryLog.size(); i++) {
            Reservation roomBooking = roomHistoryLog.get(i);
            Date checkInDate = roomBooking.getCheckIn();
            Date checkOutDate = roomBooking.getCheckout();

            if ((checkInDate.before(startDate) && checkOutDate.before(startDate)) || (checkOutDate.after(endDate) && checkOutDate.after(endDate)))
                return true; // no overlap

            return false;
        }
        return false;
    }

    public void addBooking(Reservation roomBooking){
        roomHistoryLog.add(roomBooking);
    }

    void checkIn(){}
    void checkOut(){}
}
