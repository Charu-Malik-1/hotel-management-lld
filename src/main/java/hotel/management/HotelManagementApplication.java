package hotel.management;

import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@SpringBootApplication
public class HotelManagementApplication {

    List<Guest> users;
    List<Hotel> hotels;

    public HotelManagementApplication() {
        users = new ArrayList<>();
        hotels = new ArrayList<>();
        initialise();
    }

    public static void main(String[] args) {
        SpringApplication.run(HotelManagementApplication.class, args);
        System.out.println("hi");

        HotelManagementApplication hotelManagementApplication = new HotelManagementApplication();
        Hotel hotel = hotelManagementApplication.getHotels().get(0);
        Branch branch = hotel.getBranches().get(0);
        List<Room> rooms = branch.getRooms();

        ReservationService ob = new ReservationService();
        Reservation roomBooking = ob.createBooking(rooms, hotelManagementApplication.users.get(0), Date.valueOf(LocalDate.of(2024, 11, 13)), Date.valueOf(LocalDate.of(2024, 11, 18)), 1);
        System.out.println("all bookings="+ob.getBookingLog());

    }


    public void initialise() {
        initialiseUser();
        initialiseHotels();
    }

    void initialiseUser() {
        Guest guest = new Guest();
        guest.setId(1);
        guest.setName("abc");
        users.add(guest);

        Guest guest1 = new Guest();
        guest1.setId(2);
        guest1.setName("abc1");
        users.add(guest1);

        Guest guest2 = new Guest();
        guest2.setId(3);
        guest2.setName("abc2");
        users.add(guest2);
    }

    List<Branch> createBranches() {
        Branch branch = new Branch();
        branch.setBranchId(1);
        branch.setRooms(createRooms());

        Branch branch1 = new Branch();
        branch1.setBranchId(2);
        branch1.setRooms(createRooms());

        Branch branch2 = new Branch();
        branch2.setBranchId(3);
        branch2.setRooms(createRooms());

        List<Branch> branches = new ArrayList<>();
        branches.add(branch);
        branches.add(branch1);
        branches.add(branch2);
        return branches;

    }

    List<Room> createRooms() {
        List<Room> rooms = new ArrayList<>();

        Room room1 = new Room();
        room1.setRoomId(1);
        room1.setPrice(1000);
        rooms.add(room1);

        Room room2 = new Room();
        room2.setRoomId(2);
        room2.setPrice(1000);
        rooms.add(room2);

        Room room3 = new Room();
        room3.setRoomId(3);
        room3.setPrice(1000);
        rooms.add(room3);

        return rooms;
    }


    void initialiseHotels() {
        Hotel hotel = new Hotel();
        hotel.setId(1);

        hotel.setBranches(createBranches());
        hotels.add(hotel);
    }


}
