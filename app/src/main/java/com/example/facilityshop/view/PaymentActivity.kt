package com.example.facilityshop.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.facilityshop.R
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentConfirmation
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_payment.*
import java.math.BigDecimal

class PaymentActivity : AppCompatActivity() {

    companion object {
        val PAYPAL_CLIENT_ID = "AXAh5jLAljy7IeVF-MnCgO9iougiWDY654Awa_OsfqUkoBMEmf-EyscbdX75RZeWxsb6ekgOX0fG4KXq"
        val PAYPAL_REQUEST_CODE = 1234
        var config: PayPalConfiguration = PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PAYPAL_CLIENT_ID)
    }
    var total = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        Paper.init(this@PaymentActivity)

        total = Paper.book().read("total", "") // Get from CartActivity

        textView_continue.setOnClickListener {
            if (radioButton_paypal.isChecked) {
                var paypalPayment = PayPalPayment(BigDecimal(total), "USD", "Donate for FacilityShopping",
                    PayPalPayment.PAYMENT_INTENT_SALE)

                var intent = Intent(this@PaymentActivity, com.paypal.android.sdk.payments.PaymentActivity::class.java)
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
                intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, paypalPayment)
                startActivityForResult(intent, PAYPAL_REQUEST_CODE)
            }
            if (radioButton_email.isChecked) {
                startActivity(Intent(this@PaymentActivity, PhonePaymentActivity::class.java))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this@PaymentActivity, PayPalService::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYPAL_REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {

                var confirmation: PaymentConfirmation = data!!.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity
                    .EXTRA_RESULT_CONFIRMATION)

                if (confirmation != null) {
                    var intent = Intent(this@PaymentActivity, CompletePaymentActivity::class.java)
                    intent.putExtra("type", "paypal")
                    intent.putExtra("amount", total)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)

                }
            } else if (requestCode == Activity.RESULT_CANCELED){
                Toast.makeText(this@PaymentActivity, "Cancel", Toast.LENGTH_SHORT).show()
            }
        } else if (resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(this@PaymentActivity, "Invalid", Toast.LENGTH_SHORT).show()
        }
    }
}
