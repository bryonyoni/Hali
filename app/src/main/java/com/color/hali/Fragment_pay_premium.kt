package com.color.hali

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson


class Fragment_pay_premium : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private val ARG_INSURER = "ARG_INSURER"
    private lateinit var  insurer: MainActivity.insurer
    private lateinit var listener: pay_premium_interface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            insurer = Gson().fromJson(it.getString(ARG_INSURER), MainActivity.insurer::class.java)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is pay_premium_interface){
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val va = inflater.inflate(R.layout.fragment_pay_premium, container, false)

        val first_nameEditText: EditText = va.findViewById(R.id.first_nameEditText)
        val last_nameEditText: EditText = va.findViewById(R.id.last_nameEditText)
        val emailEditText: EditText = va.findViewById(R.id.emailEditText)
        val id_numberEditText: EditText = va.findViewById(R.id.id_numberEditText)
        val amountEditText: EditText = va.findViewById(R.id.amountEditText)
        val finish_textview: TextView = va.findViewById(R.id.finish_textview)

        finish_textview.setOnClickListener {
            val f_name = first_nameEditText.text.toString().trim()
            val l_name = last_nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val id = id_numberEditText.text.toString().trim()
            val amount = amountEditText.text.toString().trim()

            if(f_name.equals("")){
                first_nameEditText.setError("Type something")
            }else if(l_name.equals("")){
                last_nameEditText.setError("Type something")
            }else if(email.equals("")){
                emailEditText.setError("Type something")
            }else if(id.equals("")){
                id_numberEditText.setError("Type something")
            }else if(amount.equals("")){
                amountEditText.setError("Type something")
            }else{
                val details = MainActivity.details(f_name,l_name,email,id,amount)
                listener.when_pay_premium_finished(details,insurer)
            }

        }


        return va
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String, insurer: String) =
            Fragment_pay_premium().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_INSURER, insurer)
                }
            }
    }


    interface pay_premium_interface{
        fun when_pay_premium_finished(details: MainActivity.details, insurer: MainActivity.insurer)
    }
}
