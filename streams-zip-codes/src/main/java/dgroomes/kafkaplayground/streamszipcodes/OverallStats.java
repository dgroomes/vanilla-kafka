package dgroomes.kafkaplayground.streamszipcodes;

/**
 * Statistics the overall level.
 */
public record OverallStats(
        // The number of ZIP areas overall
        int zipAreas,

        // Total overall population
        int totalPop,

        // The average population of ZIP areas overall
        int avgPop) {
}
