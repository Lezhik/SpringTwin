package spring.twin.cluster;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ClusterParams}.
 */
class ClusterParamsTest {

    private static final Path DEPS_FILE = Paths.get("dependencies.json");
    private static final Path OUTPUT_FILE = Paths.get("clusters.json");

    @Test
    @DisplayName("of() with empty string resolution creates params with default resolution 1.5")
    void testOf_createsParamsWithDefaultResolution() {
        ClusterParams params = ClusterParams.of(DEPS_FILE, OUTPUT_FILE, "");

        assertEquals(DEPS_FILE, params.depsFile());
        assertEquals(OUTPUT_FILE, params.outputFile());
        assertEquals(1.5, params.resolution());
    }

    @Test
    @DisplayName("of() with valid resolution string creates params with custom resolution")
    void testOf_createsParamsWithCustomResolution() {
        ClusterParams params = ClusterParams.of(DEPS_FILE, OUTPUT_FILE, "2.0");

        assertEquals(DEPS_FILE, params.depsFile());
        assertEquals(OUTPUT_FILE, params.outputFile());
        assertEquals(2.0, params.resolution());
    }

    @Test
    @DisplayName("of() with null resolutionRaw uses default value 1.5")
    void testOf_nullResolutionRaw_usesDefault() {
        ClusterParams params = ClusterParams.of(DEPS_FILE, OUTPUT_FILE, null);

        assertEquals(DEPS_FILE, params.depsFile());
        assertEquals(OUTPUT_FILE, params.outputFile());
        assertEquals(1.5, params.resolution());
    }

    @Test
    @DisplayName("of() with empty resolutionRaw uses default value 1.5")
    void testOf_emptyResolutionRaw_usesDefault() {
        ClusterParams params = ClusterParams.of(DEPS_FILE, OUTPUT_FILE, "");

        assertEquals(DEPS_FILE, params.depsFile());
        assertEquals(OUTPUT_FILE, params.outputFile());
        assertEquals(1.5, params.resolution());
    }

    @Test
    @DisplayName("of() with resolution below min (0.4) throws IllegalArgumentException")
    void testOf_resolutionBelowMin_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ClusterParams.of(DEPS_FILE, OUTPUT_FILE, "0.4")
        );

        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("of() with resolution above max (5.1) throws IllegalArgumentException")
    void testOf_resolutionAboveMax_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ClusterParams.of(DEPS_FILE, OUTPUT_FILE, "5.1")
        );

        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("of() with resolution at min boundary (0.5) is accepted")
    void testOf_resolutionMinBoundary_accepted() {
        ClusterParams params = ClusterParams.of(DEPS_FILE, OUTPUT_FILE, "0.5");

        assertEquals(DEPS_FILE, params.depsFile());
        assertEquals(OUTPUT_FILE, params.outputFile());
        assertEquals(0.5, params.resolution());
    }

    @Test
    @DisplayName("of() with resolution at max boundary (5.0) is accepted")
    void testOf_resolutionMaxBoundary_accepted() {
        ClusterParams params = ClusterParams.of(DEPS_FILE, OUTPUT_FILE, "5.0");

        assertEquals(DEPS_FILE, params.depsFile());
        assertEquals(OUTPUT_FILE, params.outputFile());
        assertEquals(5.0, params.resolution());
    }

    @Test
    @DisplayName("constructor stores all fields correctly")
    void testConstructor_storesAllFields() {
        Path customDeps = Paths.get("custom", "deps.json");
        Path customOutput = Paths.get("custom", "output.json");

        ClusterParams params = new ClusterParams(customDeps, customOutput, 3.0);

        assertEquals(customDeps, params.depsFile());
        assertEquals(customOutput, params.outputFile());
        assertEquals(3.0, params.resolution());
    }

    @Test
    @DisplayName("of() with invalid number format throws NumberFormatException")
    void testOf_invalidNumberFormat_throwsException() {
        NumberFormatException exception = assertThrows(
                NumberFormatException.class,
                () -> ClusterParams.of(DEPS_FILE, OUTPUT_FILE, "not-a-number")
        );

        assertNotNull(exception.getMessage());
    }
}