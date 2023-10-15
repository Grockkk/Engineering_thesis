package RecycleTools

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.engineering_thesis.R

class heartRateAdapter(var Samples: List<HeartRateSample>): RecyclerView.Adapter<heartRateAdapter.heartRateViewHolder>() {

    inner class heartRateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): heartRateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_heart_rate,parent,false)
        return heartRateViewHolder(view)

    }

    override fun getItemCount(): Int {
        return Samples.size
    }

    override fun onBindViewHolder(holder: heartRateViewHolder, position: Int) {
        val sample = Samples[position] // Pobieramy obiekt z listy dla danej pozycji

        // Przypisujemy odpowiednie wartości do TextViews w layoucie elementu listy
        holder.itemView.findViewById<TextView>(R.id.heartRateBpm).text = sample.sample
        holder.itemView.findViewById<TextView>(R.id.SampleTime).text = sample.time
    }
}