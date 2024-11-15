package hotel.management;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ReservationService {
    List<Reservation> bookingLog;

    public ReservationService() {
        bookingLog = new ArrayList<>();
    }

    private Reservation createBooking(Room room, Guest guest, Date checkIn, Date checkout, int id) {
        if (room.isRoomAvailable(checkIn, checkout)) {
            Reservation roomBooking = new Reservation();
            roomBooking.setBookingDate(new Date());
            roomBooking.setBookingId(id);
            roomBooking.setBookingStatus(ReservationStatus.SCHEDULED);
            roomBooking.setCheckIn(checkIn);
            roomBooking.setCheckout(checkout);
            roomBooking.setRoomId(room.getRoomId());
            roomBooking.setGuestid(guest.getId());
            return roomBooking;
        }
        return null;
    }

    public Reservation createBooking(List<Room> rooms, Guest guest, Date checkIn, Date checkout, int id) {
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            Reservation roomBooking = createBooking(room, guest, checkIn, checkout, id);
            if (roomBooking != null) {
                System.out.println("Booking success");
                //update booking log
               updateLog(room,roomBooking,guest);
                return roomBooking;
            }
        }
        System.out.println("Room not available");
        return null;
    }

    void updateLog(Room room, Reservation roomBooking, Guest user){
        user.updateUserBookingLog(roomBooking);
        room.addBooking(roomBooking);
        bookingLog.add(roomBooking);
    }

}
