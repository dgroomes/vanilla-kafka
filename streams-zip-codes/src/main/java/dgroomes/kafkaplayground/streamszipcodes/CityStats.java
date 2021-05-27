package dgroomes.kafkaplayground.streamszipcodes;

/**
 * Statistics at a city-state level.
 * The city and state fields are purposely not included in this record because they are meant to be specified in the
 * CityState class.
 */
public record CityStats(
        int numberZipAreas,
        int averageZipAreaPopulation) {
}
