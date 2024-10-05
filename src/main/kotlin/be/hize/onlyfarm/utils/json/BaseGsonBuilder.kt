package be.hize.onlyfarm.utils.json

import be.hize.onlyfarm.utils.KotlinTypeAdapterFactory
import com.google.gson.GsonBuilder
import io.github.notenoughupdates.moulconfig.observer.PropertyTypeAdapterFactory
import java.util.regex.Pattern

object BaseGsonBuilder {
    fun gson(): GsonBuilder = GsonBuilder().setPrettyPrinting()
        .excludeFieldsWithoutExposeAnnotation()
        .serializeSpecialFloatingPointValues()
        .registerTypeAdapterFactory(PropertyTypeAdapterFactory())
        .registerTypeAdapterFactory(KotlinTypeAdapterFactory())
        .registerTypeAdapter(Pattern::class.java, OFTypeAdapters.PATTERN.nullSafe())
        .enableComplexMapKeySerialization()

    fun lenientGson(): GsonBuilder = gson().registerTypeAdapterFactory(SkippingTypeAdapterFactory)
}
