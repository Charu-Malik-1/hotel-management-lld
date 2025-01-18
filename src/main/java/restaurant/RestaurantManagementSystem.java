package restaurant;

import lombok.Getter;
import lombok.Setter;

import javax.print.attribute.standard.OrientationRequested;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

enum AccountStatus {
    ACTIVE, INACTIVE
}

public class RestaurantManagementSystem {

    static void initialise() {
        Restaurant kohinoor;

    }

    public static void main(String args[]) {

        Restaurant kohinoor = new Restaurant("kohinoor");

        Address address = new Address("delhi");

        //create Menu
        MenuSection dessert = new Dessert();
        MenuItem rasgulla = new MenuItem("rasgulla", "", 100);
        MenuItem icecream = new MenuItem("icecream", "", 250);
        dessert.addItem(rasgulla);
        dessert.addItem(icecream);

        MenuSection mainCourse = new MainCourse();
        MenuItem thali = new MenuItem("thali", "", 500);
        MenuItem paneer = new MenuItem("paneer", "", 350);
        mainCourse.addItem(thali);
        mainCourse.addItem(paneer);
        Menu delhiMenu = new Menu();
        delhiMenu.addSection(dessert);
        delhiMenu.addSection(mainCourse);


        //receptionist
        Account account = new Account("user1", "pass", AccountStatus.ACTIVE);
        Person person = new Person("cha", 1);
        Receptionist receptionist = new Receptionist(account, "1", person);

        //branch
        Branch delhiBranch = new Branch(address, delhiMenu, receptionist);
        Waiter waiter1 = new Waiter(account, "2", person, delhiBranch);
        Waiter waiter2 = new Waiter(account, "3", person, delhiBranch);

        //waiters
        delhiBranch.addWaiters(waiter1);
        delhiBranch.addWaiters(waiter2);

        //Table
        Table table1 = new Table(1, 8);
        Table table2 = new Table(2, 8);
        Table table3 = new Table(3, 4);
        Table table4 = new Table(4, 4);
        Table table5 = new Table(5, 2);
        Table table6 = new Table(5, 2);
        delhiBranch.addTable(table1);
        delhiBranch.addTable(table2);
        delhiBranch.addTable(table3);
        delhiBranch.addTable(table4);
        delhiBranch.addTable(table5);
        delhiBranch.addTable(table6);

        kohinoor.addBranch(delhiBranch);

        //Order
        Guest guest = new Guest("raj", 1);
        ReservationSystem reservationSystem = new ReservationSystem(delhiBranch);
        Table t = reservationSystem.allocateTable(guest);
        Random random = new Random();
        List<MealItem> meamIteams = new ArrayList<>();
        meamIteams.add(new MealItem(2, rasgulla));
        meamIteams.add(new MealItem(2, paneer));
        List<Meal> meals = new ArrayList<>();
        meals.add(new Meal(meamIteams));
        Order order1 = new Order(random.nextInt(), t, meals);
        int cost = waiter1.takeOrder(order1);
        Payment payment = new CreditCard(cost, order1, "aaa", "aaa", LocalDateTime.now().plusYears(5));
        Bill bill = new Bill(order1, payment, cost);
        bill.processPayment();

    }
}

class Kitchen {
    void receiveOrder(Order order) {
        order.setOrderStatus(OrderStatus.INPROCESS);
        if (prepareOrder(order)) {
            order.setOrderStatus(OrderStatus.READY);
        }
    }

    private boolean prepareOrder(Order order) {
        return true;
    }
}

class Address {
    String city;

    Address(String city) {
        this.city = city;
    }
}

class Person {
    String name;
    int id;

    Person(String name, int id) {
        this.name = name;
        this.id = id;
    }
}


class Guest extends Person {
    Branch branch;
    Table table;

    Guest(String name, int id) {
        super(name, id);
//        this.branch = branch;
    }

}

class ReservationSystem {
    Branch branch;

    ReservationSystem(Branch b) {
        this.branch = b;
    }

    Table allocateTable(Guest guest) {
        List<Table> tables = branch.tables;
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).isAvailable) {
                tables.get(i).isAvailable = false;
                return tables.get(i);
            }
        }
        return null;
    }
}


class Restaurant {
    String name;
    List<Branch> branches;

    Restaurant(String name) {
        this.name = name;
        branches = new ArrayList<>();
    }

    void addBranch(Branch branch) {
        branches.add(branch);
    }

    void removeBranch(Branch branch) {
        branches.remove(branch);
    }
}

@Getter
@Setter
class Branch {
    Address address;
    Menu menu;
    List<Table> tables;
    List<Waiter> waiters;
    Receptionist receptionist;
    Kitchen kitchen;

    Branch(Address address, Menu menu, Receptionist receptionist) {
        this.address = address;
        this.menu = menu;
        tables = new ArrayList<>();
        waiters = new ArrayList<>();
        this.receptionist = receptionist;
        kitchen = new Kitchen();
    }

    void addTable(Table table) {
        tables.add(table);
    }

    void addWaiters(Waiter waiter) {
        waiters.add(waiter);
    }

    void removeTable(Table table) {
        tables.remove(table);
    }

    void removeWaiters(Waiter waiter) {
        waiters.remove(waiter);
    }
}

class Table {
    int tableNum;
    boolean isAvailable;
    int capacity;
    LocalDateTime checkIn;
    LocalDateTime checkOut;

    Table(int tableNum, int capacity) {
        this.tableNum = tableNum;
        isAvailable = true;
        this.capacity = capacity;
        checkIn = null;
        checkOut = null;
    }

    void bookTable(LocalDateTime checkIn, LocalDateTime checkOut) {
        if (isAvailable) {
            isAvailable = false;
        }
    }

    void addMoreItem() {
    }

