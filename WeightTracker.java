import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WeightTracker {
    private File file;
    private List<WeightEntry> entries;
    private int spentBlipcoins; // new field

    public WeightTracker(String filename) {
        file = new File(filename);
        entries = new ArrayList<>();
        spentBlipcoins = 0;
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

    public int getBlipcoins() {
        double lost = totalWeightLost();
        if (lost <= 0) return 0;
        int earned = (int)(lost / 3);
        return earned - spentBlipcoins; // subtract spent coins
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

    private void loadEntries() {
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine && line.startsWith("SPENT:")) {
                    spentBlipcoins = Integer.parseInt(line.split(":")[1]);
                    firstLine = false;
                    continue;
                }
                entries.add(WeightEntry.fromString(line));
                firstLine = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveEntries() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("SPENT:" + spentBlipcoins);
            bw.newLine();
            for (WeightEntry entry : entries) {
                bw.write(entry.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
