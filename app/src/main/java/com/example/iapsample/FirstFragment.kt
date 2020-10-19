package com.example.iapsample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import com.stripe.android.GooglePayConfig
import com.stripe.android.model.Token
import org.json.JSONArray
import org.json.JSONObject

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isReadyToPay()

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)

            payWithGoogle()
        }
    }

    private val paymentsClient: PaymentsClient by lazy {
        Wallet.getPaymentsClient(
            requireActivity(),
            Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                .build()
        )
    }


    private fun isReadyToPay() {
        paymentsClient.isReadyToPay(createIsReadyToPayRequest())
            .addOnCompleteListener { task ->
                try {
                    if (task.isSuccessful) {
                        // show Google Pay as payment option
                    } else {
                        // hide Google Pay as payment option
                    }
                } catch (exception: ApiException) {
                }
            }
    }

    private fun createIsReadyToPayRequest(): IsReadyToPayRequest {
        return IsReadyToPayRequest.fromJson(
            JSONObject()
                .put("allowedAuthMethods", JSONArray()
                    .put("PAN_ONLY")
                    .put("CRYPTOGRAM_3DS")
                )
                .put("allowedCardNetworks",
                    JSONArray()
                        .put("AMEX")
                        .put("DISCOVER")
                        .put("MASTERCARD")
                        .put("VISA")
                )
                .toString()
        )
    }


    private fun payWithGoogle() {
        val dataRequest = createPaymentDataRequest()
        dataRequest?.let {
            AutoResolveHelper.resolveTask(
                paymentsClient.loadPaymentData(it),
                requireActivity(),
                LOAD_PAYMENT_DATA_REQUEST_CODE
            )
        }
    }

    private fun createPaymentDataRequest(): PaymentDataRequest? {
        // create PaymentMethod
        val cardPaymentMethod = JSONObject()
            .put("type", "CARD")
            .put(
                "parameters",
                JSONObject()
                    .put("allowedAuthMethods", JSONArray()
                        .put("PAN_ONLY")
                        .put("CRYPTOGRAM_3DS"))
                    .put("allowedCardNetworks",
                        JSONArray()
                            .put("AMEX")
                            .put("DISCOVER")
                            .put("MASTERCARD")
                            .put("VISA"))

                    // require billing address
                    .put("billingAddressRequired", true)
                    .put(
                        "billingAddressParameters",
                        JSONObject()
                            // require full billing address
                            .put("format", "MIN")

                            // require phone number
                            .put("phoneNumberRequired", true)
                    )
            )
            .put(
                "tokenizationSpecification",
                GooglePayConfig("pk_test_51HB4LGJNvOQfnf0xJIEAvwNEVhKOweNq9SBMz8oj1pHLyfWfzakSELA3wZHSeTISUAfEQRaHso51veNgLQoKeT7r00lutcu77G").tokenizationSpecification
            )

        // create PaymentDataRequest
        val paymentDataRequest = JSONObject()
            .put("apiVersion", 2)
            .put("apiVersionMinor", 0)
            .put("allowedPaymentMethods",
                JSONArray().put(cardPaymentMethod))
            .put("transactionInfo", JSONObject()
                .put("totalPrice", "10.00")
                .put("totalPriceStatus", "FINAL")
                .put("currencyCode", "USD")
            )
            .put("merchantInfo", JSONObject()
                .put("merchantName", "Example Merchant"))

            // require email address
            .put("emailRequired", true)
            .toString()

        return PaymentDataRequest.fromJson(paymentDataRequest)
    }

    companion object {
        const val LOAD_PAYMENT_DATA_REQUEST_CODE = 53
    }

}