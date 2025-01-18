import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;


enum VehicleType {
    CAR, TRUCK, MOTORCYCLE, VAN
}
// First, define spot types enum
enum ParkingSpotType {
    LARGE(8.0),     // size in meters
    COMPACT(5.0),
    MOTORCYCLE(2.0),
    HANDICAPPED(8.0);

    private final double size;

    ParkingSpotType(double size) {
        this.size = size;
    }

    public double getSize() {
        return size;
    }
}

abstract class ParkingSpot {
    protected int id;
    protected boolean isFree;
    protected final ParkingSpotType type;
    protected Vehicle parkedVehicle;

    public ParkingSpot(int id, ParkingSpotType type) {
        this.id = id;
        this.type = type;
        this.isFree = true;
    }

    public boolean getIsFree() {
        return isFree;
    }

    public ParkingSpotType getType() {
        return type;
    }

    public boolean assignVehicle(Vehicle vehicle) {
        if (isFree && canFitVehicle(vehicle)) {
            this.parkedVehicle = vehicle;
            this.isFree = false;
            return true;
        }
        return false;
    }

    public void removeVehicle() {
        this.parkedVehicle = null;
        this.isFree = true;
    }

    protected abstract boolean canFitVehicle(Vehicle vehicle);
}

// Concrete spot classes
class Large extends ParkingSpot {
    public Large(int id) {
        super(id, ParkingSpotType.LARGE);
    }

    @Override
    protected boolean canFitVehicle(Vehicle vehicle) {
        return vehicle instanceof Car ||
                vehicle instanceof Van ||
                vehicle instanceof Truck;
    }
}

class Compact extends ParkingSpot {
    public Compact(int id) {
        super(id, ParkingSpotType.COMPACT);
    }

    @Override
    protected boolean canFitVehicle(Vehicle vehicle) {
        return vehicle instanceof Car;
    }
}

class MotorcycleSpot extends ParkingSpot {
    public MotorcycleSpot(int id) {
        super(id, ParkingSpotType.MOTORCYCLE);
    }

    @Override
    protected boolean canFitVehicle(Vehicle vehicle) {
        return vehicle instanceof MotorCycle;
    }
}

class HandicappedSpot extends ParkingSpot {
    private boolean hasRamp;

    public HandicappedSpot(int id, boolean hasRamp) {
        super(id, ParkingSpotType.HANDICAPPED);
        this.hasRamp = hasRamp;
    }

    @Override
    protected boolean canFitVehicle(Vehicle vehicle) {
        return vehicle instanceof Car || vehicle instanceof Van;
    }
}

@Getter
// Abstract Vehicle class
abstract class Vehicle {
    protected String licenseNo;
    protected ParkingTicket ticket;
    protected VehicleType type;

    public Vehicle(String licenseNo, VehicleType type) {
        this.licenseNo = licenseNo;
        this.type = type;
    }

    public void assignTicket(ParkingTicket ticket) {
        this.ticket = ticket;
    }
}

class Car extends Vehicle {
    public Car(String licenseNo) {
        super(licenseNo, VehicleType.CAR);
    }
}

class Truck extends Vehicle {
    private double cargoCapacity;

    public Truck(String licenseNo, double cargoCapacity) {
        super(licenseNo, VehicleType.TRUCK);
        this.cargoCapacity = cargoCapacity;
    }
}

class MotorCycle extends Vehicle {
    public MotorCycle(String licenseNo) {
        super(licenseNo, VehicleType.MOTORCYCLE);
    }
}

class Van extends Vehicle {
    public Van(String licenseNo) {
        super(licenseNo, VehicleType.VAN);
    }
}

// Abstract Payment class
@Getter
abstract class Payment {
    protected double amount;
    protected PaymentStatus status;
    protected LocalDateTime timestamp;
    protected ParkingTicket ticket;

    public Payment(double amount, ParkingTicket ticket) {
        this.amount = amount;
        this.ticket = ticket;
        this.status = PaymentStatus.PENDING;
        this.timestamp = LocalDateTime.now();
    }

    public abstract boolean initiateTransaction();
}

// Concrete Payment classes
@Getter
class Cash extends Payment {
    private double cashTendered;
    private double change;

    public Cash(double amount, ParkingTicket ticket, double cashTendered) {
        super(amount, ticket);
        this.cashTendered = cashTendered;
        this.change = cashTendered - amount;
    }

