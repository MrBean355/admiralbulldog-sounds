import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.FileOutputStream

data class SoundFile(val fileName: String, val url: String)

/** Scrape the PlaySounds web page and collect a list of sound file names and URLs. */
fun listRemoteFiles(): List<SoundFile> {
    val response = playSoundsService.getHtml().execute()
    val responseBody = response.body()
    if (!response.isSuccessful || responseBody == null) {
        throw RuntimeException("Unable to download HTML, code=${response.code()}, body length=${responseBody?.contentLength()}")
    }
    val html = String(responseBody.bytes())
    val blocks = html.split("<tr>")
            .drop(2)

    return blocks.map { block ->
        val friendlyName = block.split("<td>")[1].split("</td>").first()
        val url = block.split("data-link=\"")[1].split("\"").first()
        val fileExtension = url.substringAfterLast('.', missingDelimiterValue = "")
        val fileName = "$friendlyName.$fileExtension"
        SoundFile(fileName, url)
    }
}

/** Download the given sound to the given destination. */
fun downloadFile(file: SoundFile, destDir: String) {
    val response = nuulsService.get(file.url.removePrefix(HOST_NUULS)).execute()
    val responseBody = response.body()
    if (!response.isSuccessful || responseBody == null) {
        throw RuntimeException("Unable to download $file, code=${response.code()}, body length=${responseBody?.contentLength()}")
    }
    val stream = responseBody.byteStream()
    val output = FileOutputStream("$destDir/${file.fileName}")
    val buffer = ByteArray(4096)
    while (true) {
        val read = stream.read(buffer)
        if (read == -1) {
            break
        }
        output.write(buffer, 0, read)
    }
    output.close()
    stream.close()
}

private const val HOST_CHATBOT = "http://chatbot.admiralbulldog.live/"
private const val HOST_NUULS = "https://i.nuuls.com/"

private val playSoundsService = Retrofit.Builder()
        .baseUrl(HOST_CHATBOT)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
        .create(PlaySoundsService::class.java)

private val nuulsService = Retrofit.Builder()
        .baseUrl(HOST_NUULS)
        .build()
        .create(NuulsService::class.java)

private interface PlaySoundsService {
    @GET("playsounds")
    fun getHtml(): Call<ResponseBody>
}

private interface NuulsService {
    @GET("{name}")
    fun get(@Path("name") name: String): Call<ResponseBody>
}