import okhttp3.ResponseBody
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Scrapes the playsounds page for all available sound bytes.
 * Downloads ones we don't have already.
 * Deletes ones that don't exist anymore.
 */
class SoundByteDownloader extends DefaultTask {
    private static final HOST_PLAY_SOUNDS = "http://chatbot.admiralbulldog.live/"
    private static final HOST_NUULS = "https://i.nuuls.com/"
    private static final FILE_TYPE = "mp3"

    private final playSoundsService = new Retrofit.Builder()
            .baseUrl(HOST_PLAY_SOUNDS)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(PlaySoundsService.class)
    private final nuulsService = new Retrofit.Builder()
            .baseUrl(HOST_NUULS)
            .build()
            .create(NuulsService.class)

    String soundsDir

    @TaskAction
    void download() {
        final targetPath = "src/main/resources/$soundsDir"
        final existing = new ArrayList<String>()
        project.file(targetPath).listFiles().each {
            if (it.name.toLowerCase().endsWith(".$FILE_TYPE")) {
                existing.add(it.name)
            }
        }

        final response = playSoundsService.getHtml().execute()
        final html = new String(response.body().bytes())
                .split("<tr>")
                .drop(2)
                .dropRight(1)

        html.each { block ->
            final name = block.split("<td>")[1].split("</td>").first()
            final url = block.split("data-link=\"")[1].split("\"").first()
            existing.remove(name + ".mp3")
            downloadSound(name, url, targetPath)
        }

        existing.each {
            println "Sound no longer exists: $it"
            project.file("$targetPath/$it").delete()
        }

        println "Total monitored sounds: ${html.size()}"
    }

    void downloadSound(String name, String url, String folder) {
        final fileName = "$folder/$name.$FILE_TYPE"
        if (project.file(fileName).exists()) {
            return
        }
        final response = nuulsService.get(url.trim().replace(HOST_NUULS, "")).execute()
        if (!response.isSuccessful()) {
            throw new RuntimeException("Failed to get sound '$url': ${response.code()}")
        }
        final stream = response.body().byteStream()
        final output = new FileOutputStream(fileName)
        final buffer = new byte[4096]
        while (true) {
            final read = stream.read(buffer)
            if (read == -1) {
                break
            }
            output.write(buffer, 0, read)
        }
        output.close()
        stream.close()
        println("Downloaded new sound: $name.$FILE_TYPE")
    }

    private interface PlaySoundsService {
        @GET("playsounds")
        Call<ResponseBody> getHtml()
    }

    private interface NuulsService {
        @GET("{name}")
        Call<ResponseBody> get(@Path("name") String name)
    }
}