    @Override
    public boolean initiateTransaction() {
        if (cashTendered >= amount) {
            status = PaymentStatus.COMPLETED;
            return true;
        }
        status = PaymentStatus.FAILED;
        return false;
    }
}

@Getter
class CreditCard extends Payment {
    private String cardNumber;
    private String cardHolderName;
    private LocalDateTime expiryDate;

    public CreditCard(double amount, ParkingTicket ticket, String cardNumber,
                      String cardHolderName, LocalDateTime expiryDate) {
        super(amount, ticket);
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
    }

    @Override
    public boolean initiateTransaction() {
        // Simulate credit card processing
        if (isValidCard() && processPayment()) {
            status = PaymentStatus.COMPLETED;
            return true;
        }
        status = PaymentStatus.FAILED;
        return false;
    }

    private boolean isValidCard() {
        return !expiryDate.isBefore(LocalDateTime.now());
    }

    private boolean processPayment() {
        // Payment gateway integration would go here
        return true;
    }
}

@Getter
class ParkingTicket {
    private int ticketNo;
    private LocalDateTime issueTime;
    private LocalDateTime exitTime;
    private double amount;
    private Payment payment;
    private Vehicle vehicle;
    private ParkingSpot spot;

    public ParkingTicket(int ticketNo, Vehicle vehicle, ParkingSpot spot) {
        this.ticketNo = ticketNo;
        this.issueTime = LocalDateTime.now();
        this.vehicle = vehicle;
        this.spot = spot;
    }

    public void calculateBill(ParkingRate rate) {
        long hours = java.time.Duration.between(issueTime.minusHours(1),
                exitTime != null ? exitTime : LocalDateTime.now()).toHours();
        this.amount = rate.calculate(hours, vehicle.type);
    }

    public void setPaymentDetails(Payment payment) {
        this.payment = payment;
        this.exitTime = LocalDateTime.now();
    }
}

class ParkingRate {
    private Map<VehicleType, Double> hourlyRates;

    public ParkingRate() {
        hourlyRates = new HashMap<>();
        // Initialize default rates
        hourlyRates.put(VehicleType.CAR, 2.0);
        hourlyRates.put(VehicleType.MOTORCYCLE, 1.0);
        hourlyRates.put(VehicleType.TRUCK, 4.0);
        hourlyRates.put(VehicleType.VAN, 3.0);
    }

    public double calculate(long hours, VehicleType vehicleType) {
        return hours * getRate(vehicleType);
    }

    public void setRate(VehicleType vehicleType, double rate) {
        hourlyRates.put(vehicleType, rate);
    }

    public double getRate(VehicleType vehicleType) {
        return hourlyRates.get(vehicleType);
    }
}

@Getter
class Exit {
    private int id;

    public Exit(int id) {
        this.id = id;
    }

    public void unparkVehicle(ParkingTicket ticket,ParkingRate parkingRate, ParkingAgent agent, Payment payment) {
        boolean paymentSuccess = agent.processTicket(ticket, payment, parkingRate);
        System.out.println("Payment processed: " + paymentSuccess);
        if (ticket.getPayment() == null ||
                ticket.getPayment().getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Ticket payment not completed");
        }

        // Free up the parking spot
        ticket.getSpot().removeVehicle();
    }
}

class Entrance {
    private int id;

    public Entrance(int id) {
        this.id = id;
    }

    public ParkingTicket issueTicket(Vehicle vehicle, ParkingSpot spot) {
        if (spot != null) {
            ParkingTicket ticket = new ParkingTicket(
                    generateTicketNumber(), vehicle, spot);
            return ticket;
        }

        throw new IllegalStateException("No parking spot available");
    }

    private int generateTicketNumber() {
        return new Random().nextInt(10000) + 1;
    }

    public int getId() {
        return id;
    }
}


@Getter
class ParkingLot {
    private int id;
    private String name;
    private Address address;
    private List<Entrance> entrances;
    private List<Exit> exits;
    private DisplayBoard displayBoard;
    private ParkingRate parkingRate;
    private Map<ParkingSpotType, List<ParkingSpot>> spotsByType;

