package net.hatone.hello_grpc

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.examples.hello.*
import java.util.concurrent.TimeUnit

class HelloGrpcTask constructor(private val app: Application, private val callback: HelloGrpcTaskCallback) : AsyncTask<String, Void, String>() {
    private var channel: ManagedChannel? = null

    interface HelloGrpcTaskCallback {
        fun postExecute(result: String)
    }

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

        callback.postExecute(result)
    }
}
