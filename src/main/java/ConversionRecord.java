import java.time.OffsetDateTime;

public record ConversionRecord(
        double value,
        String fromUnit,
        String toUnit,
        double result,
        String language,
        OffsetDateTime timestamp) {
}