    public ParkingLot(int id, String name, Address address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.entrances = new ArrayList<>();
        this.exits = new ArrayList<>();
        this.parkingRate = new ParkingRate();
//        this.displayBoard=new DisplayBoard()

        this.spotsByType = new EnumMap<>(ParkingSpotType.class);
        // Initialize lists for each spot type
        for (ParkingSpotType type : ParkingSpotType.values()) {
            spotsByType.put(type, new ArrayList<>());
        }
    }

    public ParkingTicket parkVehicle(Vehicle vehicle) {
        if (!isFull()) {
            // First check if spot is available
            ParkingSpot spot = findAvailableSpot(vehicle);
            if (spot == null) {
                throw new IllegalStateException("No parking spot available for this vehicle");
            }

            // Get ticket from any entrance
            if (!entrances.isEmpty()) {
                Entrance entrance = entrances.get(0);
                ParkingTicket ticket = entrance.issueTicket(vehicle, spot);
                if (ticket != null) {
                    // Associate ticket with spot and vehicle
                    spot.assignVehicle(vehicle);
                    vehicle.assignTicket(ticket);
                    return ticket;
                }
            }
        }
        throw new IllegalStateException("No entrance available to issue ticket");
    }

    public ParkingSpot findAvailableSpot(Vehicle vehicle) {
        // First try ideal spot type
        ParkingSpotType idealType = getIdealSpotType(vehicle);
        ParkingSpot spot = findSpotInType(idealType, vehicle);

        // If no ideal spot, try other compatible types
        if (spot == null && vehicle instanceof MotorCycle) {
            // Motorcycles can park in any spot
            for (ParkingSpotType type : ParkingSpotType.values()) {
                spot = findSpotInType(type, vehicle);
                if (spot != null) break;
            }
        }

        return spot;
    }

    private ParkingSpot findSpotInType(ParkingSpotType type, Vehicle vehicle) {
        return spotsByType.get(type).stream()
                .filter(spot -> spot.getIsFree() && spot.canFitVehicle(vehicle))
                .findFirst()
                .orElse(null);
    }

    private ParkingSpotType getIdealSpotType(Vehicle vehicle) {
        if (vehicle instanceof MotorCycle) return ParkingSpotType.MOTORCYCLE;
        if (vehicle instanceof Car) return ParkingSpotType.COMPACT;
        if (vehicle instanceof Truck || vehicle instanceof Van) return ParkingSpotType.LARGE;
        return ParkingSpotType.LARGE; // default
    }

    public boolean isFull() {
        // Check if all spot types are full
        return spotsByType.values().stream()
                .flatMap(List::stream)
                .noneMatch(ParkingSpot::getIsFree);
    }

    public void addParkingSpot(ParkingSpot spot) {
        spotsByType.get(spot.getType()).add(spot);
        updateDisplayBoard();
    }

    private void updateDisplayBoard() {
        if (displayBoard != null) {
            for (Map.Entry<ParkingSpotType, List<ParkingSpot>> entry : spotsByType.entrySet()) {
                displayBoard.addParkingSpot(
                        entry.getKey().toString(),
                        entry.getValue()
                );
            }
        }
    }

    public Map<ParkingSpotType, Integer> getAvailableSpotCounts() {
        Map<ParkingSpotType, Integer> counts = new EnumMap<>(ParkingSpotType.class);
        spotsByType.forEach((type, spots) ->
                counts.put(type, (int) spots.stream().filter(ParkingSpot::getIsFree).count())
        );
        return counts;
    }

    // Management methods
    public void addEntrance(Entrance entrance) {
        entrances.add(entrance);
    }

    public void addExit(Exit exit) {
        exits.add(exit);
    }

    public void setDisplayBoard(DisplayBoard board) {
        this.displayBoard = board;
    }

    // Getters
    public ParkingRate getParkingRate() {
        return parkingRate;
    }

    public List<Entrance> getEntrances() {
        return entrances;
    }

    public List<Exit> getExits() {
        return exits;
    }
}


// DisplayBoard class
class DisplayBoard {
    private int id;
    private Map<String, List<ParkingSpot>> parkingSpots;

    public DisplayBoard(int id) {
        this.id = id;
        this.parkingSpots = new HashMap<>();
    }

    public void addParkingSpot(String spotType, List<ParkingSpot> spots) {
        parkingSpots.put(spotType, spots);
    }

