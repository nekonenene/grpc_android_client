package net.hatone.hello_grpc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_hello.*

class HelloActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello)

        sendButton.setOnClickListener { clickSendButton() }

        nameEditText.setText(R.string.default_name)
        ageEditText.setText(R.string.default_age)
        outputTextView.text = ""
    }

    private fun clickSendButton() {
        val name = nameEditText.text
        val age = ageEditText.text
        val str = "Name: $name, Age: $age"

        Log.d(localClassName, "clicked! $str")
        outputTextView.text = str

        HelloGrpcTask(application, generateCallback()).execute(name.toString(), age.toString())
    }

    private fun generateCallback(): HelloGrpcTask.HelloGrpcTaskCallback {
        return object : HelloGrpcTask.HelloGrpcTaskCallback {
            override fun onPreExecute() {
                sendButton.isEnabled = false
            }

            override fun onPostExecute(result: String) {
                outputTextView.text = result
                sendButton.isEnabled = true
            }

            override fun onCancelled() {
                sendButton.isEnabled = true
            }
        }
    }
}
