package zarama.fabian.dbzguess

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Matcher
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    var arrayName = arrayListOf<String>()
    var arrayURLS = arrayListOf<String>()
    var arrayPosition : Int = 0

    class ImageDownloader : AsyncTask<String,Void, Bitmap>() {
        override fun doInBackground(vararg urls: String?): Bitmap? {

            try {
                val url = URL(urls.get(0))
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val input = connection.inputStream
                val myBitMap = BitmapFactory.decodeStream(input)
                return myBitMap

            }catch (e:Exception){
                Log.i("error doInBackground", e.localizedMessage)
                return null
            }

        }
    }

    fun startGuess(){
        val task = ImageDownloader()
        val myImage : Bitmap
        arrayPosition = (Math.random()*100).toInt()
        var buttonShuffle = arrayListOf<Button>()

        try {
            myImage = task.execute(arrayURLS.get(arrayPosition)).get()
            imgDbz.setImageBitmap(myImage)

            buttonShuffle.add(btnOne)
            buttonShuffle.add(btnTwo)
            buttonShuffle.add(btnThree)
            buttonShuffle.add(btnFour)

            buttonShuffle.shuffle()

            buttonShuffle.get(0).setText(arrayName.get(arrayPosition))
            buttonShuffle.get(1).setText(arrayName.get((Math.random()*100).toInt()))
            buttonShuffle.get(2).setText(arrayName.get((Math.random()*100).toInt()))
            buttonShuffle.get(3).setText(arrayName.get((Math.random()*100).toInt()))


        }catch (e:Exception){
            Log.i("error startGuess", e.localizedMessage)
        }

    }


    // Pass in a string, pass out String
    open class DownloadTask() : AsyncTask<String, Void, String>(){
        protected override fun doInBackground(vararg urls: String?): String { // is like an array
            var result: String = ""

            try {

                result = URL(urls.get(0)).readText()

            }catch (e: Exception){
                Log.i("error DownloadTask()", e.localizedMessage)
            }
            return result
        }


    }

    fun checkAnswer(view: View){

        val buttonPressed = view as Button

        if(buttonPressed.text == arrayName.get(arrayPosition)){
            Toast.makeText(this,"Correct",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this,"Incorrect it was "+arrayName.get(arrayPosition),Toast.LENGTH_SHORT).show()
        }

        startGuess()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var task = DownloadTask()
        var result : String = ""

        try {
            //result = task.execute("http://www.posh24.se/kandisar").get()
            result = task.execute("https://comicvine.gamespot.com/profile/instant1100/lists/top-100-strongest-dragon-ball-characters/62050/").get()
            var splitResult  = result.split("id=\"default-content\"")

            var p = Pattern.compile("img src=\"(.*?)\"")
            var m = p.matcher(splitResult.get(1))



            while (m.find()){
                arrayURLS.add(0,m.group(1))
            }

            arrayURLS.reverse()

             p = Pattern.compile("<h3>(.*?)</h3?")
             m = p.matcher(splitResult.get(1))


            while (m.find()){
                arrayName.add(m.group(1))

            }

            startGuess()


        }catch (e: Exception){
            Log.i("error onCreate", e.printStackTrace().toString())
        }



    }
}
