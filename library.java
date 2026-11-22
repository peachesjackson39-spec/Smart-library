package com.student;

import java.net.URLEncoder;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

// Name: ____  Student No: ____

public class App {
    static String[] rTitle = new String[20], rAuthor = new String[20], rUrl = new String[20];
    static String[] lTitle = new String[20], lAuthor = new String[20], lUrl = new String[20];
    static int rCount = 0, lCount = 0;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;
        do {
            System.out.println("\n===== GUTENBERG BOOK EXPLORER =====");
            System.out.println("1. Explore Books");
            System.out.println("2. Search by Title");
            System.out.println("3. Search by Author");
            System.out.println("4. Add Book to Reading List");
            System.out.println("5. View Reading List");
            System.out.println("6. Remove Book");
            System.out.println("7. Display Book Details");
            System.out.println("0. Exit");
            choice = readInt("Enter your choice: ");

            if (choice == 1) exploreBooks();
            else if (choice == 2) {
                System.out.print("Title: ");
                searchBook(sc.nextLine());
            } else if (choice == 3) {
                System.out.print("Author: ");
                searchBook(sc.nextLine(), true);
            } else if (choice == 4) addBook();
            else if (choice == 5) viewReadingList();
            else if (choice == 6) removeBook();
            else if (choice == 7) displayBookDetails();
            else if (choice != 0) System.out.println("Invalid choice.");
        } while (choice != 0);
        System.out.println("Goodbye!");
    }

    static int readInt(String msg) {
        System.out.print(msg);
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    static void exploreBooks() {
        try {
            Document doc = Jsoup.connect("https://www.gutenberg.org/browse/scores/top")
                    .userAgent("Mozilla/5.0").timeout(10000).get();
            rCount = 0;
            for (Element a : doc.select("a[href^=/ebooks/]")) {
                if (rCount == 20) break;
                if (!a.attr("href").matches("/ebooks/\\d+")) continue;
                String text = a.text().replaceAll("\\s*\\(\\d+\\)$", "");
                int i = text.lastIndexOf(" by ");
                rTitle[rCount] = i > 0 ? text.substring(0, i) : text;
                rAuthor[rCount] = i > 0 ? text.substring(i + 4) : "Unknown";
                rUrl[rCount] = a.absUrl("href");
                rCount++;
            }
            showResults();
        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }

    static void searchBook(String title) {
        searchBook(title, false);
    }

    static void searchBook(String keyword, boolean byAuthor) {
        if (keyword.trim().isEmpty()) {
            System.out.println("Search text cannot be empty.");
            return;
        }
        try {
            String q = (byAuthor ? "a." : "t.") + keyword.trim();
            Document doc = Jsoup.connect("https://www.gutenberg.org/ebooks/search/?query="
                    + URLEncoder.encode(q, "UTF-8")).userAgent("Mozilla/5.0").timeout(10000).get();
            rCount = 0;
            for (Element li : doc.select("li.booklink")) {
                if (rCount == 20) break;
                Element link = li.selectFirst("a.link");
                Element t = li.selectFirst("span.title");
                Element a = li.selectFirst("span.subtitle");
                if (link == null || t == null) continue;
                rTitle[rCount] = t.text();
                rAuthor[rCount] = a != null ? a.text() : "Unknown";
                rUrl[rCount] = link.absUrl("href");
                rCount++;
            }
            showResults();
        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }

    static void showResults() {
        if (rCount == 0) System.out.println("No books found.");
        for (int i = 0; i < rCount; i++)
            System.out.println((i + 1) + ". " + rTitle[i] + " - " + rAuthor[i]);
    }

    static void addBook() {
        if (rCount == 0) {
            System.out.println("Explore or search first.");
            return;
        }
        if (lCount == 20) {
            System.out.println("Reading list is full.");
            return;
        }
        showResults();
        int n = readInt("Book number to add: ");
        if (n < 1 || n > rCount) {
            System.out.println("Invalid number.");
            return;
        }
        for (int i = 0; i < lCount; i++) {
            if (lUrl[i].equals(rUrl[n - 1])) {
                System.out.println("Already in your list.");
                return;
            }
        }
        lTitle[lCount] = rTitle[n - 1];
        lAuthor[lCount] = rAuthor[n - 1];
        lUrl[lCount] = rUrl[n - 1];
        lCount++;
        System.out.println("Added: " + rTitle[n - 1]);
    }

    static void viewReadingList() {
        if (lCount == 0) System.out.println("Your reading list is empty.");
        for (int i = 0; i < lCount; i++)
            System.out.println((i + 1) + ". " + lTitle[i] + " - " + lAuthor[i]);
    }

    static void removeBook() {
        if (lCount == 0) {
            System.out.println("Your reading list is empty.");
            return;
        }
        viewReadingList();
        int n = readInt("Book number to remove: ");
        if (n < 1 || n > lCount) {
            System.out.println("Invalid number.");
            return;
        }
        for (int i = n - 1; i < lCount - 1; i++) {
            lTitle[i] = lTitle[i + 1];
            lAuthor[i] = lAuthor[i + 1];
            lUrl[i] = lUrl[i + 1];
        }
        lCount--;
        System.out.println("Book removed.");
    }

    static void displayBookDetails() {
        if (lCount == 0) {
            System.out.println("Your reading list is empty.");
            return;
        }
        viewReadingList();
        int n = readInt("Book number: ");
        if (n < 1 || n > lCount) {
            System.out.println("Invalid number.");
            return;
        }
        System.out.println("Title : " + lTitle[n - 1]);
        System.out.println("Author: " + lAuthor[n - 1]);
        System.out.println("URL   : " + lUrl[n - 1]);
    }
}
