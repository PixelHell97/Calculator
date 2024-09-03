package com.pixel.calculator

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pixel.calculator.Constants.MODE_VIEW_KEY
import com.pixel.calculator.Constants.SHARED_KEY
import com.pixel.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private var isNewCalc = false
    private var isCalcEmpty = true
    private var isHaveDot = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences(SHARED_KEY, Context.MODE_PRIVATE)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
    }

    private fun initView() {
        binding.firstNumber.text = null
        binding.operator.text = null
        binding.secondNumber.text = null
        binding.result.text = getString(R.string._0)
        isCalcEmpty = true

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            binding.isNightMode = true
        } else if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            binding.isNightMode = false
        }

        binding.nightModeOn.setOnClickListener {
            binding.isNightMode = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            with(sharedPref.edit()) {
                putInt(MODE_VIEW_KEY, AppCompatDelegate.MODE_NIGHT_YES)
                apply()
            }
        }

        binding.nightModeOff.setOnClickListener {
            binding.isNightMode = false
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            with(sharedPref.edit()) {
                putInt(MODE_VIEW_KEY, AppCompatDelegate.MODE_NIGHT_NO)
                apply()
            }
        }
    }

    fun onDigitClick(view: View) {
        if (isCalcEmpty) {
            if (isNewCalc) {
                clear()
                isNewCalc = false
            }
            if (binding.result.text == getString(R.string._0) &&
                (view as Button).text ==
                getString(
                    R.string._0,
                )
            ) {
                return //
            }
            binding.result.text = (view as Button).text
            isCalcEmpty = false
            return
        }
        binding.result.append((view as Button).text)
    }

    fun onOperatorClick(view: View) {
        if (isNewCalc) {
            binding.firstNumber.text = binding.result.text
            binding.operator.text = (view as Button).text
            binding.secondNumber.text = null
            isNewCalc = false
            return
        }
        if (isCalcEmpty) return
        if (binding.firstNumber.text.isNullOrBlank()) {
            binding.firstNumber.text = binding.result.text
        } else {
            val result =
                calculate(
                    binding.firstNumber.text
                        .toString()
                        .toDouble(),
                    binding.operator.text.toString()[0],
                    binding.result.text
                        .toString()
                        .toDouble(),
                )
            binding.firstNumber.text = result
            binding.result.text = result
        }
        binding.operator.text = (view as Button).text
        isHaveDot = false
        isCalcEmpty = true
    }

    fun onClearClick(view: View) {
        clear()
    }

    private fun clear() {
        binding.firstNumber.text = null
        binding.operator.text = null
        binding.secondNumber.text = null
        binding.result.text = getString(R.string._0)
        isCalcEmpty = true
    }

    fun onDeleteClick(view: View) {
        if (isCalcEmpty) return
        val length =
            binding.result.text
                .toString()
                .length
        if (isHaveDot) {
            if (binding.result.text[length] == '.') {
                binding.result.text =
                    binding.result.text
                        .toString()
                        .substring(0, length)
                isHaveDot = false
                return
            }
        }
        binding.result.text =
            binding.result.text
                .toString()
                .substring(0, length - 1)
        if (binding.result.text.isNullOrBlank()) {
            binding.result.text = getString(R.string._0)
            isCalcEmpty = true
        }
    }

    fun onEqualClick(view: View) {
        if (isCalcEmpty) return
        if (binding.operator.text.isNullOrBlank()) return
        binding.secondNumber.text = binding.result.text
        val firstNum: Double =
            binding.firstNumber.text
                .toString()
                .toDouble()
        val secNum: Double =
            binding.secondNumber.text
                .toString()
                .toDouble()
        val operator: Char =
            binding.operator.text
                .toString()[0]
        binding.result.text = calculate(firstNum, operator, secNum)
        isCalcEmpty = true
        isHaveDot = false
        isNewCalc = true
    }

    private fun calculate(
        firstNum: Double,
        operator: Char,
        secNum: Double,
    ): String {
        var result = 0.0
        when (operator) {
            '+' -> {
                result = firstNum + secNum
            }

            '-' -> {
                result = firstNum - secNum
            }

            '×' -> {
                result = firstNum * secNum
            }

            '÷' -> {
                result = firstNum / secNum
            }

            else -> {
                0.0
            }
        }

        return if (result % 1 == 0.0) {
            result.toInt().toString()
        } else {
            String.format("%.2f", result)
        }
    }

    fun onDotClick(view: View) {
        if (isHaveDot) return
        if (isCalcEmpty) {
            binding.result.text = getString(R.string._0)
        }
        binding.result.append(getString(R.string.dot))
        isHaveDot = true
        isCalcEmpty = false
    }

    private fun getResult(result: String): String {
        var realNumber = 0
        var decimalNumber = 0

        var i = 0
        var j = i + 1
        while (i < result.length) {
            if (result[i] == '.') {
                while (j < result.length) {
                    decimalNumber = (decimalNumber * 10) + result[j].code
                    j++
                }
                break
            }
            realNumber = (realNumber * 10) + result[i].code
            i++
        }
        return if (decimalNumber == 0) {
            realNumber.toString()
        } else {
            "$realNumber.$decimalNumber"
        }
    }

    fun onPlusAndMinusClick(view: View) {
        val resultCurrentValue =
            binding.result.text
                .toString()
                .toDouble()
        if (resultCurrentValue % 1 == 0.0) {
            binding.result.text = (resultCurrentValue / -1).toInt().toString()
        } else {
            binding.result.text = String.format("%.2f", resultCurrentValue / -1)
        }
    }

    fun onModulusClick(view: View) {
        if (binding.result.text == getString(R.string._0)) return
        val currentValue =
            binding.result.text
                .toString()
                .toDouble()

        binding.result.text = (currentValue % 100).toString()
        isCalcEmpty = true
        isHaveDot = false
        isNewCalc = true
    }
}
