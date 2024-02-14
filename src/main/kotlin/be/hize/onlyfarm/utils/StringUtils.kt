package be.hize.onlyfarm.utils

object StringUtils {

    private val formattingChars by lazy { "kmolnr".toCharArray() + "kmolnr".uppercase().toCharArray() }
    fun String.removeColor(keepFormatting: Boolean = false): String {
        val builder = StringBuilder(this.length)

        var counter = 0
        while (counter < this.length) {
            if (this[counter] == '§') {
                if (!keepFormatting || this[counter + 1] !in formattingChars) {
                    counter += 2
                    continue
                }
            }
            builder.append(this[counter])
            counter++
        }

        return builder.toString()
    }

}