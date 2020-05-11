package com.color.hali

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.api.Hover
import com.hover.sdk.api.HoverParameters
import com.hover.sdk.permissions.PermissionActivity
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(),
    Fragment_Insurer_options.insurer_options_interface,
    Fragment_pay_premium.pay_premium_interface,
    Fragment_make_claim.make_claim_interface,
    Hover.DownloadListener
{
    val _Fragment_Insurer_options = "_Fragment_Insurer_options"
    val insurer_list: ArrayList<insurer> = ArrayList()
    val transaction_list: ArrayList<transaction> = ArrayList()
    var is_loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        load_transactions()
        load_companies()

        Hover.initialize(applicationContext, this)

        findViewById<TextView>(R.id.permissions).setOnClickListener {
            val i = Intent(applicationContext, PermissionActivity::class.java)
            startActivityForResult(i, 0)
        }
    }

    fun load_companies(){
        //test items
        insurer_list.add(insurer("Eco-bank Insurance", R.drawable.eco_bank, "Kenya"))
        insurer_list.add(insurer("Jubilee Insurance", R.drawable.jubilee, "Kenya"))
        insurer_list.add(insurer("NHIF Insurance", R.drawable.nhif, "Kenya"))

        val companies_recyclerview: RecyclerView = findViewById(R.id.companies_recyclerview)
        companies_recyclerview.adapter = myInsurersListAdapter()
        companies_recyclerview.layoutManager = LinearLayoutManager(applicationContext,
            LinearLayoutManager.HORIZONTAL, false)
    }


    fun load_transactions(){
        val no_transaction_layout: RelativeLayout = findViewById(R.id.no_transaction_layout)

        if(transaction_list.isEmpty()){
            no_transaction_layout.visibility = View.VISIBLE
        }else{
            no_transaction_layout.visibility = View.GONE
        }

        val transactions_recyclerview:RecyclerView = findViewById(R.id.transactions_recyclerview)
        transactions_recyclerview.adapter = myTransactionsListAdapter()
        transactions_recyclerview.layoutManager = LinearLayoutManager(applicationContext)
    }

    class insurer(val name: String, val image: Int, val country: String): Serializable

    class transaction(val type: String, val time: Long, val insurer: insurer, val status: String, val details: details): Serializable


    internal inner class myInsurersListAdapter : RecyclerView.Adapter<ViewHolderInsurers>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderInsurers {
            val vh = ViewHolderInsurers(LayoutInflater.from(applicationContext)
                .inflate(R.layout.recycler_item_insurance_companies, viewGroup, false))
            return vh
        }

        override fun onBindViewHolder(viewHolder: ViewHolderInsurers, position: Int) {
            val insurer = insurer_list[position]
            viewHolder.company_image.setImageResource(insurer.image)

            viewHolder.company_image.setOnClickListener {
                open_company(insurer)
            }
        }

        override fun getItemCount() = insurer_list.size

    }

    internal inner class ViewHolderInsurers (view: View) : RecyclerView.ViewHolder(view) {
       val company_image: ImageView = view.findViewById(R.id.company_image)
    }



    internal inner class myTransactionsListAdapter : RecyclerView.Adapter<ViewHolderTransactions>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderTransactions {
            val vh = ViewHolderTransactions(LayoutInflater.from(applicationContext)
                .inflate(R.layout.recycler_item_transaction, viewGroup, false))
            return vh
        }

        override fun onBindViewHolder(viewHolder: ViewHolderTransactions, position: Int) {
            val transaction = transaction_list[position]

            viewHolder.to_insurer.text = transaction.details.amount+ " to " + transaction.insurer.name

            val cal = Calendar.getInstance()
            viewHolder.date.text = construct_elapsed_time(cal.timeInMillis-transaction.time)

        }

        override fun getItemCount() = transaction_list.size

    }

    internal inner class ViewHolderTransactions (view: View) : RecyclerView.ViewHolder(view) {
        val what_did: TextView = view.findViewById(R.id.what_did)
        val date: TextView = view.findViewById(R.id.date)
        val to_insurer: TextView = view.findViewById(R.id.to_insurer)
    }


    class details(val first_name: String, val last_name: String, val email: String, val id: String, val amount: String)

    fun open_company(insurer: insurer){
        val as_string = Gson().toJson(insurer)
        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .add(R.id.gucci, Fragment_Insurer_options.newInstance("","", as_string),_Fragment_Insurer_options).commit()
    }

    override fun make_claim(insurer: insurer) {
        val as_string = Gson().toJson(insurer)
        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .add(R.id.gucci, Fragment_make_claim.newInstance("","", as_string),_Fragment_Insurer_options).commit()
    }



    override fun when_make_claim_finished(details: details, insurer: insurer) {
        val transaction = transaction(
            "claim",
            Calendar.getInstance().timeInMillis,
            insurer,
            "Complete",
            details
        )

        transaction_list.add(transaction)
        load_transactions()
        onBackPressed()
    }

    override fun make_contribution(insurer: insurer) {
        val as_string = Gson().toJson(insurer)
        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .add(R.id.gucci, Fragment_pay_premium.newInstance("","", as_string),_Fragment_Insurer_options).commit()
    }

    override fun when_pay_premium_finished(details: details, insurer: insurer) {
        val transaction = transaction(
            "payment",
            Calendar.getInstance().timeInMillis,
            insurer,
            "Complete",
            details
        )

        transaction_list.add(transaction)
        load_transactions()
        onBackPressed()

        val i = HoverParameters.Builder(applicationContext)
            .request("a4ab1dc7") // Add your action ID here
            .extra("bizNumber", "328100")
            .extra("acctNumber", "328100")
            .extra("amount", details.amount).buildIntent()
        startActivityForResult(i, 0)
    }

    override fun onBackPressed() {
        if(supportFragmentManager.fragments.size>0){
            val trans = supportFragmentManager.beginTransaction()
            trans.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
            val currentFragPos = supportFragmentManager.fragments.size-1

            trans.remove(supportFragmentManager.fragments.get(currentFragPos))
            trans.commit()
            supportFragmentManager.popBackStack()

        }else super.onBackPressed()
    }



    fun show_loading_screen(){
        is_loading = false

        val my_loading_screen: RelativeLayout = findViewById(R.id.my_loading_screen)
        my_loading_screen.visibility = View.VISIBLE
        my_loading_screen.setOnTouchListener { v, event -> true }
    }

    fun hide_loading_screen(){
        is_loading = false
        val my_loading_screen: RelativeLayout = findViewById(R.id.my_loading_screen)
        my_loading_screen.visibility = View.GONE
    }



    fun construct_elapsed_time(time: Long): String{
        val a_year = 1000L*60L*60L*24L*365L
        val a_month = 1000L*60L*60L*24L*30L
        val a_week = 1000L*60L*60L*24L*7L
        val a_day = 1000L*60L*60L*24L
        val an_hour = 1000L*60L*60L
        val a_minute = 1000L*60L
        val a_second = 1000L

        Log.d("view", "appication time: "+time+ " a year in mills: "+a_year)
        if(time>=a_year){
            //time is greater than a year, we will parse the time in years
            val time_in_years = (time.toDouble()/a_year.toDouble()).toInt()
            var t = "yrs."
            if(time_in_years==1) t = "yr."
            return time_in_years.toString()+t
        }
        else if(time>=a_month){
            //time is greater than a month, we will parse the time in months
            val time_in_months = (time.toDouble()/a_month.toDouble()).toInt()
            var t = "months."
            if(time_in_months==1) t = "month."
            return time_in_months.toString()+t
        }
        else if(time>=a_week){
            //time is greater than a week, we will parse the time in week
            val time_in_weeks = (time.toDouble()/a_week.toDouble()).toInt()
            var t = "wks."
            if(time_in_weeks==1) t = "wk."
            return time_in_weeks.toString()+t
        }
        else if(time>=a_day){
            //time is greater than a day, we will parse the time in day
            val time_in_days = (time.toDouble()/a_day.toDouble()).toInt()
            var t = "days."
            if(time_in_days==1) t = "day."
            return time_in_days.toString()+t
        }
        else if(time>=an_hour){
            //time is greater than an hour, we will parse the time in hours
            val time_in_hours = (time.toDouble()/an_hour.toDouble()).toInt()
            var t = "hrs."
            if(time_in_hours==1) t = "hr."
            return time_in_hours.toString()+ t
        }
        else if(time>=a_minute){
            //time is greater than a minute, we will parse the time in minutes
            val time_in_minutes = (time.toDouble()/a_minute.toDouble()).toInt()
            var t = "min."
            if(time_in_minutes==1) t = "min."
            return time_in_minutes.toString()+ t
        }
        else{
            val time_in_seconds = (time.toDouble()/a_second.toDouble()).toInt()
            var t = "sec."
            if(time_in_seconds==1) t = "sec."
            return time_in_seconds.toString()+t
        }

    }

    override fun onSuccess(p0: java.util.ArrayList<HoverAction>?) {

    }

    override fun onError(p0: String?) {

//		Toast.makeText(this, "Error while attempting to download actions, see logcat for error", Toast.LENGTH_LONG).show();
        Log.e("TAG", "Error: $p0")
    }


}
