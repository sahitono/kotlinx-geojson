import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias MapAny = Map<String, Any?>

interface IFeature<Prop : MapAny> {
    val type: String
    val geometry: Geometry
    val properties: Prop
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Feature(
    override val type: String = "Feature",
    override val geometry: Geometry,
    override val properties: Map<String, @Serializable(with = DynamicLookupSerializer::class) Any?>
) : IFeature<MapAny>


@ExperimentalSerializationApi
class DynamicLookupSerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = ContextualSerializer(Any::class, null, emptyArray()).descriptor

    @OptIn(InternalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Any) {
        if (value is ArrayList<*>) {
            encoder.encodeSerializableValue(ListSerializer(DynamicLookupSerializer()), value)
            return
        }
        val actualSerializer = encoder.serializersModule.getContextual(value::class) ?: value::class.serializer()
        encoder.encodeSerializableValue(actualSerializer as KSerializer<Any>, value)
    }

    override fun deserialize(decoder: Decoder): Any {
        error("Unsupported")
    }
}
