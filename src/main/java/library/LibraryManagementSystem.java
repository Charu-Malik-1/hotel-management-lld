package library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class LibraryManagementSystem {

    public static void main(String[] args) {
        BookReservationSystem bookReservationSystem = new BookReservationSystem();
        Librarian librarian1 = new Librarian("mam1", bookReservationSystem);


        // AI book
        BookItem aiBook1 = new BookItem(1);
        BookItem aiBook2 = new BookItem(2);
        BookItem aiBook3 = new BookItem(3);
        List<BookItem> aiBooks = new ArrayList<>();
        aiBooks.add(aiBook1);
        aiBooks.add(aiBook2);
        aiBooks.add(aiBook3);
        Book ai = new Book("b1","AI", "charu malik", LocalDate.of(2022, 8, 8), aiBooks);


        // literature book
        BookItem litBook1 = new BookItem(4);
        BookItem litBook2 = new BookItem(5);
        BookItem litBook3 = new BookItem(6);
        List<BookItem> litBooks = new ArrayList<>();
        litBooks.add(litBook1);
        litBooks.add(litBook2);
        litBooks.add(litBook3);
        Book lit = new Book("b2","LIT", "charu malik", LocalDate.of(2023, 8, 8), litBooks);

        // Math Book
        BookItem mathBook1 = new BookItem(7);
        BookItem mathBook2 = new BookItem(8);
        List<BookItem> mathBooks = new ArrayList<>();
        mathBooks.add(mathBook1);
        mathBooks.add(mathBook2);
        Book math = new Book("b3","MATH", "charu malik", LocalDate.of(2022, 8, 8), mathBooks);

        // issue book

        Member member1 = new Member("kai");
        librarian1.issueBook(math, member1);

        Member member2 = new Member("rani");
        librarian1.issueBook(math, member2);
        Member member3 = new Member("rakesh");
        librarian1.issueBook(math, member3);

        System.out.println("Lib management System");
    }
}

class BookItem {
    int bookItemId;
    boolean isAvailable;
    LocalDate dateOfIssue;
    LocalDate dateOfReturn;

    BookItem(int id) {
        bookItemId = id;
        isAvailable = true;
        dateOfIssue = null;
        dateOfReturn = null;
    }
}

class Book {
    String bookId;
    String name;
    String authorName;
    LocalDate dateOfPublication;
    List<BookItem> bookItems;
    Rack rack;

    Book(String bookId,String name, String authorName, LocalDate dateOfPublication, List<BookItem> bookItems) {
        this.bookId = bookId;
        this.name = name;
        this.authorName = authorName;
        this.dateOfPublication = dateOfPublication;
        this.bookItems = bookItems;
    }

    void addBookItem(BookItem bookItem) {
        bookItems.add(bookItem);
    }
}

class Rack {
    int rackId;

    Rack() {
        rackId = new Random().nextInt();
    }
}

enum AccountStatus {
    ACTIVE, INACTIVE;
}

class Account {
    String userName;
    String password;
    AccountStatus accountStatus;

    Account(String u, String p) {
        userName = u;
        password = p;
        accountStatus = AccountStatus.ACTIVE;
    }
}

abstract class Person {
    String name;
    int libraryCardId;

    //  Account account;
    Person(String n) {
        name = n;
        libraryCardId = new Random().nextInt();
    }
}

class Member extends Person {

    List<BookItem> issuedBookHistory;
    int fine;
    List<BookItem> currentIssuedBooks;
    int totalIssuedBooks;

    Member(String name) {
        super(name);
        issuedBookHistory = new ArrayList<>();
        fine = 0;
        totalIssuedBooks = 0;
        currentIssuedBooks = new ArrayList<>();
    }

    void getCurrentIssuedBooks() {
    }

    void payFine() {
    }

    void searchBookByName(String name) {

    }
}

class Librarian extends Person {
    BookReservationSystem bookReservationSystem;

    Librarian(String name, BookReservationSystem bookReservationSystem) {
        super(name);
        this.bookReservationSystem = bookReservationSystem;
    }

    void issueBook(Book book, Member member) {
        bookReservationSystem.issueBook(book, member);
    }

    void returnBook(BookItem bookItem, Member member) {
        bookReservationSystem.returnBook(bookItem, member);
    }

    void addBookItem(){

    }
}


class BookReservationSystem {
    Map<Member, List<BookItem>> issuedBooks;

    BookReservationSystem() {
        issuedBooks = new HashMap<>();
    }

    void issueBook(Book book, Member member) {
        if (member.totalIssuedBooks + 1 <= 10) {
            BookItem bookItem = checkBookAvailability(book);
            if (bookItem != null) {
                member.currentIssuedBooks.add(bookItem);
                bookItem.isAvailable = false;
                bookItem.dateOfIssue = LocalDate.now();
                bookItem.dateOfReturn = LocalDate.now().plusDays(15);
                member.totalIssuedBooks++;

                //update issued Books
                List<BookItem> issueBookItems = issuedBooks.get(member);
                if (issueBookItems == null) {
                    issueBookItems = new ArrayList<>();
                }
                issueBookItems.add(bookItem);
                issuedBooks.put(member, issueBookItems);
            }
        } else {
            System.out.println("member already issued 10 books");
        }
    }

    private BookItem checkBookAvailability(Book book) {
        for (int i = 0; i < book.bookItems.size(); i++) {
            BookItem bookItem = book.bookItems.get(i);
            if (bookItem.isAvailable) {
                return bookItem;
            }
        }
        return null;
    }

    void returnBook(BookItem bookItem, Member member) {
        if (bookItem.dateOfReturn.isBefore(LocalDate.now())) {
            long daysDifference = ChronoUnit.DAYS.between(LocalDate.now(), bookItem.dateOfReturn);
            long fine = daysDifference * 10;
            if (fine > 0) {
                if (payFine(fine)) {
                    bookItem.isAvailable = true;
                    bookItem.dateOfIssue = null;
                    bookItem.dateOfReturn = null;
                    member.currentIssuedBooks.remove(bookItem);
                    member.issuedBookHistory.add(bookItem);
                    member.totalIssuedBooks--;
                }
            }
        }
    }

    boolean payFine(long fine) {
        return true;
    }
}

class Search {
    HashMap<String,List<Book>> bookTitle;
    HashMap<String,List<Book>> bookAuthors;
    HashMap<String,List<Book>> bookSubject;

    List<Book> searchByName(String name) {
     if (bookTitle.containsKey(name)){
         return bookTitle.get(name);
     }
     return null;
    }

    List<Book> searchByAuthor(String author) {
        if (bookAuthors.containsKey(author)){
            return bookAuthors.get(author);
        }
        return null;
    }

    List<Book> searchBySubject(String subject) {
        if (bookSubject.containsKey(subject)){
            return bookSubject.get(subject);
        }
        return null;
    }
}

