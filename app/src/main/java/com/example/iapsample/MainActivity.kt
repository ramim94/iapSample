package com.example.iapsample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.stripe.android.model.Token
import org.json.JSONObject


class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }





    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            FirstFragment.LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val paymentData = PaymentData.getFromIntent(data!!)
                        val paymentDataObject = JSONObject(paymentData!!.toJson())
                        print(paymentDataObject)
                        // You can get some data on the user's card, such as the
                        // brand and last 4 digits
//                        val info = paymentData!!.cardInfo
                        // You can also pull the user address from the
                        // PaymentData object.
//                        val address = paymentData.shippingAddress
//                         This is the raw JSON string version of your Stripe token.
                        val rawToken = paymentDataObject
                            .getJSONObject("paymentMethodData")
                            .getJSONObject("tokenizationData")
                            .getString("token")

                        val tokenObject = JSONObject(rawToken)

                        // Now that you have a Stripe token object,
                        // charge that by using the id
//                        val stripeToken = Token.fromString(rawToken)
                        //paymentData.tok
                        val stripeToken = Token.fromJson(tokenObject)

                        print(stripeToken)
//                        if (stripeToken != null) {
                        // This chargeToken function is a call to your own
                        // server, which should then connect to Stripe's
                        // API to finish the charge.
                        // chargeToken(stripeToken.id)
//                        }
                    }
                    Activity.RESULT_CANCELED -> {
                    }
                    AutoResolveHelper.RESULT_ERROR -> {
                        // Log the status for debugging
                        // Generally there is no need to show an error to
                        // the user as the Google Payment API will do that
                        val status = AutoResolveHelper.getStatusFromIntent(data)
                    }
                    else -> {
                        // Do nothing.
                    }
                }
            }
            else -> {
                // Handle any other startActivityForResult calls you may have made.
            }
        }

    }
}