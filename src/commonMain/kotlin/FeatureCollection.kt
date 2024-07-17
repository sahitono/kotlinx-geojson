import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class FeatureCollection(
    val type: String = "FeatureCollection",
    val features: List<IFeature<Map<String, @Serializable(with = DynamicLookupSerializer::class) Any?>>>
)
