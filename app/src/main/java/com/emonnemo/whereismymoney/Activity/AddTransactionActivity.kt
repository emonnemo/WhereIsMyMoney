package com.emonnemo.whereismymoney.Activity

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.emonnemo.whereismymoney.Model.Transaction
import com.emonnemo.whereismymoney.R
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_add_transaction.*
import java.util.*

class AddTransactionActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var itemLists = Transaction.getTypeList()

    private var typeSpinner: Spinner? = null
    private var typeInput: Int = 0
    private var dateValue: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // Initiate the value
        val dateInput = findViewById<EditText>(R.id.add_transaction_date_input)
        val amountInput = findViewById<EditText>(R.id.add_transaction_amount_input)
        typeSpinner = this.add_transaction_type_spinner
        val descriptionInput = findViewById<EditText>(R.id.add_transaction_description_input)

        // Load the transactions from shared prefs
        val gson = GsonBuilder().setPrettyPrinting().create()
        val prefs = this.getSharedPreferences("com.emonnemo.whereismymoney.prefs", 0)
        val transactionListString: String = prefs.getString("transaction_list", "")
        var transactionList: MutableList<Transaction> = gson.fromJson(transactionListString, object : TypeToken<MutableList<Transaction>>() {}.type)

        // Set the spinner handler
        typeSpinner!!.onItemSelectedListener = this

        // Create an ArrayAdapter using a simple spinner layout and items array
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemLists)
        // Set layout to use when the list of choices appear
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        typeSpinner!!.adapter = arrayAdapter

        // Set the date picker handler
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)


            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                calendar.set(year, monthOfYear, dayOfMonth)
                dateInput.setText(android.text.format.DateFormat.format("EEEE, dd MMMM yyyy", calendar))
                dateValue = String.format("%d-%d-%d", dayOfMonth, monthOfYear, year)
            }, year, month, day)
            datePickerDialog.show()
        }

        // Set the submit button handler
        val submitButton = findViewById<Button>(R.id.add_transaction_submit_button)
        submitButton.setOnClickListener {
            try {
                if (dateValue == "")
                    throw Exception()
                val amountValue: Long = amountInput.text.toString().toLong()
                val descriptionValue: String = descriptionInput.text.toString()
                val transaction = Transaction(dateValue, typeInput, amountValue, descriptionValue)

                // Apply the changes to shared prefs
                val editor = prefs!!.edit()
                transactionList.add(transaction)
                editor.putString("transaction_list", gson.toJson(transactionList))
                editor.apply()
                onBackPressed()
            } catch (e: Exception) {
                Toast.makeText(this, "Please fill all the form before submitting.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        // use position to know the selected item
        typeInput = position
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }

    companion object {

        private val INTENT_USER_ID = "add_transaction_activity"

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, AddTransactionActivity::class.java)
//            intent.putExtra(INTENT_USER_ID, user.id)
            return intent
        }
    }
}
