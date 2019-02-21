package net.hatone.hello_grpc

import android.app.Activity
import android.app.Application
import android.os.AsyncTask
import android.util.Log
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.examples.hello.*
import kotlinx.android.synthetic.main.activity_hello.*
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

class HelloGrpcTask constructor(activity: Activity) : AsyncTask<String, Void, String>() {
    private val activityReference: WeakReference<Activity> = WeakReference(activity)
    private val app: Application = activity.application
    private var channel: ManagedChannel? = null

    override fun doInBackground(vararg params: String): String {
        val name = params[0]
        val age = params[1].toInt()
        val host = "grpc-test.hatone.net"
        val port = 443

        return try {
            channel = ManagedChannelBuilder.forAddress(host, port).useTransportSecurity().build()
            val stub = GreeterGrpc.newBlockingStub(channel)
            val request = HelloRequest.newBuilder().setName(name).setAge(age).build()
            val reply: HelloReply = stub.sayHello(request)

            Log.d(javaClass.simpleName, reply.toString())
            reply.message
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Failed to get message", e)
            app.getString(R.string.error_occurred)
        }
    }

    override fun onPostExecute(result: String) {
        try {
            channel!!.shutdown().awaitTermination(1, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }

        val activity = activityReference.get() ?: return
        activity.outputTextView.text = result
        activity.sendButton.isEnabled = true
    }
}
