package spring.twin.cluster;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import spring.twin.scan.LinkDetails;
import spring.twin.scan.LinkType;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DependencyReader}.
 */
class DependencyReaderTest {

    private final DependencyReader reader = new DependencyReader();
    private static final Path TEST_RESOURCES = Paths.get("src", "test", "resources", "spring", "twin", "cluster");

    @Test
    @DisplayName("read() with valid file returns dependency map with correct structure")
    void testRead_validFile_returnsDependencyMap() {
        Path validFile = TEST_RESOURCES.resolve("valid-dependencies.json");

        Map<String, Map<String, Set<LinkDetails>>> result = reader.read(validFile);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.containsKey("com.example.OrderService"));
        Map<String, Set<LinkDetails>> orderServiceDeps = result.get("com.example.OrderService");
        assertTrue(orderServiceDeps.containsKey("com.example.PaymentClient"));
    }

    @Test
    @DisplayName("read() with empty JSON file returns empty map")
    void testRead_emptyFile_returnsEmptyMap() {
        Path emptyFile = TEST_RESOURCES.resolve("empty-dependencies.json");

        Map<String, Map<String, Set<LinkDetails>>> result = reader.read(emptyFile);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("read() with non-existent file throws UncheckedIOException")
    void testRead_nonExistentFile_throwsUncheckedIOException() {
        Path nonExistentFile = Paths.get("non-existent-file.json");

        UncheckedIOException exception = assertThrows(
                UncheckedIOException.class,
                () -> reader.read(nonExistentFile)
        );

        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("read() with invalid JSON throws UncheckedIOException")
    void testRead_invalidJson_throwsUncheckedIOException() {
        Path invalidFile = TEST_RESOURCES.resolve("invalid-dependencies.json");

        UncheckedIOException exception = assertThrows(
                UncheckedIOException.class,
                () -> reader.read(invalidFile)
        );

        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("read() with file containing multiple classes returns all class keys")
    void testRead_fileWithMultipleClasses_returnsAllClasses() {
        Path multiClassFile = TEST_RESOURCES.resolve("valid-dependencies.json");

        Map<String, Map<String, Set<LinkDetails>>> result = reader.read(multiClassFile);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey("com.example.OrderService"));
        assertTrue(result.containsKey("com.example.repository.OrderRepository"));
    }

    @Test
    @DisplayName("read() with file containing LinkDetails deserializes type and details correctly")
    void testRead_fileWithLinkDetails_returnsCorrectLinkDetails() {
        Path validFile = TEST_RESOURCES.resolve("valid-dependencies.json");

        Map<String, Map<String, Set<LinkDetails>>> result = reader.read(validFile);

        assertNotNull(result);

        // Check OrderService -> PaymentClient (FIELD)
        Map<String, Set<LinkDetails>> orderServiceDeps = result.get("com.example.OrderService");
        Set<LinkDetails> paymentClientLinks = orderServiceDeps.get("com.example.PaymentClient");
        assertNotNull(paymentClientLinks);
        assertEquals(1, paymentClientLinks.size());
        LinkDetails paymentLink = paymentClientLinks.iterator().next();
        assertEquals(LinkType.FIELD, paymentLink.type());
        assertEquals("paymentClient", paymentLink.details());

        // Check OrderService -> OrderRepository (FIELD)
        Set<LinkDetails> orderRepoLinks = orderServiceDeps.get("com.example.repository.OrderRepository");
        assertNotNull(orderRepoLinks);
        assertEquals(1, orderRepoLinks.size());
        LinkDetails orderRepoLink = orderRepoLinks.iterator().next();
        assertEquals(LinkType.FIELD, orderRepoLink.type());
        assertEquals("orderRepository", orderRepoLink.details());

        // Check OrderRepository -> OrderModel (METHOD)
        Map<String, Set<LinkDetails>> orderRepoDeps = result.get("com.example.repository.OrderRepository");
        Set<LinkDetails> orderModelLinks = orderRepoDeps.get("com.example.model.OrderModel");
        assertNotNull(orderModelLinks);
        assertEquals(1, orderModelLinks.size());
        LinkDetails orderModelLink = orderModelLinks.iterator().next();
        assertEquals(LinkType.METHOD, orderModelLink.type());
        assertEquals("add(Lcom/example/model/OrderModel;)", orderModelLink.details());
    }
}