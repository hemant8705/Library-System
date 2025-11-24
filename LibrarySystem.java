import java.io.*;
import java.util.*;

class Book implements Comparable<Book> {
    int bookId;
    String title;
    String author;
    String category;
    boolean isIssued;

    public Book(int bookId, String title, String author, String category, boolean isIssued) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isIssued = isIssued;
    }

    public void displayBookDetails() {
        System.out.println("Book ID: " + bookId);
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("Category: " + category);
        System.out.println("Issued: " + (isIssued ? "Yes" : "No"));
    }

    public void markAsIssued() {
        isIssued = true;
    }

    public void markAsReturned() {
        isIssued = false;
    }

    @Override
    public int compareTo(Book b) {
        return this.title.compareToIgnoreCase(b.title);
    }

    @Override
    public String toString() {
        return bookId + "," + title + "," + author + "," + category + "," + isIssued;
    }
}

class Member {
    int memberId;
    String name;
    String email;
    List<Integer> issuedBooks = new ArrayList<>();

    public Member(int memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
    }

    public void addIssuedBook(int bookId) {
        issuedBooks.add(bookId);
    }

    public void returnIssuedBook(int bookId) {
        issuedBooks.remove(Integer.valueOf(bookId));
    }

    public void displayMemberDetails() {
        System.out.println("Member ID: " + memberId);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Issued Books: " + issuedBooks);
    }

    @Override
    public String toString() {
        return memberId + "," + name + "," + email + "," + issuedBooks.toString();
    }
}

public class LibrarySystem {

    Map<Integer, Book> books = new HashMap<>();
    Map<Integer, Member> members = new HashMap<>();

    final String BOOK_FILE = "books.txt";
    final String MEMBER_FILE = "members.txt";

    Scanner sc = new Scanner(System.in);

    public void loadFromFile() {
        try {
            File file = new File(BOOK_FILE);
            if (!file.exists()) file.createNewFile();

            BufferedReader br = new BufferedReader(new FileReader(BOOK_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Book b = new Book(
                        Integer.parseInt(data[0]),
                        data[1],
                        data[2],
                        data[3],
                        Boolean.parseBoolean(data[4])
                );
                books.put(b.bookId, b);
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error loading books.");
        }

        try {
            File file = new File(MEMBER_FILE);
            if (!file.exists()) file.createNewFile();

            BufferedReader br = new BufferedReader(new FileReader(MEMBER_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Member m = new Member(
                        Integer.parseInt(data[0]),
                        data[1],
                        data[2]
                );
                members.put(m.memberId, m);
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error loading members.");
        }
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BOOK_FILE))) {
            for (Book b : books.values()) {
                bw.write(b.toString());
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("Error saving books.");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MEMBER_FILE))) {
            for (Member m : members.values()) {
                bw.write(m.toString());
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("Error saving members.");
        }
    }

    public void addBook() {
        System.out.print("Enter Book ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Title: ");
        String title = sc.nextLine();

        System.out.print("Enter Author: ");
        String author = sc.nextLine();

        System.out.print("Enter Category: ");
        String category = sc.nextLine();

        Book b = new Book(id, title, author, category, false);
        books.put(id, b);

        saveToFile();
        System.out.println("Book added successfully!");
    }

    public void addMember() {
        System.out.print("Enter Member ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        Member m = new Member(id, name, email);
        members.put(id, m);

        saveToFile();
        System.out.println("Member added successfully!");
    }

    public void issueBook() {
        System.out.print("Enter Member ID: ");
        int mid = sc.nextInt();

        System.out.print("Enter Book ID: ");
        int bid = sc.nextInt();

        if (!books.containsKey(bid)) {
            System.out.println("Book not found!");
            return;
        }

        if (!members.containsKey(mid)) {
            System.out.println("Member not found!");
            return;
        }

        Book b = books.get(bid);
        Member m = members.get(mid);

        if (b.isIssued) {
            System.out.println("Book already issued!");
            return;
        }

        b.markAsIssued();
        m.addIssuedBook(bid);

        saveToFile();
        System.out.println("Book issued successfully!");
    }

    public void returnBook() {
        System.out.print("Enter Book ID: ");
        int bid = sc.nextInt();

        if (!books.containsKey(bid)) {
            System.out.println("Book not found!");
            return;
        }

        books.get(bid).markAsReturned();

        for (Member m : members.values()) {
            m.returnIssuedBook(bid);
        }

        saveToFile();
        System.out.println("Book returned successfully!");
    }

    public void searchBooks() {
        sc.nextLine();
        System.out.print("Enter keyword to search: ");
        String key = sc.nextLine().toLowerCase();

        books.values().stream()
                .filter(b -> b.title.toLowerCase().contains(key)
                        || b.author.toLowerCase().contains(key)
                        || b.category.toLowerCase().contains(key))
                .forEach(Book::displayBookDetails);
    }

    public void sortBooks() {
        List<Book> list = new ArrayList<>(books.values());

        System.out.println("1. Sort by Title");
        System.out.println("2. Sort by Author");

        int ch = sc.nextInt();

        if (ch == 1) {
            Collections.sort(list);
        } else if (ch == 2) {
            list.sort(Comparator.comparing(b -> b.author.toLowerCase()));
        }

        list.forEach(Book::displayBookDetails);
    }

    public void menu() {
        loadFromFile();
        int choice;

        do {
            System.out.println("\n=== City Library Digital Management System ===");
            System.out.println("1. Add Book");
            System.out.println("2. Add Member");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Search Books");
            System.out.println("6. Sort Books");
            System.out.println("7. Exit");

            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1 -> addBook();
                case 2 -> addMember();
                case 3 -> issueBook();
                case 4 -> returnBook();
                case 5 -> searchBooks();
                case 6 -> sortBooks();
                case 7 -> {
                    saveToFile();
                    System.out.println("Exiting...");
                }
                default -> System.out.println("Invalid choice!");
            }

        } while (choice != 7);
    }

    public static void main(String[] args) {
        new LibrarySystem().menu();
    }
}
