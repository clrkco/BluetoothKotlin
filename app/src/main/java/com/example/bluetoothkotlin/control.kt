package com.example.bluetoothkotlin

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log

import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.android.synthetic.main.content_control.*
import java.io.IOException
import java.util.*

class control : AppCompatActivity() {

    companion object{
        var myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var bluetoothSocket: BluetoothSocket? = null
        lateinit var progress: ProgressDialog
        lateinit var bluetoothAdapter: BluetoothAdapter
        var isConnected: Boolean = false
        lateinit var address: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)

        ConnectToDevice(this).execute()

        helloBtn.setOnClickListener{sendCommand("h") }
        armsBtn.setOnClickListener{sendCommand("a") }
        disconnectBtn.setOnClickListener{disconnect() }
    }

    private fun sendCommand(input:String){
        if(bluetoothSocket!=null){
            try{
                bluetoothSocket!!.outputStream.write(input.toByteArray())
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun disconnect(){
        if(bluetoothSocket!=null){
            try {
                bluetoothSocket!!.close()
                bluetoothSocket = null
                isConnected = false
            }catch(e: IOException){
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectToDevice(c: Context): AsyncTask<Void, Void,String>(){
        private var connectSuccess: Boolean = true
        private val context: Context

        init{
            this.context =c
        }
        override fun onPreExecute(){
            super.onPreExecute()
            progress = ProgressDialog.show(context, "Connecting","Please wait.")
        }

        override fun doInBackground(vararg p0: Void?): String?{
            try{
                if(bluetoothSocket == null || !isConnected){
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
                    bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    bluetoothSocket!!.connect()
                }
            }catch(e:IOException){
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!connectSuccess){
                Log.i("data","couldn't connect")
            } else {
                isConnected = true
            }
            progress.dismiss()
        }
    }
}
