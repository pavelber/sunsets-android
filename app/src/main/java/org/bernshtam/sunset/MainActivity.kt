package org.bernshtam.sunset

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), CoroutineScope by MainScope(),
    AdapterView.OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner: Spinner = findViewById(R.id.spinner)
        spinner.onItemSelectedListener = this

        launch(Dispatchers.Main) {
            try {
                updateSpinner()
            } catch (e: Exception) {
                e.printStackTrace()
                updateSpinner(arrayOf("Can't fetch locations"))
            }
        }


    }

    private fun updateSpinner(locations: Array<String>) {
        // Get reference of widgets from XML layout
        val spinner = findViewById<Spinner>(R.id.spinner)

        // Initializing an ArrayAdapter
        val spinnerArrayAdapter = ArrayAdapter(
            this, R.layout.spinner_item, locations
        )
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item)
        spinner.adapter = spinnerArrayAdapter
    }

    private suspend fun updateSpinner() {
        updateSpinner(ServerWrapper.getLocations())
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        val selectedLocation = parent.getItemAtPosition(pos) as String
        val coordinatesJson = ServerWrapper.name2Coordinates.get(selectedLocation)
        if (coordinatesJson != null) {
            launch(Dispatchers.Main) {
                try {
                    updateTexts(ServerWrapper.getPrediction(coordinatesJson))
                } catch (e: Exception) {
                    e.printStackTrace()
                    updateTexts(ServerWrapper.emptyData)
                }
            }
        } else updateTexts(ServerWrapper.emptyData)
    }

    private fun updateTexts(data: List<String>) {
        val day1 = findViewById<TextView>(R.id.day1)
        val day2 = findViewById<TextView>(R.id.day2)
        val day3 = findViewById<TextView>(R.id.day3)
        day1.text = data.getOrNull(0) ?: "No Data"
        day2.text = data.getOrNull(1) ?: "No Data"
        day3.text = data.getOrNull(2) ?: "No Data"
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
}
