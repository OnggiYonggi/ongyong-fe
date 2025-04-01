package com.bravepeople.onggiyonggi.extension

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object LocalDateTimeFromNaverSerializer : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val text = decoder.decodeString()
        return ZonedDateTime.parse(text, formatter).toLocalDateTime()
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val formatted = value.format(formatter)
        encoder.encodeString(formatted)
    }
}