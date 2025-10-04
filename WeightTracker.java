import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WeightTracker {
    private File file;
    private List<WeightEntry> entries;

    public WeightTracker(String filename) {
        file = new File(filename);
        entries = new ArrayList<>();
        loadEntries();
    }

    public void addEntry(WeightEntry entry) {
        entries.add(entry);
        saveEntries();
    }

    public void deleteEntry(LocalDate date) {
        entries.removeIf(e -> e.getDate().equals(date));
        saveEntries();
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

    public void updateEntry(LocalDate date, double newWeight) {
        for (WeightEntry e : entries) {
            if (e.getDate().equals(date)) {
                entries.set(entries.indexOf(e), new WeightEntry(date, newWeight));
                saveEntries();
                return;
            }
        }
        System.out.println("No entry found for that date.");
    }


    // ✅ New method for total weight lost from first to last entry
    public double totalWeightLost() {
        if (entries.isEmpty()) return 0.0;
        double start = entries.get(0).getWeight();
        double current = entries.get(entries.size() - 1).getWeight();
        return start - current; // if negative = gained
    }

    private void loadEntries() {
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                entries.add(WeightEntry.fromString(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveEntries() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (WeightEntry entry : entries) {
                bw.write(entry.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
