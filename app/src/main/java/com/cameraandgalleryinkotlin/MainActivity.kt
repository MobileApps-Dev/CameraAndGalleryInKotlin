package com.cameraandgalleryinkotlin

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {

    lateinit var btn_pick_picture: Button
    lateinit var img_display: ImageView
    var REQUEST_CODE: Int = 11
    var CAMERA_CODE: Int = 156
    var GALLERY_CODE: Int = 158

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // declare view id
        btn_pick_picture = findViewById(R.id.btn_pick_picture) as Button
        img_display = findViewById(R.id.img_display) as ImageView

        // check runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionsDetails() // declare permission
        }

        btn_pick_picture.setOnClickListener(View.OnClickListener {
            selectImage() // open alert dialog
        })
    }

    // display alert dialog
    private fun selectImage() {
        var items = arrayOf<CharSequence>("Camera", "Gallery", "Cancel") // array list

        var builderdialog = AlertDialog.Builder(this@MainActivity)
        builderdialog.setTitle("Choose Options")

        builderdialog.setItems(items, DialogInterface.OnClickListener { dialog, item ->

            if (items[item].equals("Camera")) { // open camera
                cameraClick() // open default camera
            } else if (items[item].equals("Gallery")) { // open gallery
                galleryClick() // open default gallery
            } else if (items[item].equals("Cancel")) { // close dialog
                dialog.dismiss()
            }
        })
        builderdialog.show() // show dialog
    }

    // open default gallery
    private fun galleryClick() {
        try {
            // pick local image
            var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY_CODE)
        } catch (e: Exception) {
        }
    }

    // open default camera
    private fun cameraClick() {
        try {
            // pick capture image
            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_CODE)
        } catch (e: Exception) {
        }
    }

    // runtime permission
    private fun getPermissionsDetails() {
        var cameraFlag: Int = 0
        var galleryFlag: Int = 0
        var permissionCountFlag: Int = 0
        var permissionCountNewFlag: Int = 0
        var flag: Int = 0

        // permissions
        var camerapermission = ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.CAMERA)
        var gallerypermission = ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            cameraFlag = 1
            permissionCountFlag += 1
            flag = 1
        }
        if (gallerypermission != PackageManager.PERMISSION_GRANTED) {
            galleryFlag = 1
            permissionCountFlag += 1
            flag = 1
        }

        var countArrayList = arrayOfNulls<String>(permissionCountFlag)

        if (cameraFlag == 1) {
            countArrayList[permissionCountNewFlag] = android.Manifest.permission.CAMERA
            permissionCountNewFlag += 1
        }

        if (galleryFlag == 1) {
            countArrayList[permissionCountNewFlag] = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            permissionCountNewFlag += 1
        }

        if (flag == 1) {
            ActivityCompat.requestPermissions(
                    this@MainActivity,
                    countArrayList,
                    REQUEST_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, dataImage: Intent?) {
        super.onActivityResult(requestCode, resultCode, dataImage)

        if (requestCode == CAMERA_CODE) { // camera code
            try {// get bitmap of image
                var bitmap = dataImage!!.getExtras().get("data") as Bitmap
                var byteAray = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteAray)

                //convert bitmap to string
                var path: String = MediaStore.Images.Media.insertImage(this.contentResolver, bitmap, "TItle", null)
                // check bitmap image size
                var height: Int = bitmap.height
                var width: Int = bitmap.width

                // Display Normal image
                Glide.with(this).load(path).into(img_display)
            } catch (e: Exception) {
            }

            //img_display.setImageBitmap(bitmap)
//            Toast.makeText(this, path, Toast.LENGTH_SHORT).show()
//            Log.e("h*w", height.toString() + "\n" + width)

        } else if (requestCode == GALLERY_CODE) { // gallery code
            var cursor: Cursor
            try {
                //get selected image path url
                var selectImageView: Uri = dataImage!!.data
                var filePath = arrayOf(MediaStore.Images.Media.DATA)
                //fetch data from gallery
                cursor = contentResolver.query(selectImageView, filePath, null, null, null)
                cursor.moveToFirst()
                // get path of selected image
                var path: String = cursor.getString(cursor.getColumnIndex(filePath[0]))

                // Display Normal image
                Glide.with(this).load(path).into(img_display)

                // Display Circle image
//                Glide.with(this).load(path).asBitmap().into(object : BitmapImageViewTarget(img_display){
//                    override fun setResource(resource: Bitmap?) {
//                        //super.setResource(resource)
//                        val circularBitmapDrawable =RoundedBitmapDrawableFactory.create(resources,resource)
//                        circularBitmapDrawable.isCircular=true
//                        img_display.setImageDrawable(circularBitmapDrawable)
//                    }
//                })

                cursor.close()
            } catch (e: Exception) {
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
