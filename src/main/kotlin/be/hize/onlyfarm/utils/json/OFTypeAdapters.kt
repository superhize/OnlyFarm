package be.hize.onlyfarm.utils.json

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.util.regex.Pattern

object OFTypeAdapters {
    val PATTERN: TypeAdapter<Pattern> = object : TypeAdapter<Pattern>() {
        override fun write(out: JsonWriter, value: Pattern) {
            out.value(value.toString())
        }

        override fun read(reader: JsonReader): Pattern {
            return reader.nextString().toPattern()
        }
    }
}