    public void showFreeSlot() {
        System.out.println("Current Parking Status:");
        parkingSpots.forEach((type, spots) -> {
            long freeSpots = spots.stream()
                    .filter(ParkingSpot::getIsFree)
                    .count();
            System.out.println(type + ": " + freeSpots + " spots available");
        });
    }
}


// Abstract Account class
abstract class Account {
    protected String userName;
    protected String password;
    protected AccountStatus status;
    protected Person person;

    public Account(String userName, String password, Person person) {
        this.userName = userName;
        this.password = password;
        this.person = person;
        this.status = AccountStatus.ACTIVE;
    }

    public boolean resetPassword(String newPassword) {
        if (isValidPassword(newPassword)) {
            this.password = newPassword;
            return true;
        }
        return false;
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    // Getters
    public String getUserName() {
        return userName;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Person getPerson() {
        return person;
    }
}

class ParkingAgent extends Account {

    public ParkingAgent(String userName, String password, Person person) {
        super(userName, password, person);
    }

    public boolean processTicket(ParkingTicket ticket, Payment payment, ParkingRate parkingRate) {
        if (ticket != null && payment != null) {
            ticket.calculateBill(parkingRate);
            if (payment.initiateTransaction())
            {
                ticket.setPaymentDetails(payment);
                return true;
            }
        }
        return false;
    }
}

class Admin extends Account {
    public Admin(String userName, String password, Person person) {
        super(userName, password, person);
    }

    public boolean addParkingSpot(ParkingSpot spot, ParkingLot parkingLot) {
        try {
            parkingLot.addParkingSpot(spot);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addEntrance(Entrance entrance, ParkingLot parkingLot) {
        try {
            parkingLot.getEntrances().add(entrance);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addExit(Exit exit, ParkingLot parkingLot) {
        try {
            parkingLot.getExits().add(exit);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addDisplayBoard(DisplayBoard displayBoard, ParkingLot parkingLot) {
        try {
            parkingLot.setDisplayBoard(displayBoard);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

// Support classes
class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    public Address(String street, String city, String state,
                   String zipCode, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }
}

class Person {
    private String name;
    private String email;
    private String phone;

    public Person(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}


// Enums
enum PaymentStatus {
    PENDING, COMPLETED, FAILED, CANCELLED
}

enum AccountStatus {
    ACTIVE, CLOSED, CANCELED, BLACKLISTED, BLOCKED
}

// Main class to demonstrate the parking system
public class Parking_Lot_Latest {
    public static void main(String[] args) {
        try {
            // Initialize the parking lot
//            Address address = new Address("123 Main St", "City", "State", "12345", "Country");
//            ParkingLot parkingLot = new ParkingLot(1, "Central Parking", address);

            Address parkingAddress = new Address(
                    "123 Main St",
                    "Metropolis",
                    "State",
                    "12345",
                    "Country"
            );
            ParkingLot parkingLot = new ParkingLot(1, "Downtown Parking", parkingAddress);

            // Create admin
            Person adminPerson = new Person("Admin John", "admin@parking.com", "123-456-7890");
            Admin admin = new Admin("admin1", "admin123", adminPerson);

            // Add entrance and exit
            Entrance entrance = new Entrance(1);
            Exit exit = new Exit(1);
            admin.addEntrance(entrance, parkingLot);
            admin.addExit(exit, parkingLot);

            // Add parking spots
            admin.addParkingSpot(new Large(1), parkingLot);
            admin.addParkingSpot(new Compact(2), parkingLot);
//            admin.addParkingSpot(new Motorcycle(3), parkingLot);

            // Create parking agent
            Person agentPerson = new Person("John Agent", "john@parking.com", "123-456-7890");
            ParkingAgent agent = new ParkingAgent("agent1", "pass123", agentPerson);

            // Simulate parking process
            System.out.println("=== Parking System Demo ===");


            // Car parks
            Car car = new Car("ABC123");
            ParkingTicket ticket = parkingLot.parkVehicle(car);
            System.out.println("Ticket issued for car: " + car.licenseNo);

            // Calculate parking fee
            Thread.sleep(2000); // Simulate time passing

            // Process payment
            Payment creditCardPayment = new CreditCard(10.0, ticket, "11111", "cha", LocalDateTime.now().plusYears(20));
            // Exit
            exit.unparkVehicle(ticket,parkingLot.getParkingRate(),agent,creditCardPayment);
            System.out.println("Car exited successfully");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}