package com.android.galleryexample

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import android.util.Base64
import android.util.Log
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {


    private var bitmapMap: ArrayList<Bitmap>? = null
    private lateinit var addfab: FloatingActionButton
    private lateinit var donefab: FloatingActionButton
    private lateinit var recycler: RecyclerView
    private var mindex: Int? = null
    private var permarray = ArrayList<String>()
    private var perm = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler = findViewById(R.id.recycler)

        addfab = findViewById(R.id.add)
        donefab = findViewById(R.id.done)

        addfab.setOnClickListener(this)
        donefab.setOnClickListener(this)

        bitmapMap = ArrayList()

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.add -> {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 200)

                } else {
                    startCapture(100)
                }
            }
            R.id.done -> {
                checkPerm()

                if (perm) {
                    requestPermission(permarray)
                } else {
                    if (bitmapMap!!.size > 0) {
                        /*Toast.makeText(this, "size : ${bitmapMap!!.size}", Toast.LENGTH_SHORT).show()

                        val str = encodeImageToString(bitmapMap!![0])
                        Log.d("ssltag", str)*/
                        var file = createFile()
                        writeFile(file)
                    } else {
                        Toast.makeText(this, "No Bitmaps available", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun checkPerm() {
        permarray.clear()
        perm = false
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permarray.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            perm = true
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permarray.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            perm = true
        }
    }

    private fun requestPermission(permarray: ArrayList<String>) {
        ActivityCompat.requestPermissions(this, permarray.toTypedArray(), 100)
    }


    private fun startCapture(position: Int) {
        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), position)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            200 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startCapture(100)
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "Acess camera permission is required to take images", Toast.LENGTH_SHORT).show()
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return
            }

            100 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (bitmapMap!!.size > 0) {
                        /*Toast.makeText(this, "size : ${bitmapMap!!.size}", Toast.LENGTH_SHORT).show()

                        val str = encodeImageToString(bitmapMap!![0])
                        Log.d("ssltag", str)*/
                        var file = createFile()
                        writeFile(file)
                    } else {
                        Toast.makeText(this, "No Bitmaps available", Toast.LENGTH_SHORT).show()
                    }
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "Storage  permission is required to take save pdf", Toast.LENGTH_SHORT).show()
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return
            }
        // Add other 'when' lines to check for other
        // permissions this app might request.

            else -> {
                // Ignore all other requests.
            }
        }

    }

    private fun refreshRecycler() {
        recycler.invalidate()
        recycler.setHasFixedSize(true)
        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.adapter = ImageBitmapAdapter(this, layoutInflater, bitmapMap)
    }


    fun remove(position: Int) {
        bitmapMap!!.removeAt(position)
        refreshRecycler()
    }

    fun update(position: Int) {
        mindex = position
        startCapture(300)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val extras = data!!.extras
            val imageBitmap = extras.get("data") as Bitmap
            if (imageBitmap != null) {
                when (requestCode) {
                    100 -> {
                        if (bitmapMap!!.size > 0) {
                            bitmapMap!!.add(imageBitmap)
                        } else {
                            bitmapMap!!.add(imageBitmap)
                        }
                        refreshRecycler()
                    }

                    300 -> {
                        bitmapMap!![mindex!!] = imageBitmap
                        refreshRecycler()
                    }
                }
            } else {
                Toast.makeText(this, "No Image Captured", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No Image Captured", Toast.LENGTH_SHORT).show()
        }
    }

    /*  private fun encodeImageToString(bitmap: Bitmap?): String {
          var bitmap = bitmap
          val bao = ByteArrayOutputStream()
          val quality = 100 //100: compress nothing
          bitmap!!.compress(Bitmap.CompressFormat.JPEG, quality, bao)

          if (bitmap != null) {//important! prevent out of memory
              bitmap.recycle()
              bitmap = null
          }

          val ba = bao.toByteArray()
          return Base64.encodeToString(ba, Base64.URL_SAFE)
      }*/


    private fun createFile(): File {

        //First Check if the external storage is writable
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED != state) {
            Toast.makeText(this, "Media Storage not available", Toast.LENGTH_SHORT).show()
        }

        //Create a directory for your PDF
        val pdfDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "gallery_lm")
        if (!pdfDir.exists()) {
            pdfDir.mkdir()
        }


        //Now create the name of your PDF file that you will generate
        return File(pdfDir, "v1.pdf")
    }

    private fun writeFile(myFile: File) {

        val output = FileOutputStream(myFile)
        //Step 1
        val document = Document()
        //Step 2
        PdfWriter.getInstance(document, output)

        //Step 3
        document.open()
        document.pageSize = PageSize.A4
        for (i in 0..(bitmapMap!!.size - 1)) {
            val bmp = bitmapMap!![i]
            val baos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 0, baos)
            val b = baos.toByteArray()
            val image = Image.getInstance(b)
            image.setAbsolutePosition(0f,0f)
//            image.scaleToFit(PageSize.A4.width, PageSize.A4.height)
            image.scaleAbsolute(PageSize.A4.width, PageSize.A4.height)
            document.add(image)
            document.newPage()
        }

        document.close()
        output.close()

        Toast.makeText(this, "Pdf sucessfully created", Toast.LENGTH_SHORT).show()
    }

}
