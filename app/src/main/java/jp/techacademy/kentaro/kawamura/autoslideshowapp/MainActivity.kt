package jp.techacademy.kentaro.kawamura.autoslideshowapp


import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.content.ContentUris
import android.net.Uri
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Handler
import android.view.View
import java.util.*


class MainActivity : AppCompatActivity(),View.OnClickListener {

    private val PERMISSIONS_REQUEST_CODE = 100
    val ImageList: ArrayList<Uri> = ArrayList<Uri>()  //クラス直下に変数を置けば、その変数をどこの関数でも使える。
    var Ind = 0


    private var mTimer: Timer? = null
    private var mHandler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
        } else {
            getContentsInfo()
        }

        Next.setOnClickListener(this)
        Back.setOnClickListener(this)
        StartStop.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.Next&&StartStop.text=="再生") {
            if (ImageList.size-1!=Ind){
            Ind++
            imageView.setImageURI(ImageList[Ind])
            }
            else{Ind=0
                imageView.setImageURI(ImageList[Ind])
            }
        }

        else if (v.id == R.id.Back&&StartStop.text=="再生") {
            if (Ind!=0){
            Ind--
            imageView.setImageURI(ImageList[Ind])
            }
            else{Ind=ImageList.size-1
                imageView.setImageURI(ImageList[Ind])
            }
        }


        else if (v.id == R.id.StartStop){
            if (mTimer == null){
                mTimer = Timer()
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {

                        if (ImageList.size-1!=Ind){
                            Ind++
                            mHandler.post {
                            imageView.setImageURI(ImageList[Ind])
                                StartStop.text="停止"

                            }
                        }
                        else {Ind=0
                            mHandler.post {
                                imageView.setImageURI(ImageList[Ind])
                                StartStop.text="停止"
                            }
                        }
                    }
                }, 2000, 2000)}
            else if (mTimer != null){
                mTimer!!.cancel()
                mTimer = null
                StartStop.text="再生"
            }
        }
    }







    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        val resolver = contentResolver
        val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目（null = 全項目）
                null, // フィルタ条件（null = フィルタなし）
                null, // フィルタ用パラメータ
                null // ソート (nullソートなし）
        )
        if (cursor!!.moveToFirst()) {
            do {
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                ImageList.add(imageUri)
            } while (cursor.moveToNext())
        }
        cursor.close()
        imageView.setImageURI(ImageList[Ind])
    }
}