package com.example.task1

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.view.View
import android.webkit.DownloadListener
import android.webkit.URLUtil
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.app.DownloadManager as DownloadManager1

@Suppress("DEPRECATION")

class MainActivity : AppCompatActivity() {

    private var downloadReference: Long = 0
    lateinit var downloadManager: android.app.DownloadManager
    lateinit var context: Context
    lateinit var activity: MainActivity
    lateinit var downloadListener: DownloadListener
    var writeAcess = false
    var downloadPage=""
    private val PERMISSION_REQUEST_CODE=1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = applicationContext
        activity =this

        checkWriteAccess()
    }

    fun btnDownloadClickEvent(view: View) {
        downloadPage= etEnterURL.text.toString();
        Toast.makeText(this,"download will start",Toast.LENGTH_LONG).show()


      createDownloadListener()

      onDownloadComplete()
    }
    private fun checkWriteAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Toast.makeText(this,"starting download..1..",Toast.LENGTH_LONG).show()
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    val builder = AlertDialog.Builder(activity)
                    builder.setMessage("Required permission to write external storage to save downloaded file.")
                    builder.setTitle("Please Grant Write Permission")
                    builder.setPositiveButton("OK") { _, _ ->
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            PERMISSION_REQUEST_CODE
                        )
                    }
                    builder.setNeutralButton("Cancel", null)
                    val dialog = builder.create()
                    dialog.show()
                } else {

                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_CODE
                    )
                }
            } else {
                /**
                 * Already have required permission.
                 * */
                Toast.makeText(this,"cannot start download",Toast.LENGTH_LONG).show()
                writeAcess = false
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"starting download 2..",Toast.LENGTH_LONG).show()
                    writeAcess=true
                } else {
                    // Permission denied
                    writeAcess=false
                    Toast.makeText(context,"Permission Denied. This app will not work with right permission.",Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun createDownloadListener() {
        Toast.makeText(this,"starting download  3 .. ",Toast.LENGTH_LONG).show()
        //downloadListener = DownloadListener { url, userAgent, contentDescription, mimetype, contentLength ->
            val request = android.app.DownloadManager.Request(Uri.parse(downloadPage))
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
          //  val fileName = URLUtil.guessFileName(url, contentDescription, mimetype)
         //  request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            val dManager = getSystemService(Context.DOWNLOAD_SERVICE) as android.app.DownloadManager
            if (writeAcess)
                dManager.enqueue(request)
            else {
                Toast.makeText(context, "Unable to download", Toast.LENGTH_LONG).show()
                checkWriteAccess()
            }
        }
    
    private fun onDownloadComplete(){

        val onComplete = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                Toast.makeText(context,"File downloaded",Toast.LENGTH_LONG).show()
            }
        }
        registerReceiver(onComplete, IntentFilter(android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }
}


