package com.emonnemo.whereismymoney.Model

/**
 * Created by dikak_000 on 5/1/2018.
 */

data class TransactionDaily(val date: String, val type: Int, var amount: Long, val transactionList: ArrayList<Transaction>) {
//    var date: String by Delegates.notNull()
//    var type: Int
//    var amount: Long
//    var description: String by Delegates.notNull()
//
//    init {
//        this.date = date_
//        this.type = type_
//        this.amount = amount_
//        this.description = description_
//    }

    fun addTransaction(transaction: Transaction) {
        transactionList.add(transaction)
        amount += transaction.amount
    }
    fun removeTransaction(transaction: Transaction) {
        transactionList.remove(transaction)
        amount -= transaction.amount
    }
    fun getTypeString() = Transaction.getType(type)

    companion object {
        val typeLists = arrayOf("Transportation", "Food", "Others")
        fun getType(position: Int) = typeLists[position]
    }

}