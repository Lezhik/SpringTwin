package spring.twin.scan;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;

/**
 * Model for storing detailed information about a link between classes.
 *
 * <p>Contains the type of link ({@link LinkType}) and additional description
 * ({@code details}) that depends on the link type. Used to represent
 * dependencies in the dependency graph.
 *
 * <p>For example:
 * <pre>
 * LinkDetails.of(LinkType.FIELD, "orderRepository")
 * // serializes to: {"type": "FIELD", "details": "orderRepository"}
 * </pre>
 *
 * @param type    the type of link (superclass, field, method, annotation, etc.)
 * @param details additional information about the link, depends on type:
 *                <ul>
 *                <li>SUPERCLASS — empty string</li>
 *                <li>INTERFACE — empty string</li>
 *                <li>FIELD — field name</li>
 *                <li>STATIC_BLOCK — empty string</li>
 *                <li>METHOD — method signature</li>
 *                <li>CLASS_ANNOTATION — empty string</li>
 *                <li>FIELD_ANNOTATION — field name</li>
 *                <li>METHOD_ANNOTATION — method signature</li>
 *                <li>METHOD_ARG_ANNOTATION — method signature</li>
 *                </ul>
 */
public record LinkDetails(
        @JsonProperty("type") @JsonSerialize(using = LinkTypeSerializer.class) @JsonDeserialize(using = LinkTypeDeserializer.class) LinkType type,
        @JsonProperty("details") String details) {

    /**
     * Creates a new LinkDetails instance with the specified type and details.
     *
     * <p>This factory method is used when additional information about the link
     * is available (e.g., field name for FIELD type, method signature for METHOD type).
     *
     * <p>If details is null, it will be replaced with an empty string.
     *
     * <p>The returned instance is suitable for use in collections such as {@code Set<LinkDetails>}.
     *
     * @param type    the type of link, must not be null
     * @param details additional information about the link, may be null (will be converted to empty string)
     * @return a new LinkDetails instance
     */
    public static LinkDetails of(LinkType type, String details) {
        return new LinkDetails(type, details == null ? "" : details);
    }

    /**
     * Creates a new LinkDetails instance with the specified type and empty details.
     *
     * <p>This factory method is a convenience for link types that do not require
     * additional information (e.g., SUPERCLASS, INTERFACE, CLASS_ANNOTATION).
     *
     * <p>The returned instance is suitable for use in collections such as {@code Set<LinkDetails>}.
     *
     * @param type the type of link, must not be null
     * @return a new LinkDetails instance with empty details
     */
    public static LinkDetails of(LinkType type) {
        return new LinkDetails(type, "");
    }

    /**
     * Custom Jackson serializer for LinkType.
     *
     * <p>Serializes LinkType using its JSON name via {@link LinkType#getJsonName()}.
     */
    public static class LinkTypeSerializer extends JsonSerializer<LinkType> {
        @Override
        public void serialize(LinkType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.getJsonName());
        }
    }

    /**
     * Custom Jackson deserializer for LinkType.
     *
     * <p>Deserializes LinkType from its JSON name via {@link LinkType#valueOf(String)}.
     */
    public static class LinkTypeDeserializer extends JsonDeserializer<LinkType> {
        @Override
        public LinkType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return LinkType.valueOf(p.getValueAsString());
        }
    }
}