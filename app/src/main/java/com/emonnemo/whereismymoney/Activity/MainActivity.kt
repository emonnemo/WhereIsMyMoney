/*
The MIT License (MIT)

Copyright (c) [2017] [Sundeepk]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.emonnemo.whereismymoney.Activity

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.util.Log
import android.widget.TextView
import com.emonnemo.whereismymoney.Adapter.TransactionAdapter
import com.emonnemo.whereismymoney.Model.Transaction
import com.emonnemo.whereismymoney.Model.TransactionDaily
import com.emonnemo.whereismymoney.R
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    private var compactCalendarView: CompactCalendarView?= null
    private var monthlySum: ArrayList<Long> = ArrayList()
    private var transactionList: ArrayList<Transaction> = ArrayList()
    private var dailyTransactionList: ArrayList<TransactionDaily> = ArrayList()
    private var dailyTransactionListMap: MutableMap<Date, ArrayList<TransactionDaily>> = HashMap()
    private var transactionAdapter: TransactionAdapter = TransactionAdapter(this, dailyTransactionList, monthlySum)

    fun initiateDailyTransaction(midnightDate: Date) {
        Log.d("INIT DAILY", "$midnightDate")
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = midnightDate
        val dateString: String = DateFormat.format("dd-MM-yyyy", midnightDate).toString()
        if (!dailyTransactionListMap.containsKey(midnightDate)) {
            dailyTransactionListMap.put(midnightDate, ArrayList())
            for (i in 0 until Transaction.getTypeListSize()) {
                dailyTransactionListMap[midnightDate]!!.add(TransactionDaily(dateString, i, 0, ArrayList()))
            }
            transactionList
                    .filter { dateString == it.date }
                    .forEach {
                        dailyTransactionListMap[midnightDate]!![it.type].addTransaction(it)
                    }
            for (i in 0 until Transaction.getTypeListSize()) {
                monthlySum[i] += dailyTransactionListMap[midnightDate]!![i].amount
            }
        }
        dailyTransactionList = dailyTransactionListMap[midnightDate]!!
        transactionAdapter.updateList(dailyTransactionList, monthlySum)
    }

    fun initiateCalendarMonth(compactCalendarView: CompactCalendarView) {
        compactCalendarView.removeAllEvents()
        dailyTransactionListMap.clear()
        val date: Date = compactCalendarView.firstDayOfCurrentMonth
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date
        monthlySum = ArrayList()
        for (i in 0 until Transaction.getTypeListSize()) {
            monthlySum.add(0)
        }

        for (i in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            calendar.set(Calendar.DAY_OF_MONTH, i)
            initiateDailyTransaction(calendar.time)
            val sumAmount: Long = (0 until Transaction.getTypeListSize())
                    .map { dailyTransactionList[it].amount }
                    .sum()
            var color: Int
            if (sumAmount < 30000) {
                color = Color.parseColor("#ffcc00")
            } else if (sumAmount < 60000){
                color = Color.parseColor("#ff823a")
            } else {
                color = Color.parseColor("#ff0000")
            }
            if (sumAmount > 0) {
                val event = Event(color, calendar.timeInMillis)
                compactCalendarView.addEvent(event)
            }
        }
    }

    override fun onRestart() {
        Log.d("RESTART", "RESTART")
        super.onRestart()
        val gson = GsonBuilder().setPrettyPrinting().create()
        val prefs = this.getSharedPreferences("com.emonnemo.whereismymoney.prefs", 0)
        val transactionListString: String = prefs.getString("transaction_list", "")
        transactionList = gson.fromJson(transactionListString, object : TypeToken<ArrayList<Transaction>>() {}.type)
        initiateCalendarMonth(compactCalendarView as CompactCalendarView)
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.HOUR, 0)
        val midnightDate: Date = calendar.time
        initiateDailyTransaction(midnightDate)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("RESTART", "ONCREATE")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Initiate some variables
        val monthTextView = findViewById<TextView>(R.id.month_name)
        val dayTextView = findViewById<TextView>(R.id.current_day)
        val recyclerView = findViewById<RecyclerView>(R.id.transaction_recycler_view)
        val gson = GsonBuilder().setPrettyPrinting().create()
        val prefs = this.getSharedPreferences("com.emonnemo.whereismymoney.prefs", 0)
        val transactionListString: String = prefs.getString("transaction_list", "")
        transactionList = gson.fromJson(transactionListString, object : TypeToken<ArrayList<Transaction>>() {}.type)

        // Set recycler view handler
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        transactionAdapter = TransactionAdapter(this, dailyTransactionList, monthlySum)
        recyclerView.adapter = transactionAdapter

        val calendar: Calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.HOUR, 0)
        val midnightDate: Date = calendar.time

        // Set compact calendar view handler
        compactCalendarView = findViewById<CompactCalendarView>(R.id.compactcalendar_view)
        (compactCalendarView as CompactCalendarView).setCurrentDate(midnightDate)
        monthTextView.text = DateFormat.format("MMMM yy", midnightDate)
        dayTextView.text = DateFormat.format("dd MMMM yy", midnightDate)

        // Initiate month view list
        initiateCalendarMonth(compactCalendarView as CompactCalendarView)
        initiateDailyTransaction(midnightDate)
        (compactCalendarView as CompactCalendarView).setCurrentDate(midnightDate)

        // define a listener to receive callbacks when certain events happen.
        (compactCalendarView as CompactCalendarView).setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                if (!(compactCalendarView as CompactCalendarView).getEvents(dateClicked).isEmpty()) {
                    (compactCalendarView as CompactCalendarView).setCurrentSelectedDayBackgroundColor((compactCalendarView as CompactCalendarView).getEvents(dateClicked)[0].color)
                } else {
                    (compactCalendarView as CompactCalendarView).setCurrentSelectedDayBackgroundColor(Color.parseColor("#bbbbbb"))
                }

                dayTextView.text = DateFormat.format("dd MMMM yy", dateClicked)
                initiateDailyTransaction(dateClicked)
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                initiateCalendarMonth(compactCalendarView as CompactCalendarView)
                monthTextView.text = DateFormat.format("MMMM yy", firstDayOfNewMonth)
                dayTextView.text = DateFormat.format("dd MMMM yy", firstDayOfNewMonth)
                initiateDailyTransaction(firstDayOfNewMonth)
            }
        })

        fab.setOnClickListener {
            // Start add transaction intent
            val intent = AddTransactionActivity.newIntent(this)
            startActivity(intent)
        }
    }

}
