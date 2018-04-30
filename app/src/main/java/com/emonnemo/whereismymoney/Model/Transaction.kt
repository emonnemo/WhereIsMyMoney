package com.emonnemo.whereismymoney.Model

/**
 * Created by dikak_000 on 4/30/2018.
 */

data class Transaction(val date: String, val type: Int, val amount: Long, val description: String) {

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

    companion object {
        val typeLists = arrayOf("Transportation", "Food", "Others")

        fun getTypeList() = typeLists
        fun getType(position: Int) = typeLists[position]
    }

}