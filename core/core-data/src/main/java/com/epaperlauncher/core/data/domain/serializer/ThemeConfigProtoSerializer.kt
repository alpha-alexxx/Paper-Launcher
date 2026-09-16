package com.epaperlauncher.core.data.domain.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.epaperlauncher.core.data.domain.model.ThemeConfig
import kotlinx.serialization.SerializationException
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream

/**
 * ProtoBuf serializer for ThemeConfig used by DataStore.
 */
object ThemeConfigProtoSerializer : Serializer<ThemeConfig> {
    override val defaultValue: ThemeConfig = ThemeConfig()

    override suspend fun readFrom(input: InputStream): ThemeConfig {
        return try {
            ProtoBuf.decodeFromByteArray(
                input.readBytes()
            )
        } catch (e: SerializationException) {
            throw CorruptionException("Cannot read ThemeConfig proto", e)
        }
    }

    override suspend fun writeTo(t: ThemeConfig, output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(t))
    }
}
