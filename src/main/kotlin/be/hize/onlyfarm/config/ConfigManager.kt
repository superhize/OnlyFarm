package be.hize.onlyfarm.config

import be.hize.onlyfarm.OnlyFarmMod
import be.hize.onlyfarm.utils.KotlinTypeAdapterFactory
import be.hize.onlyfarm.utils.Logger
import be.hize.onlyfarm.utils.SimpleTimeMark
import be.hize.onlyfarm.utils.SimpleTimeMark.Companion.asTimeMark
import be.hize.onlyfarm.utils.Utils.shutdownMinecraft
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import io.github.moulberry.moulconfig.observer.PropertyTypeAdapterFactory
import io.github.moulberry.moulconfig.processor.BuiltinMoulConfigGuis
import io.github.moulberry.moulconfig.processor.ConfigProcessorDriver
import io.github.moulberry.moulconfig.processor.MoulConfigProcessor
import net.minecraft.item.ItemStack
import net.minecraft.launchwrapper.Launch
import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*
import kotlin.concurrent.fixedRateTimer

class ConfigManager {
    companion object {
        val gson = GsonBuilder().setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .serializeSpecialFloatingPointValues()
            .registerTypeAdapterFactory(PropertyTypeAdapterFactory())
            .registerTypeAdapterFactory(KotlinTypeAdapterFactory())
            .registerTypeAdapter(UUID::class.java, object : TypeAdapter<UUID>() {
                override fun write(out: JsonWriter, value: UUID) {
                    out.value(value.toString())
                }

                override fun read(reader: JsonReader): UUID {
                    return UUID.fromString(reader.nextString())
                }
            }.nullSafe())
            .registerTypeAdapter(SimpleTimeMark::class.java, object : TypeAdapter<SimpleTimeMark>() {
                override fun write(out: JsonWriter, value: SimpleTimeMark) {
                    out.value(value.toMillis())
                }

                override fun read(reader: JsonReader): SimpleTimeMark {
                    return reader.nextString().toLong().asTimeMark()
                }
            }.nullSafe())
            .enableComplexMapKeySerialization()
            .create()

        var configDirectory = File("config/onlyfarm")
    }

    val features get() = jsonHolder[ConfigFileType.FEATURES] as Features
    private val logger = Logger("config_manager")

    private val jsonHolder = mutableMapOf<ConfigFileType, Any>()

    lateinit var processor: MoulConfigProcessor<Features>
    private var disableSaving = false

    fun firstLoad() {
        if (jsonHolder.isNotEmpty()) {
            logger.log("Loading config despite config being already loaded?")
        }
        configDirectory.mkdirs()


        for (fileType in ConfigFileType.entries) {
            jsonHolder[fileType] = firstLoadFile(fileType.file, fileType, fileType.clazz.newInstance())
        }

        fixedRateTimer(name = "of-config-auto-save", period = 60_000L, initialDelay = 60_000L) {
            saveConfig(ConfigFileType.FEATURES, "auto-save-60s")
        }

        val features = OnlyFarmMod.feature
        processor = MoulConfigProcessor(OnlyFarmMod.feature)
        BuiltinMoulConfigGuis.addProcessors(processor)
       // UpdateManager.injectConfigProcessor(processor)
        val configProcessorDriver = ConfigProcessorDriver(processor)
        configProcessorDriver.processConfig(features)
    }


    private fun firstLoadFile(file: File?, fileType: ConfigFileType, defaultValue: Any): Any {
        val fileName = fileType.fileName
        logger.log("Trying to load $fileName from $file")
        var output: Any = defaultValue

        if (file!!.exists()) {
            try {
                val inputStreamReader = InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8)
                val bufferedReader = BufferedReader(inputStreamReader)

                logger.log("load-$fileName-now")

                output = if (fileType==ConfigFileType.FEATURES) {
                    val jsonObject = gson.fromJson(bufferedReader.readText(), JsonObject::class.java)
                    val run = { gson.fromJson(jsonObject, defaultValue.javaClass) }
                    if (Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean) {
                        try {
                            run()
                        } catch (e: Throwable) {
                            e.printStackTrace()
                            shutdownMinecraft("Config is corrupt inside developement enviroment.")
                        }
                    } else {
                        run()
                    }
                } else {
                    gson.fromJson(bufferedReader.readText(), defaultValue.javaClass)
                }

                logger.log("Loaded $fileName from file")
            } catch (error: Exception) {
                error.printStackTrace()
                val backupFile = file.resolveSibling("$fileName-${System.currentTimeMillis()}-backup.json")
                logger.log("Exception while reading $file. Will load blank $fileName and save backup to $backupFile")
                logger.log("Exception was $error")
                try {
                    file.copyTo(backupFile)
                } catch (e: Exception) {
                    logger.log("Could not create backup for $fileName file")
                    e.printStackTrace()
                }
            }
        }

        if (output==defaultValue) {
            logger.log("Setting $fileName to be blank as it did not exist. It will be saved once something is written to it")
        }

        return output
    }

    fun saveConfig(fileType: ConfigFileType, reason: String) {
        val json = jsonHolder[fileType] ?: error("Could not find json object for $fileType")
        saveFile(fileType.file, fileType.fileName, json, reason)
    }

    private fun saveFile(file: File?, fileName: String, data: Any, reason: String) {
        if (disableSaving) return
        logger.log("saveConfig: $reason")
        if (file==null) throw Error("Can not save $fileName, ${fileName}File is null!")
        try {
            logger.log("Saving $fileName file")
            file.parentFile.mkdirs()
            val unit = file.parentFile.resolve("$fileName.json.write")
            unit.createNewFile()
            BufferedWriter(OutputStreamWriter(FileOutputStream(unit), StandardCharsets.UTF_8)).use { writer ->
                writer.write(gson.toJson(data))
            }
            // Perform move — which is atomic, unlike writing — after writing is done.
            Files.move(
                unit.toPath(),
                file.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            )
        } catch (e: IOException) {
            logger.log("Could not save $fileName file to $file")
            e.printStackTrace()
        }
    }

    fun disableSaving() {
        disableSaving = true
    }
}

enum class ConfigFileType(val fileName: String, val clazz: Class<*>) {
    FEATURES("config", Features::class.java),
    ;

    val file by lazy { File(ConfigManager.configDirectory, "$fileName.json") }
}


