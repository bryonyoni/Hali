package com.color.hali

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.gson.Gson


class Fragment_Insurer_options : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private val ARG_INSURER = "ARG_INSURER"
    private lateinit var  insurer: MainActivity.insurer
    private lateinit var listener: insurer_options_interface

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
        if(context is insurer_options_interface){
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val va = inflater.inflate(R.layout.fragment__insurer_options, container, false)

        val company_image: ImageView = va.findViewById(R.id.company_image)
        val insurer_name: TextView = va.findViewById(R.id.insurer_name)
        val make_claim_card: CardView = va.findViewById(R.id.make_claim_card)
        val pay_contribution_card: CardView = va.findViewById(R.id.pay_contribution_card)

        company_image.setImageResource(insurer.image)
        insurer_name.setText(insurer.name)

        make_claim_card.setOnClickListener {
            listener.make_claim(insurer)
        }

        pay_contribution_card.setOnClickListener {
            listener.make_contribution(insurer)
        }

        return va
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String, insurer: String) =
            Fragment_Insurer_options().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_INSURER, insurer)
                }
            }
    }


    interface insurer_options_interface{
        fun make_claim(insurer: MainActivity.insurer)
        fun make_contribution(insurer: MainActivity.insurer)
    }
}
