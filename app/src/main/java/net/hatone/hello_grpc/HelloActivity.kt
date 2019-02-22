package net.hatone.hello_grpc

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_hello.*

class HelloActivity : AppCompatActivity() {
    lateinit var inputMethodManager: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello)

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        sendButton.setOnClickListener { onClickSendButton() }

        nameEditText.setText(R.string.default_name)
        ageEditText.setText(R.string.default_age)
        outputTextView.text = ""
    }

    private fun onClickSendButton() {
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        val name = nameEditText.text
        val age = ageEditText.text
        Log.d(localClassName, "Clicked! (Name: $name, Age: $age)")

        if (!validateForms()) {
            return
        }

        outputTextView.text = getString(R.string.connecting)

        HelloGrpcTask(application, generateCallback()).execute(name.toString(), age.toString())
    }

    private fun validateForms(): Boolean {
        var isOK = true

        if (nameEditText.text.isEmpty()) {
            nameEditText.error = getString(R.string.please_input_x, getString(R.string.name))
            isOK = false
        }
        if (ageEditText.text.isEmpty()) {
            ageEditText.error = getString(R.string.please_input_x, getString(R.string.age))
            isOK = false
        }

        return isOK
    }

    private fun generateCallback(): HelloGrpcTask.HelloGrpcTaskCallback {
        return object: HelloGrpcTask.HelloGrpcTaskCallback {
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
