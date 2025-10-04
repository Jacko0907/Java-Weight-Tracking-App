import java.time.LocalDate;

public class WeightEntry {
    private LocalDate date;
    private double weight;

    public WeightEntry(LocalDate date, double weight) {
        this.date = date;
        this.weight = weight;
    }

    public LocalDate getDate() { return date; }
    public double getWeight() { return weight; }

    @Override
    public String toString() {
        return date + "," + weight;
    }

    public static WeightEntry fromString(String line) {
        String[] parts = line.split(",");
        LocalDate date = LocalDate.parse(parts[0]);
        double weight = Double.parseDouble(parts[1]);
        return new WeightEntry(date, weight);
    }
}
