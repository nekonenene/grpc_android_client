package net.hatone.hello_grpc

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import android.widget.Button
import android.widget.TextView
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.examples.hello.*
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

class HelloGrpcTask constructor(activity: Activity) : AsyncTask<String, Void, String>() {
    private val activityReference: WeakReference<Activity> = WeakReference(activity)
    private var channel: ManagedChannel? = null

    override fun doInBackground(vararg params: String): String {
        val name = params[0]
        val age = params[1].toInt()
        val host = "grpc-test.hatone.net/"
        val port = 443

        return try {
            channel = ManagedChannelBuilder.forAddress(host, port).useTransportSecurity().build()
            val stub = GreeterGrpc.newBlockingStub(channel)
            val request = HelloRequest.newBuilder().setName(name).setAge(age).build()
            val reply: HelloReply = stub.sayHello(request)
            reply.message
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Failed to get message", e)

            val sw = StringWriter()
            val pw = PrintWriter(sw)
            e.printStackTrace(pw)
            pw.flush()
            String.format("Error occurred... : %n%s", sw)
        }

    }

    override fun onPostExecute(result: String) {
        try {
            channel!!.shutdown().awaitTermination(1, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }

        val activity = activityReference.get() ?: return
        val outputTextView: TextView = activity.findViewById(R.id.outputTextView)
        val sendButton: Button = activity.findViewById(R.id.sendButton)
        outputTextView.text = result
        sendButton.isEnabled = true
    }
}
