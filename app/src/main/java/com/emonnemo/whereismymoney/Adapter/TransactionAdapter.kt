package com.emonnemo.whereismymoney.Adapter

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.emonnemo.whereismymoney.Model.TransactionDaily
import com.emonnemo.whereismymoney.R


/**
 * Created by dikak_000 on 5/1/2018.
 */
class TransactionAdapter(private val context: Context, private var transactionList: ArrayList<TransactionDaily>, private var monthlyAmountList: ArrayList<Long>) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    fun updateList(transactionList: ArrayList<TransactionDaily>, monthlyAmountList: ArrayList<Long>) {
        this.transactionList = transactionList
        this.monthlyAmountList = monthlyAmountList
        notifyDataSetChanged()
    }

    inner class TransactionViewHolder(v: View, private val viewContext: Context) : RecyclerView.ViewHolder(v) {
        private val typeView: TextView = v.findViewById(R.id.transaction_type)
        private val amountView: TextView = v.findViewById(R.id.transaction_amount_content)
        private val monthlyAmountView: TextView = v.findViewById(R.id.transaction_amount_monthly_content)
        private val cardView: CardView = v.findViewById(R.id.transaction_card_view)

        init {

            cardView.setOnClickListener {
                    Toast.makeText(viewContext, "LALA", Toast.LENGTH_SHORT).show()
//                    val intent = Intent(viewContext, DetailTransactionActivity::class.java)
//                    intent.putExtra("kegiatan", jadwalList[adapterPosition])
//                    viewContext.startActivity(intent)
            }

        }

        fun bindTo(transaction: TransactionDaily, monthlyAmount: Long) {
            typeView.text = transaction.getTypeString()
            amountView.text = transaction.amount.toString()
            monthlyAmountView.text = monthlyAmount.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionAdapter.TransactionViewHolder {
        return TransactionViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.transaction_card_view_item, parent, false), context)

    }

    override fun onBindViewHolder(holder: TransactionAdapter.TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        var monthlyAmount = monthlyAmountList[position]
        holder.bindTo(transaction, monthlyAmount)

    }

    override fun getItemCount(): Int {
        return transactionList.size
    }


}
