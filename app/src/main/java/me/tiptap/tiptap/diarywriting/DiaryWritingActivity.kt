package me.tiptap.tiptap.diarywriting

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.location.*
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import gun0912.tedbottompicker.TedBottomPicker
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.ActivityDiaryWritingBinding
import java.io.IOException
import java.util.*


open class DiaryWritingActivity : AppCompatActivity()  {

    private lateinit var binding : ActivityDiaryWritingBinding
    private var locationManager : LocationManager? = null
    private var checkLocationOnce = true

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diary_writing)

        binding.textComplete.setOnClickListener { finish() }
        binding.btnBack.setOnClickListener { finish() }

        getFormattedDate(binding)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION)
        binding.textLocation.setOnClickListener {

            val alert = AlertDialog.Builder(this@DiaryWritingActivity)
            alert.setTitle(R.string.location_confirm)

            val input = EditText(this@DiaryWritingActivity)
            alert.setView(input)

            alert.setNegativeButton(R.string.cancel, null)
            alert.setPositiveButton(R.string.ok) { _, _ ->
                val place = input.text.toString()
                // Do something with value!
                if (place != "")
                    binding.textLocation.text = place
            }
            alert.show()
        }

        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if(permissionCheck == PackageManager.PERMISSION_GRANTED && checkLocationOnce) {
            checkLocationOnce = false
            Log.d("request", "no permission")
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
            val provider = locationManager?.getBestProvider(Criteria(), true)

            val locations = locationManager?.getLastKnownLocation(provider)
            val providerList = locationManager?.allProviders

            try {
                // Request location updates
                locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener);
            } catch(ex: SecurityException) {
                Log.d("myTag", "Security Exception, no location available");
            }

        } else {
            Log.d("request", "permission")
            val permission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, permission,0)
        }




        binding.imgGallery.setOnClickListener { _ ->
            /*val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1) */
            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)


            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permission,0)
                // 권한 없음
            } else {
                // 권한 있음
                val tedBottomPicker = TedBottomPicker.Builder(this@DiaryWritingActivity)
                        .setOnImageSelectedListener {
                            // here is selected uri
                            val matrix = ColorMatrix()
                            matrix.setSaturation(0.0F)
                            val filter = ColorMatrixColorFilter(matrix)

                            val inputStream  = contentResolver.openInputStream(it)
                            var dr = Drawable.createFromStream(inputStream, it.toString())

                            binding.imgMyPicture.colorFilter = filter
                            binding.imgMyPicture.layoutParams.width = 500
                            binding.imgMyPicture.layoutParams.height = 500
                            binding.imgMyPicture.setImageDrawable(dr)
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                            }
                            else
                                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                        }
                        .create()

                tedBottomPicker.show(supportFragmentManager)
            }
        }


        binding.editDiaryWrite.inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        binding.editDiaryWrite.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.textKeyboard.text = p0?.length.toString() + "/800"
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.textKeyboard.text = "0/800"
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.textKeyboard.text = p0
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                if(data !=null){
                    try{
                        val bitmap:Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data )
                        binding.imgMyPicture.setImageBitmap(bitmap)
                    }catch (e:IOException){
                        e.printStackTrace()
                    }
                }else if(resultCode == Activity.RESULT_CANCELED){
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private val locationListener:LocationListener = object:LocationListener {
        override fun onLocationChanged(location: Location) {
            val geocoder = Geocoder(applicationContext, Locale.getDefault())
            val listAddresses  = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if(null != listAddresses && listAddresses.size > 0) {
                val Location = listAddresses[0].getAddressLine(0)
                val str:String = Location.toString().substring(Location.toString().indexOf(" "))

                binding.textLocation.text = str
            }
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }


}
