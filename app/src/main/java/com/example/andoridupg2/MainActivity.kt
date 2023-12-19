package com.example.andoridupg2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import com.example.andoridupg2.ui.theme.AndoridUPG2Theme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndoridUPG2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CocktailLauncher()
                }
            }
        }
    }
}

@Composable
fun CocktailLauncher(modifier: Modifier = Modifier) {
    val client = OkHttpClient()

    var cocktail by remember { mutableStateOf("Cocktail") }

    var cocktailPictureUrl by remember { mutableStateOf("") }

    val started by remember { mutableStateOf(false) }

    fun fetchCocktail() {
        val req = Request.Builder().url("https://www.thecocktaildb.com/api/json/v1/1/random.php").build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("APIDEBUG", "Fetching not successful")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseString = response.body!!.string()

                    Log.i("APIDEBUG", responseString)

                    val cocktailData =
                        Json { ignoreUnknownKeys = true }.decodeFromString<CocktailData>(responseString)

                    cocktail = cocktailData.drinks?.firstOrNull()?.strDrink ?: "No Cocktail Found"

                    cocktailPictureUrl = cocktailData.drinks?.firstOrNull()?.strDrinkThumb ?: ""
                }
            }
        })
    }

    LaunchedEffect(started) {
        fetchCocktail()
    }

    Column {
        Text(
            text = cocktail,
            modifier = modifier

        )

        if (cocktailPictureUrl.isNotEmpty()) {

            Image(
                painter = rememberAsyncImagePainter(model = cocktailPictureUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
        Button(onClick = { fetchCocktail() }) {
            Text(text = "New cocktail")
        }
    }
}

@Composable
fun PreviewCocktailLauncher() {
    AndoridUPG2Theme {
        CocktailLauncher()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PreviewCocktailLauncher()
}

@Serializable
data class CocktailData(val drinks: List<Cocktail>?)

@Serializable
data class Cocktail(val strDrink: String, val strDrinkThumb: String?)
