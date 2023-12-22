package com.zhangke.framework.serialize

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Date

class DateSerializer : KSerializer<Date> {

    override fun deserialize(decoder: Decoder): Date {
        return Date(decoder.decodeLong())
    }

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor
        get() = DateSerialDescriptor()

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeLong(value.time)
    }
}

@ExperimentalSerializationApi
class DateSerialDescriptor : SerialDescriptor {

    override val elementsCount: Int = 1

    override val kind: SerialKind = StructureKind.OBJECT

    override val serialName = "com.zhangke.framework.serialize.DateAsLongSerializer"

    override fun getElementAnnotations(index: Int): List<Annotation> = emptyList()

    override fun getElementDescriptor(index: Int): SerialDescriptor = Long.serializer().descriptor

    override fun getElementIndex(name: String): Int = name.toInt()

    override fun getElementName(index: Int): String = index.toString()

    override fun isElementOptional(index: Int): Boolean = false
}
