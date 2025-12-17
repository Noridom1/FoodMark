package com.example.foodmark.geo.domain.serializer

import com.example.foodmark.geo.domain.model.GeoPoint
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.MDC.put
import java.nio.ByteBuffer
import java.nio.ByteOrder

object GeoPointSerializer : KSerializer<GeoPoint> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("GeoPoint")

    override fun serialize(encoder: Encoder, value: GeoPoint) {
        // For writing to database, use GeoJSON format
        val geoJson = buildJsonObject {
            put("type", "Point")
            put("coordinates", buildJsonArray {
                add(JsonPrimitive(value.lng))
                add(JsonPrimitive(value.lat))
            })
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), geoJson)
    }

    override fun deserialize(decoder: Decoder): GeoPoint {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())

        return when {
            // Handle WKB format (hex string)
            jsonElement is JsonPrimitive && jsonElement.isString -> {
                val wkbHex = jsonElement.content
                if (wkbHex.startsWith("0101000020E6100000")) {
                    parseWKBHex(jsonElement.content)
                } else {
                    // Try WKT format
                    parseWKTPoint(wkbHex)
                }
            }

            // Handle GeoJSON format: {"type":"Point","coordinates":[lng,lat]}
            jsonElement is JsonObject && jsonElement.containsKey("coordinates") -> {
                val coordinates = jsonElement["coordinates"]?.jsonArray
                    ?: throw SerializationException("Missing coordinates in GeoJSON")

                if (coordinates.size != 2) {
                    throw SerializationException("Invalid coordinates array size")
                }

                val lng = coordinates[0].jsonPrimitive.double
                val lat = coordinates[1].jsonPrimitive.double
                GeoPoint(lng, lat)
            }

            // Handle simple object format: {"lng": number, "lat": number}
            jsonElement is JsonObject && jsonElement.containsKey("lng") && jsonElement.containsKey(
                "lat"
            ) -> {
                val lng = jsonElement["lng"]?.jsonPrimitive?.double
                    ?: throw SerializationException("Missing lng")
                val lat = jsonElement["lat"]?.jsonPrimitive?.double
                    ?: throw SerializationException("Missing lat")
                GeoPoint(lng, lat)
            }

            // Handle WKT format string: "POINT(lng lat)"
            jsonElement is JsonPrimitive && jsonElement.isString -> {
                val wkt = jsonElement.content
                parseWKTPoint(wkt)
            }

            else -> throw SerializationException("Unknown GeoPoint format: $jsonElement")
        }
    }

    private fun parseWKTPoint(wkt: String): GeoPoint {
        // Parse "POINT(lng lat)" format
        val regex =
            Regex("""POINT\s*\(\s*(-?\d+\.?\d*)\s+(-?\d+\.?\d*)\s*\)""", RegexOption.IGNORE_CASE)
        val match = regex.find(wkt)
            ?: throw SerializationException("Invalid WKT Point format: $wkt")

        val lng = match.groupValues[1].toDouble()
        val lat = match.groupValues[2].toDouble()
        return GeoPoint(lng, lat)
    }

    private fun parseWKBHex(wkbHex: String): GeoPoint {
        val bytes = wkbHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

        val byteOrder = buffer.get().toInt() // 1 = little endian
        if (byteOrder != 1) {
            buffer.order(ByteOrder.BIG_ENDIAN)
        }

        val type = buffer.int // geometry type + flags
        val hasSRID = (type and 0x20000000) != 0 // check if SRID flag is set

        if (hasSRID) {
            buffer.int // skip SRID (4326)
        }

        val lng = buffer.double
        val lat = buffer.double
        return GeoPoint(lng, lat)
    }
}
