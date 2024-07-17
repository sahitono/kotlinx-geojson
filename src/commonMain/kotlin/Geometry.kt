import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive



@Serializable(with=GeometrySerializer::class)
sealed class Geometry {
    abstract val type: GeometryType
    abstract val bbox: BBox?
    abstract val coordinates: Any

    @Serializable
    data class Point(
        override val type: GeometryType = GeometryType.Point,
        override val bbox: List<Double>? = null,
        override val coordinates: Position,
    ) : Geometry()

    @Serializable
    data class MultiPoint(
        override val type: GeometryType = GeometryType.MultiPoint,
        override val bbox: List<Double>? = null,
        override val coordinates: List<Position>,
    ) : Geometry()

    @Serializable
    data class LineString(
        override val type: GeometryType = GeometryType.LineString,
        override val bbox: List<Double>? = null,
        override val coordinates: LinearRing,
    ) : Geometry()

    @Serializable
    data class MultiLineString(
        override val type: GeometryType = GeometryType.MultiLineString,
        override val bbox: List<Double>? = null,
        override val coordinates: List<LinearRing>,
    ) : Geometry()


    @Serializable
    data class Polygon(
        override val type: GeometryType = GeometryType.Polygon,
        override val bbox: List<Double>? = null,
        override val coordinates: List<LinearRing>,
    ) : Geometry()

    @Serializable
    data class MultiPolygon(
        override val type: GeometryType = GeometryType.MultiPolygon,
        override val bbox: List<Double>? = null,
        override val coordinates: List<List<LinearRing>>,
    ) : Geometry()
}

enum class GeometryType {
    Point,
    Polygon,
    LineString,
    MultiPoint,
    MultiLineString,
    MultiPolygon,
}

typealias BBox = List<Double>
typealias Position = List<Double>
typealias LinearRing = List<Position>

object GeometrySerializer : JsonContentPolymorphicSerializer<Geometry>(Geometry::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Geometry> {
        val geomType = element.jsonObject["type"]?.jsonPrimitive?.content?.let {
            GeometryType.valueOf(it.trim())
        }

        return when (geomType) {
            GeometryType.Point -> Geometry.Point.serializer()
            GeometryType.MultiPoint -> Geometry.MultiPoint.serializer()
            GeometryType.LineString -> Geometry.LineString.serializer()
            GeometryType.MultiLineString -> Geometry.MultiLineString.serializer()
            GeometryType.Polygon -> Geometry.Polygon.serializer()
            GeometryType.MultiPolygon -> Geometry.MultiPolygon.serializer()
            else -> throw SerializationException("unsupported geometry")
        }
    }
}
