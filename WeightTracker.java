import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WeightTracker {
    private File file;
    private List<WeightEntry> entries;
    private int spentBlipcoins;
    private double heightInInches;
    private double goalWeight; // new
    private boolean goalAchieved; // new

    public WeightTracker(String filename) {
        file = new File(filename);
        entries = new ArrayList<>();
        spentBlipcoins = 0;
        heightInInches = 0.0;
        goalWeight = 0.0;
        goalAchieved = false;
        loadEntries();
    }

    public void setHeight(double height) {
        this.heightInInches = height;
        saveEntries();
    }

    public double getHeight() {
        return heightInInches;
    }

    public void setGoalWeight(double goal) {
        this.goalWeight = goal;
        saveEntries();
    }

    public double getGoalWeight() {
        return goalWeight;
    }

    public void addEntry(WeightEntry entry, Scanner scanner) {
        WeightEntry existing = findEntryByDate(entry.getDate());
        if (existing != null) {
            System.out.printf("⚠️  An entry for %s already exists (%.1f lbs).%n", existing.getDate(), existing.getWeight());
            System.out.print("Would you like to update it instead? (y/n): ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("y")) {
                updateEntry(existing.getDate(), entry.getWeight());
                System.out.println("Entry updated.");
            } else {
                System.out.println("Entry not added.");
            }
            return;
        }

        entries.add(entry);
        checkGoalAchievement();
        saveEntries();

        if (heightInInches > 0) {
            double bmi = calculateBMI(entry.getWeight());
            System.out.printf("BMI for %.1f lbs at %.1f inches: %.1f (%s)%n",
                    entry.getWeight(), heightInInches, bmi, getBMICategory(bmi));
        } else {
            System.out.println("Set your height to enable BMI tracking.");
        }
    }


    public void deleteEntry(LocalDate date) {
        entries.removeIf(e -> e.getDate().equals(date));
        saveEntries();
    }

    public void updateEntry(LocalDate date, double newWeight) {
        for (WeightEntry e : entries) {
            if (e.getDate().equals(date)) {
                entries.set(entries.indexOf(e), new WeightEntry(date, newWeight));
                checkGoalAchievement();
                saveEntries();
                return;
            }
        }
        System.out.println("No entry found for that date.");
    }

    public WeightEntry findEntryByDate(LocalDate date) {
        for (WeightEntry e : entries) {
            if (e.getDate().equals(date)) {
                return e;
            }
        }
        return null;
    }


    public List<WeightEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    public double averageWeight() {
        return entries.stream().mapToDouble(WeightEntry::getWeight).average().orElse(0);
    }

    public double minWeight() {
        return entries.stream().mapToDouble(WeightEntry::getWeight).min().orElse(0);
    }

    public double maxWeight() {
        return entries.stream().mapToDouble(WeightEntry::getWeight).max().orElse(0);
    }

    public double totalWeightLost() {
        if (entries.isEmpty()) return 0.0;
        double start = entries.get(0).getWeight();
        double current = entries.get(entries.size() - 1).getWeight();
        return start - current;
    }

    public double currentWeight() {
        if (entries.isEmpty()) return 0.0;
        return entries.get(entries.size() - 1).getWeight();
    }

    public void checkGoalAchievement() {
        if (goalAchieved || goalWeight <= 0 || entries.isEmpty()) return;
        double current = currentWeight();
        if (current <= goalWeight) {
            goalAchieved = true;
            System.out.println("🎯 Congratulations! You reached your goal weight!");
            spentBlipcoins--; // award 1 bonus coin
            if (spentBlipcoins < 0) spentBlipcoins = 0; // just in case
        }
    }

    public double poundsToGoal() {
        if (goalWeight <= 0 || entries.isEmpty()) return 0.0;
        double current = currentWeight();
        double remaining = current - goalWeight;
        return Math.max(0.0, remaining);
    }

    public int getBlipcoins() {
        double lost = totalWeightLost();
        if (lost <= 0) return 0;
        int earned = (int)(lost / 3);
        if (goalAchieved) earned += 1; // bonus
        return earned - spentBlipcoins;
    }

    public void buySweetTreat() {
        if (getBlipcoins() > 0) {
            spentBlipcoins++;
            saveEntries();
            System.out.println("Yum! You bought a sweet treat 🎂 (-1 Blipcoin)");
        } else {
            System.out.println("Not enough Blipcoins to buy a sweet treat.");
        }
    }

    public double calculateBMI(double weightInPounds) {
        if (heightInInches <= 0) return 0.0;
        return (weightInPounds * 703) / (heightInInches * heightInInches);
    }

    public String getBMICategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 24.9) return "Normal weight";
        if (bmi < 29.9) return "Overweight";
        return "Obese";
    }

    private void loadEntries() {
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("SPENT:")) {
                    spentBlipcoins = Integer.parseInt(line.split(":")[1]);
                } else if (line.startsWith("HEIGHT:")) {
                    heightInInches = Double.parseDouble(line.split(":")[1]);
                } else if (line.startsWith("GOAL:")) {
                    goalWeight = Double.parseDouble(line.split(":")[1]);
                } else if (line.startsWith("GOAL_DONE:")) {
                    goalAchieved = Boolean.parseBoolean(line.split(":")[1]);
                } else {
                    entries.add(WeightEntry.fromString(line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveEntries() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("SPENT:" + spentBlipcoins);
            bw.newLine();
            bw.write("HEIGHT:" + heightInInches);
            bw.newLine();
            bw.write("GOAL:" + goalWeight);
            bw.newLine();
            bw.write("GOAL_DONE:" + goalAchieved);
            bw.newLine();
            for (WeightEntry entry : entries) {
                bw.write(entry.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getGoalProgressBar() {
        if (goalWeight <= 0 || entries.isEmpty()) return "No goal set.";
        double start = entries.get(0).getWeight();
        double current = currentWeight();
        double goal = goalWeight;

        double totalToLose = start - goal;
        if (totalToLose <= 0) return "Invalid goal weight.";

        double lostSoFar = start - current;
        double progress = (lostSoFar / totalToLose) * 100;
        progress = Math.min(100, Math.max(0, progress)); // clamp to 0–100

        int totalBars = 20; // length of the bar
        int filledBars = (int) (progress / (100.0 / totalBars));

        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < totalBars; i++) {
            if (i < filledBars) bar.append("#");
            else bar.append("-");
        }
        bar.append("] ");
        bar.append(String.format("%.0f%% complete", progress));
        return bar.toString();
    }

}
