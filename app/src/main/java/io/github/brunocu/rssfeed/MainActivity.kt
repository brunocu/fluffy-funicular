package io.github.brunocu.rssfeed

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL
import kotlin.properties.Delegates

class FeedEntry {
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""

    override fun toString(): String {
        return """
            name = $name
            artist = $artist
            releaseDate = $releaseDate
            summary = $summary
            imageURL = $imageURL
        """.trimIndent()
    }
}

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.xmlRecyclerView)

        Log.d(TAG, "onCreate")

        val downloadData = DownloadData(this, recyclerView)
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
        Log.d(TAG, "onCreate DONE")
    }

    // -- dos formas de objetos "anidados"
    // Inline - mala práctica
//    inner object Object {}
    // Companion
    companion object {
        private class DownloadData(context: Context, recyclerView: RecyclerView) : AsyncTask<String, Void, String>() {
            private val TAG = "DownloadData"

            var localContext: Context by Delegates.notNull()
            var localRecyclerView: RecyclerView by Delegates.notNull()

            init {
                localContext = context
                localRecyclerView = recyclerView
            }

            override fun doInBackground(vararg url: String?): String {
                Log.d(TAG, "doInBackground")
                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()) {
                    Log.e(TAG, "doInBackground: Failed")
                }
                Log.d(TAG, rssFeed)
                return rssFeed
            }

            override fun onPostExecute(result: String) {
//                val xmlResult = StringBuilder()
//                try {
//                    val url = URL(urlPath)
//
//                    var connection: HttpURLConnection = url.openConnection() as HttpURLConnection
//                    val response = connection.responseCode
//                    Log.d(TAG, "downloadXML response was $response")
//
////                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
////                    val inputBuffer = CharArray(500)
////                    var charsRead = 0
////                    while (charsRead >= 0) {
////                        charsRead = reader.read(inputBuffer)
////                        // if no data don't stop
////                        if (charsRead > 0) {
////                            xmlResult.append(String(inputBuffer, 0, charsRead))
////                        }
////                    }
////                    reader.close()
//                    // Kotlin-ified
//                    connection.inputStream.buffered().reader().use { reader ->
//                        xmlResult.append(reader.readText())
//                    }
//                    Log.d(TAG, "Received ${xmlResult.length} bytes")
//                    return xmlResult.toString()
//
////                } catch (e: MalformedURLException) {
////                    Log.e(TAG, "downloadXML: Invalid URL: ${e.message}")
////                } catch (e: IOException) {
////                    Log.e(TAG, "downloadXML: Error reading data: ${e.message}")
////                } catch (e: Exception) {
////                    Log.e(TAG, "downloadXML: Unknown error: ${e.message}")
////                }
//
//                } catch (e: Exception) {
//                    val errorMessage: String = when (e) {
//                        is MalformedURLException -> "downloadXML: Invalid url: ${e.message}"
//                        is IOException -> "downloadXML: Error reading data: ${e.message}"
//                        else -> "downloadXML: Unknown Error: ${e.message}"
//                    }
//                    Log.e(TAG, errorMessage)
//                }
//                return ""
//            }
                super.onPostExecute(result)
                val parsedApplication = ParseApplication()
                parsedApplication.parse(result)
//                for (app in parsedApplication.applications) {
//                    Log.d(TAG, app.toString())
//                }
                val adapter: ApplicationsAdapter = ApplicationsAdapter(localContext, parsedApplication.applications)
                localRecyclerView.adapter = adapter
                localRecyclerView.layoutManager = LinearLayoutManager(localContext)
            }

            private fun downloadXML(urlPath: String?): String {
                try {
                    return URL(urlPath).readText()
                } catch (e: Exception) {
                    val errorMessage: String = when (e) {
                        is MalformedURLException -> "downloadXML: Invalid URL ${e.message}"
                        is IOException -> "downloadXML: IOException reading data ${e.message}"
                        else -> "downloadXML: Unknown Error ${e.message}"
                    }
                    Log.e(TAG, errorMessage)
                }
                return ""
            }
        }
    }
}