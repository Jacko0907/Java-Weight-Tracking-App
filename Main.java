import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        WeightTracker tracker = new WeightTracker("weights.txt");

        while (true) {
            System.out.println("\n--- Weight Tracker ---");
            System.out.println("1. Add new entry");
            System.out.println("2. View all entries");
            System.out.println("3. Delete entry by date");
            System.out.println("4. View stats (avg, min, max, total lost, rewards)");
            System.out.println("5. Update entry by date");
            System.out.println("6. Buy sweet treat (-1 Blipcoin)");
            System.out.println("7. Exit");
            System.out.print("Choose option: ");


            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Try again.");
                continue;
            }

            switch (choice) {
                case 1: // add entry
                    try {
                        System.out.print("Enter date (YYYY-MM-DD): ");
                        LocalDate date = LocalDate.parse(scanner.nextLine());

                        System.out.print("Enter weight: ");
                        double weight = Double.parseDouble(scanner.nextLine());

                        tracker.addEntry(new WeightEntry(date, weight));
                        System.out.println("Entry added.");
                    } catch (Exception e) {
                        System.out.println("Invalid input. Please try again.");
                    }
                    break;

                case 2: // view all entries
                    List<WeightEntry> entries = tracker.getEntries();
                    if (entries.isEmpty()) {
                        System.out.println("No entries found.");
                    } else {
                        for (WeightEntry entry : entries) {
                            System.out.println(entry.getDate() + " -> " + entry.getWeight());
                        }
                    }
                    break;

                case 3: // delete entry
                    try {
                        System.out.print("Enter date (YYYY-MM-DD) to delete: ");
                        LocalDate delDate = LocalDate.parse(scanner.nextLine());
                        tracker.deleteEntry(delDate);
                        System.out.println("Entry deleted (if it existed).");
                    } catch (Exception e) {
                        System.out.println("Invalid input. Try again.");
                    }
                    break;

                case 4: // stats
                    System.out.println("Average: " + tracker.averageWeight());
                    System.out.println("Min: " + tracker.minWeight());
                    System.out.println("Max: " + tracker.maxWeight());

                    double lost = tracker.totalWeightLost();
                    if (lost > 0) {
                        System.out.println("Total lost: " + lost);
                    } else if (lost < 0) {
                        System.out.println("Total gained: " + Math.abs(lost));
                    } else {
                        System.out.println("No change from starting weight.");
                    }

                    System.out.println("Blipcoins earned: " + tracker.getBlipcoins());
                    break;

                case 5: // update entry
                    try {
                        System.out.print("Enter date (YYYY-MM-DD) to update: ");
                        LocalDate updDate = LocalDate.parse(scanner.nextLine());

                        System.out.print("Enter new weight: ");
                        double newWeight = Double.parseDouble(scanner.nextLine());

                        tracker.updateEntry(updDate, newWeight);
                        System.out.println("Entry updated (if it existed).");
                    } catch (Exception e) {
                        System.out.println("Invalid input. Try again.");
                    }
                    break;

                case 6: // buy sweet treat
                    tracker.buySweetTreat();
                    break;

                case 7: // exit
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;


            }
        }
    }
}
