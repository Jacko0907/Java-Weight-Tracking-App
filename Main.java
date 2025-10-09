import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        WeightTracker tracker = new WeightTracker("weights.txt");

        while (true) {
            System.out.println("1. Add new entry");
            System.out.println("2. View all entries");
            System.out.println("3. Delete entry by date");
            System.out.println("4. View stats");
            System.out.println("5. Update entry by date");
            System.out.println("6. Buy sweet treat (-1 Blipcoin)");
            System.out.println("7. Set or view height");
            System.out.println("8. Set or view goal weight");
            System.out.println("9. Exit");




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

                        tracker.addEntry(new WeightEntry(date, weight), scanner);
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

                case 4:
                    System.out.println("Average: " + tracker.averageWeight());
                    System.out.println("Min: " + tracker.minWeight());
                    System.out.println("Max: " + tracker.maxWeight());
                    System.out.println("Total lost: " + tracker.totalWeightLost());
                    System.out.println("Blipcoins earned: " + tracker.getBlipcoins());

                    if (tracker.getGoalWeight() > 0) {
                        System.out.println(tracker.getGoalProgressBar());
                        System.out.printf("You have %.1f lbs left to reach your goal.%n", tracker.poundsToGoal());
                    } else {
                        System.out.println("No goal weight set yet.");
                    }
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

                case 7: // height setup
                    System.out.println("Current height: " + tracker.getHeight() + " inches");
                    System.out.print("Enter new height in inches (or 0 to keep current): ");
                    double h = scanner.nextDouble();
                    scanner.nextLine();
                    if (h > 0) {
                        tracker.setHeight(h);
                        System.out.println("Height updated.");
                    }
                    break;

                case 8: // goal setup
                    System.out.println("Current goal weight: " + tracker.getGoalWeight());
                    System.out.print("Enter new goal weight (or 0 to keep current): ");
                    double g = scanner.nextDouble();
                    scanner.nextLine();
                    if (g > 0) {
                        tracker.setGoalWeight(g);
                        System.out.println("Goal weight set to " + g + " lbs.");
                    }
                    if (tracker.getGoalWeight() > 0) {
                        double remaining = tracker.poundsToGoal();
                        System.out.printf("You have %.1f lbs left to reach your goal.%n", remaining);
                    }
                    break;

                case 9:
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;



            }
        }
    }
}
