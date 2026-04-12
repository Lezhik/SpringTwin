package spring.twin.scan;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LinkDetails} record.
 *
 * <p>Tests cover:
 * <ul>
 *   <li>Factory methods ({@code of})</li>
 *   <li>Equality and hash code contracts</li>
 *   <li>Set behavior (duplicates prevention)</li>
 *   <li>JSON serialization and deserialization</li>
 * </ul>
 */
class LinkDetailsTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test that LinkDetails.of(LinkType, String) creates an object with correct type and details.
     */
    @Test
    void testOfTypeAndDetails_createsLinkDetails() {
        LinkDetails linkDetails = LinkDetails.of(LinkType.FIELD, "orderRepository");

        assertEquals(LinkType.FIELD, linkDetails.type());
        assertEquals("orderRepository", linkDetails.details());
    }

    /**
     * Test that LinkDetails.of(LinkType) creates an object with empty details.
     */
    @Test
    void testOfTypeOnly_createsLinkDetailsWithEmptyDetails() {
        LinkDetails linkDetails = LinkDetails.of(LinkType.SUPERCLASS);

        assertEquals(LinkType.SUPERCLASS, linkDetails.type());
        assertEquals("", linkDetails.details());
    }

    /**
     * Test that two LinkDetails with the same type and details are equal.
     */
    @Test
    void testEquals_sameTypeAndDetails_returnsTrue() {
        LinkDetails linkDetails1 = LinkDetails.of(LinkType.FIELD, "orderRepository");
        LinkDetails linkDetails2 = LinkDetails.of(LinkType.FIELD, "orderRepository");

        assertEquals(linkDetails1, linkDetails2);
    }

    /**
     * Test that two LinkDetails with different types are not equal.
     */
    @Test
    void testEquals_differentType_returnsFalse() {
        LinkDetails linkDetails1 = LinkDetails.of(LinkType.FIELD, "orderRepository");
        LinkDetails linkDetails2 = LinkDetails.of(LinkType.METHOD, "orderRepository");

        assertNotEquals(linkDetails1, linkDetails2);
    }

    /**
     * Test that two LinkDetails with different details are not equal.
     */
    @Test
    void testEquals_differentDetails_returnsFalse() {
        LinkDetails linkDetails1 = LinkDetails.of(LinkType.FIELD, "orderRepository");
        LinkDetails linkDetails2 = LinkDetails.of(LinkType.FIELD, "paymentClient");

        assertNotEquals(linkDetails1, linkDetails2);
    }

    /**
     * Test that two equal LinkDetails have the same hash code.
     */
    @Test
    void testHashCode_sameTypeAndDetails_sameHashCode() {
        LinkDetails linkDetails1 = LinkDetails.of(LinkType.FIELD, "orderRepository");
        LinkDetails linkDetails2 = LinkDetails.of(LinkType.FIELD, "orderRepository");

        assertEquals(linkDetails1.hashCode(), linkDetails2.hashCode());
    }

    /**
     * Test that adding duplicate LinkDetails to a Set does not create duplicates.
     */
    @Test
    void testSetDuplicate_sameTypeAndDetails_notAddedTwice() {
        Set<LinkDetails> linkDetailsSet = new HashSet<>();

        LinkDetails linkDetails1 = LinkDetails.of(LinkType.FIELD, "orderRepository");
        LinkDetails linkDetails2 = LinkDetails.of(LinkType.FIELD, "orderRepository");

        linkDetailsSet.add(linkDetails1);
        linkDetailsSet.add(linkDetails2);

        assertEquals(1, linkDetailsSet.size());
    }

    /**
     * Test Jackson serialization produces correct JSON format.
     */
    @Test
    void testSerialization_toJson_correctFormat() throws Exception {
        LinkDetails linkDetails = LinkDetails.of(LinkType.FIELD, "orderRepository");

        String json = objectMapper.writeValueAsString(linkDetails);

        assertEquals("{\"type\":\"FIELD\",\"details\":\"orderRepository\"}", json);
    }

    /**
     * Test Jackson serialization with empty details produces correct JSON format.
     */
    @Test
    void testSerialization_emptyDetails_correctFormat() throws Exception {
        LinkDetails linkDetails = LinkDetails.of(LinkType.SUPERCLASS);

        String json = objectMapper.writeValueAsString(linkDetails);

        assertEquals("{\"type\":\"SUPERCLASS\",\"details\":\"\"}", json);
    }

    /**
     * Test Jackson deserialization produces correct LinkDetails object.
     */
    @Test
    void testDeserialization_fromJson_correctObject() throws Exception {
        String json = "{\"type\":\"METHOD\",\"details\":\"add(OrderModel)\"}";

        LinkDetails linkDetails = objectMapper.readValue(json, LinkDetails.class);

        assertEquals(LinkType.METHOD, linkDetails.type());
        assertEquals("add(OrderModel)", linkDetails.details());
    }
}