    boolean eatOrder(Order order) {
        return freeTable(order);
    }

    boolean freeTable(Order o) {
        isAvailable = true;
        o.setOrderStatus(OrderStatus.COMPLETED);
        return true;
    }
}

enum OrderStatus {
    READY, SERVED, INPROCESS, TAKEN, COMPLETED
}

@Getter
@Setter
class Order {
    int orderId;
    Table table;
    List<Meal> meals;
    OrderStatus orderStatus;
    LocalDateTime timeOfOrder;


    Order(int orderId, Table t, List<Meal> items) {
        this.orderId = orderId;
        table = t;
        this.meals = items;
        orderStatus = OrderStatus.INPROCESS;
        timeOfOrder = LocalDateTime.now();
    }

    void addItemToOrder(Meal item) {
        meals.add(item);
    }

    void removeItemFromOrder(Meal item) {
        meals.remove(item);
    }
}

enum PaymentStatus {
    PENDING, COMPLETED, FAILED, CANCELLED
}

@Getter
abstract class Payment {
    protected double amount;
    protected PaymentStatus status;
    protected LocalDateTime timestamp;
    protected Order order;

    public Payment(double amount, Order ticket) {
        this.amount = amount;
        this.order = ticket;
        this.status = PaymentStatus.PENDING;
        this.timestamp = LocalDateTime.now();
    }

    public abstract boolean initiateTransaction();
}

// Concrete parking.Payment classes
@Getter
class Cash extends Payment {
    private double cashTendered;
    private double change;

    public Cash(double amount, Order ticket, double cashTendered) {
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

    public CreditCard(double amount, Order ticket, String cardNumber,
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
        // parking.Payment gateway integration would go here
        return true;
    }
}

abstract class Employee extends Person {
    String empId;
    Account account;

    Employee(String empId, Account account, Person person) {
        super(person.name, person.id);
        this.empId = empId;
        this.account = account;
    }
}

class Account {
    String userName;
    String password;
    AccountStatus accountStatus;

    Account(String userName, String password, AccountStatus accountStatus) {
        this.userName = userName;
        this.password = password;
        this.accountStatus = accountStatus;
    }
}

class Receptionist extends Employee {
    Receptionist(Account account, String empId, Person person) {
        super(empId, account, person);
    }

    ReservationSystem makeReservation(LocalDate checkIn, LocalDate checkout, int numOfPeople) {
        return new ReservationSystem(new Branch(null, null, null));
    }

    void cancelReservation(ReservationSystem reservation) {
    }
}

class Bill {
    int billId;
    Order order;
    Payment payment;
    int amount;
    LocalDateTime time;

    Bill(Order order, Payment payment, int amount) {
        billId = new Random().nextInt();
        this.order = order;
        this.payment = payment;
        this.amount = amount;
        this.time = LocalDateTime.now();
    }

    void processPayment() {
        payment.initiateTransaction();
    }
}

class Waiter extends Employee {
    List<Order> orders;
    Branch branch;

    Waiter(Account account, String empId, Person person, Branch branch) {
        super(empId, account, person);
        orders = new ArrayList<>();
        this.branch = branch;
    }

    int takeOrder(Order order) {
        order.setOrderStatus(OrderStatus.TAKEN);
        orders.add(order);
        branch.getKitchen().receiveOrder(order);
        return serveOrder(order);
    }

    int serveOrder(Order order) {
        order.setOrderStatus(OrderStatus.SERVED);
        int bill = 0;
        if (order.table.eatOrder(order)) {
            for (int i = 0; i < order.getMeals().size(); i++) {
                List<MealItem> l = order.getMeals().get(i).mealItems;
                for (int j = 0; j < l.size(); j++) {
                    bill += l.get(j).quantity * l.get(j).menuItem.price;
                }
            }
        }
        return bill;
    }

    void cancelOrder(Order order) {
    }

    void updateOrder(Order order) {
    }
}

class Menu {
    List<MenuSection> menuSectionList;

    Menu() {
        menuSectionList = new ArrayList<>();
    }

    void addSection(MenuSection menuSection) {
        menuSectionList.add(menuSection);
    }

    void removeSection(MenuSection menuSection) {
        menuSectionList.remove(menuSection);
    }
}

abstract class MenuSection {
    String name;

    MenuSection(String name) {
        this.name = name;
    }

    abstract void addItem(MenuItem menuItem);

    abstract void removeItem(MenuItem menuItem);
}

class Dessert extends MenuSection {
    List<MenuItem> menuItems;

    Dessert() {
        super("dessert");
        menuItems = new ArrayList<>();
    }

    void addItem(MenuItem menuItem) {
        menuItems.add(menuItem);
    }

    void removeItem(MenuItem menuItem) {
        menuItems.remove(menuItem);
    }
}

class MainCourse extends MenuSection {
    List<MenuItem> menuItems;

    MainCourse() {
        super("MainCourse");
        menuItems = new ArrayList<>();
    }

    void addItem(MenuItem menuItem) {
        menuItems.add(menuItem);
    }

    void removeItem(MenuItem menuItem) {
        menuItems.remove(menuItem);
    }
}

class MenuItem {
    String name;
    String description;
    int price;

    MenuItem(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}

class Meal {
    int mealId;
    List<MealItem> mealItems;


    Meal(List<MealItem> mealItems) {
        mealId = new Random().nextInt();

        this.mealItems = mealItems;
    }
}

class MealItem {
    int mealIdItem;
    int quantity;
    MenuItem menuItem;

    MealItem(int qty, MenuItem menuItem) {
        mealIdItem = new Random().nextInt();
        quantity = qty;
        this.menuItem = menuItem;
    }

    boolean updateQuantity(int qty) {
        quantity = qty;
        return true;
    }
}

class TableSeat {
}


class Chef {
}




