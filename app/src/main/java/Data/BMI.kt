package Data

import com.example.engineering_thesis.GlobalClass

class BMI {
    var GlobalClass = GlobalClass()

    fun countBMI(weight: Double, height: Double): Double{
        val heightInMeters = height / 100
        val bmi = weight / (heightInMeters * heightInMeters)
        return bmi
    }

}