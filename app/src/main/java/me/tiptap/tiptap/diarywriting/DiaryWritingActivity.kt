package me.tiptap.tiptap.diarywriting

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.location.places.Place
import com.google.gson.JsonObject
import com.taskail.googleplacessearchdialog.SimplePlacesSearchDialog
import com.taskail.googleplacessearchdialog.SimplePlacesSearchDialogBuilder
import gun0912.tedbottompicker.TedBottomPicker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_diary_writing.view.*
import me.tiptap.tiptap.R
import me.tiptap.tiptap.common.network.DiaryApi
import me.tiptap.tiptap.common.network.ServerGenerator
import me.tiptap.tiptap.common.rx.RxBus
import me.tiptap.tiptap.data.Diary
import me.tiptap.tiptap.databinding.ActivityDiaryWritingBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.annotations.NotNull
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class DiaryWritingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiaryWritingBinding
    private var locationManager: LocationManager? = null
    private var checkLocationOnce = true

    val isPhotoAvailable = ObservableField<Boolean>(false)

    private var imgUri: Uri? = null //image uri
    private var diary = Diary() //diary to send

    private val service: DiaryApi = ServerGenerator.createService(DiaryApi::class.java) //service
    private val rxBus = RxBus.getInstance()
    private val disposables: CompositeDisposable = CompositeDisposable()


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diary_writing)

        checkBus() //넘겨진 데이터가 있는지 확인

        binding.apply {
            activity = this@DiaryWritingActivity

            btnBack.setOnClickListener { finish() }
            textWriteKeyboard.text = getString(R.string.text_length, 0.toString())
            textWriteDate.text = SimpleDateFormat("yyyy MMM dd - hh:mm", Locale.US).format(Date())

            textWriteLocation.setOnClickListener { _ ->
                SimplePlacesSearchDialogBuilder(this@DiaryWritingActivity)
                        .setLocationListener(object : SimplePlacesSearchDialog.PlaceSelectedCallback {
                            override fun onPlaceSelected(@NotNull place: Place) {
                                binding.textWriteLocation.text = place.name
                            }
                        }).build().show()
            }
        }

        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (permissionCheck == PackageManager.PERMISSION_GRANTED && checkLocationOnce) {
            if (binding.textWriteLocation.text == "위치설정") {
                Log.d("request", "no permission")
                locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

                try {
                    // Request location updates
                    locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener);
                } catch (ex: SecurityException) {
                    Log.d("myTag", "Security Exception, no location available");
                }
            } else {
                Log.d("permission", "Hello")
            }

        } else {
            Log.d("request", "permission")
            val permission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, permission, 0)
        }



        binding.imgWriteGallery.setOnClickListener { _ ->
            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)


            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permission, 0)
                // 권한 없음
            } else {
                // 권한 있음
                val tedBottomPicker = TedBottomPicker.Builder(this@DiaryWritingActivity)
                        .setOnImageSelectedListener {
                            imgUri = it //set img uri
                            // here is selected uri
                            binding.imgWriteMyPicture.run {
                                setImageURI(it)
                                isPhotoAvailable.set(true)
                            }

                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N)
                                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                            else
                                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                        }
                        .create()

                tedBottomPicker.show(supportFragmentManager)
            }
        }

        binding.editWriteDiary.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(str: CharSequence, p1: Int, p2: Int, p3: Int) {
                str.length.apply {
                    if (this > 0) {
                        binding.toolbarWrite.text_complete.setTextColor(ContextCompat.getColor(this@DiaryWritingActivity, R.color.colorMainBlack))
                    } else {
                        binding.toolbarWrite.text_complete.setTextColor(ContextCompat.getColor(this@DiaryWritingActivity, R.color.colorMainGray))
                    }
                }
                binding.textWriteKeyboard.text = getString(R.string.text_length, str?.length.toString())
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
                        binding.imgWriteMyPicture.setImageBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            //set latitude, longitude
            diary.run {
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()
            }

            val geocoder = Geocoder(applicationContext, Locale.getDefault())
            val listAddresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (null != listAddresses && listAddresses.size > 0) {
                val location = listAddresses[0].getAddressLine(0)
                val strKor: String = location.toString().substring(location.toString().indexOf(" "))

                if (binding.textWriteLocation.text == "위치설정") {
                    binding.textWriteLocation.text = strKor

                    diary.location = strKor //set location
                }

            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    //Check its edited or not
    private fun checkBus() {
        disposables.add(rxBus.toObservable().subscribe {
            if (it is String) {
                binding.count = it
            }
        })
    }


    /**
     * When user click Complete button.
     */
    fun onCompleteButtonClick() {
        writeDiary()
    }


    /**
     * Get User access token.
     */
    private fun getAccessToken(): String {
        val sharedPref = getSharedPreferences(getString(R.string.auth), Activity.MODE_PRIVATE)
                ?: return ""
        val token = sharedPref.getString(getString(R.string.token), "")

        if (token.isBlank()) throw IllegalStateException("Invalid token form.") else return token
    }

    /**
     * convert object to RequestBody
     */
    private fun toRequestBody(content: String): RequestBody =
            RequestBody.create(MediaType.parse("text/plain"), content)


    /**
     * convert image file to MultipartBody.Part
     */
    private fun toMultipartBody(uri: Uri?): MultipartBody.Part? {
        uri?.let {
            val file = File(it.path)
            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)

            return MultipartBody.Part.createFormData(diary::diaryFile.name, file.name, requestFile)
        }
        return null
    }


    /**
     * Call write diary api.
     */
    private fun writeDiary() {
        disposables.add(service.writeDiary(
                getAccessToken(),
                toRequestBody(diary.content),
                toRequestBody(diary.location),
                toRequestBody(diary.latitude),
                toRequestBody(diary.longitude),
                toMultipartBody(imgUri))

                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object : DisposableObserver<JsonObject>() {
                    override fun onNext(t: JsonObject) {
                        //do something
                    }

                    override fun onComplete() {
                        //call startActivity is allocated a lot of memories.
                        finish()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                }))
    }


    override fun onDestroy() {
        super.onDestroy()

        disposables.dispose()
    }
}



