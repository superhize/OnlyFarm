package be.hize.onlyfarm.config

import be.hize.onlyfarm.OnlyFarmMod
import be.hize.onlyfarm.utils.DelayedRun
import be.hize.onlyfarm.utils.Logger
import be.hize.onlyfarm.utils.Utils
import be.hize.onlyfarm.utils.Utils.shutdownMinecraft
import be.hize.onlyfarm.utils.json.BaseGsonBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.github.notenoughupdates.moulconfig.processor.BuiltinMoulConfigGuis
import io.github.notenoughupdates.moulconfig.processor.ConfigProcessorDriver
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.EnumMap
import kotlin.concurrent.fixedRateTimer
import kotlin.reflect.KMutableProperty0

class ConfigManager {
    companion object {
        val gson: Gson = BaseGsonBuilder.gson()
            //  .registerIfBeta(FeatureTogglesByDefaultAdapter)
            .create()

        var configDirectory = File("config/onlyfarm")
    }

    val features get() = jsonHolder[ConfigFileType.FEATURES] as Features
    val notifications get() = jsonHolder[ConfigFileType.NOTIFICATIONS] as Notifications

    private val logger = Logger("config_manager")

    private val jsonHolder: Map<ConfigFileType, Any> = EnumMap(ConfigFileType::class.java)

    lateinit var processor: MoulConfigProcessor<Features>
    private var disableSaving = false

    private fun setConfigHolder(type: ConfigFileType, value: Any) {
        require(value.javaClass == type.clazz)
        @Suppress("UNCHECKED_CAST")
        (type.property as KMutableProperty0<Any>).set(value)
        (jsonHolder as MutableMap<ConfigFileType, Any>)[type] = value
    }

    fun firstLoad() {
        if (jsonHolder.isNotEmpty()) {
            logger.log("Loading config despite config being already loaded?")
        }
        configDirectory.mkdirs()


        for (fileType in ConfigFileType.entries) {
            setConfigHolder(fileType, firstLoadFile(fileType.file, fileType, fileType.clazz.newInstance()))
        }

        fixedRateTimer(name = "nes-config-auto-save", period = 60_000L, initialDelay = 60_000L) {
            for (file in ConfigFileType.entries) {
                saveConfig(file, "auto-save-60s")
            }
        }

        val features = OnlyFarmMod.feature
        processor = MoulConfigProcessor(features)
        BuiltinMoulConfigGuis.addProcessors(processor)
        val configProcessorDriver = ConfigProcessorDriver(processor)
        try {
            configProcessorDriver.processConfig(features)
        } catch (ex: Exception) {
            println("Exc: ${ex.localizedMessage}")
        }
    }


    private fun firstLoadFile(file: File?, fileType: ConfigFileType, defaultValue: Any): Any {
        val fileName = fileType.fileName
        logger.log("Trying to load $fileName from $file")
        var output: Any = defaultValue

        if (file!!.exists()) {
            try {
                val inputStreamReader = InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8)
                val bufferedReader = BufferedReader(inputStreamReader)
                val lenientGson = BaseGsonBuilder.lenientGson().create()

                logger.log("load-$fileName-now")

                output = if (fileType == ConfigFileType.FEATURES) {
                    val jsonObject = gson.fromJson(bufferedReader.readText(), JsonObject::class.java)
                    val run = { lenientGson.fromJson(jsonObject, defaultValue.javaClass) }
                    if (Utils.isInDevEnviromen()) {
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
                    lenientGson.fromJson(bufferedReader.readText(), defaultValue.javaClass)
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

        if (output == defaultValue) {
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
        if (file == null) throw Error("Can not save $fileName, ${fileName}File is null!")
        try {
            logger.log("Saving $fileName file")
            file.parentFile.mkdirs()
            val unit = file.parentFile.resolve("$fileName.json.write")
            unit.createNewFile()
            BufferedWriter(OutputStreamWriter(FileOutputStream(unit), StandardCharsets.UTF_8)).use { writer ->
                writer.write(gson.toJson(data))
            }
            // Perform move — which is atomic, unlike writing — after writing is done.
            move(unit, file, reason)
        } catch (e: IOException) {
            logger.log("Could not save $fileName file to $file")
            e.printStackTrace()
        }
    }

    private fun move(unit: File, file: File, reason: String, loop: Int = 0) {
        try {
            Files.move(
                unit.toPath(),
                file.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE,
            )
        } catch (e: AccessDeniedException) {
            if (loop == 5) {
                return
            }
            DelayedRun.runNextTick {
                move(unit, file, reason, loop + 1)
            }
        }
    }

    fun disableSaving() {
        disableSaving = true
    }
}

enum class ConfigFileType(val fileName: String, val clazz: Class<*>, val property: KMutableProperty0<*>) {
    FEATURES("config", Features::class.java, OnlyFarmMod::feature),
    NOTIFICATIONS("notifications", Notifications::class.java, OnlyFarmMod::notifications),
    ;

    val file by lazy { File(ConfigManager.configDirectory, "$fileName.json") }
}
