package com.example.facilityshop.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.R
import com.example.facilityshop.model.CallProgressDialog
import com.example.facilityshop.model.Server
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_profile2.*
import org.json.JSONArray

/*This activity use for facebook and google user info*/

class Profile2Activity : AppCompatActivity() {

    var imageUri: Uri? = null
    lateinit var progress: CallProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile2)

        progress = CallProgressDialog()

        setToolbar()

        var id = intent.getStringExtra("idSocial") // get from HomeActivity
        loadUserProfile(Server.getSocialUserInfo, id)

        imageView_edit_image_profile2.setOnClickListener {
            CropImage.activity(imageUri).setAspectRatio(1, 1)
                .start(this@Profile2Activity)
        }

        imageView_save_profile2.setOnClickListener {
            progress.showProgress(this@Profile2Activity,
                R.style.AppCompatAlertDialogStyle, "Updating your profile",
                "Please wait...")

            var fullName = editText_fullName2.text.toString()
            var email = editText_email_profile2.text.toString()

            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email)) {
                progress.dismissProgress()
                Toast.makeText(this@Profile2Activity, "Please fill full information !", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (imageUri != null) {
                    var image = imageUri.toString()
                    updateImageAndInfo(Server.updateUserInfo3, id, fullName, email, image)
                } else {
                    updateAnotherInfo(Server.updateUserInfo4, id, fullName, email)
                }
            }
        }
    }

    private fun updateAnotherInfo(url: String, id: String, fullName: String, email: String) {
        var requestQueue = Volley.newRequestQueue(this@Profile2Activity)
        var stringRequest = object : StringRequest(Method.POST, url, object : Response.Listener<String>{
            override fun onResponse(response: String?) {
                if (response.equals("Success")) {
                    val handler = Handler()
                    handler.postDelayed(Runnable {
                        progress.dismissProgress()
                        Toast.makeText(this@Profile2Activity, "Update your profile success.", Toast.LENGTH_SHORT).show()
                    }, 1500)
                }
            }

        }, object: Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("Error", error.toString())
            }

        }){
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("IdSocialNetwork", id)
                hashMap.put("fullName", fullName)
                hashMap.put("email", email)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun updateImageAndInfo(url:String, id: String, fullName: String, email: String,image: String) {
        var requestQueue = Volley.newRequestQueue(this@Profile2Activity)
        var stringRequest = object : StringRequest(Method.POST, url, object : Response.Listener<String>{
            override fun onResponse(response: String?) {
                if (response.equals("Success")) {
                    val handler = Handler()
                    handler.postDelayed(Runnable {
                        progress.dismissProgress()
                        Toast.makeText(this@Profile2Activity, "Update your profile success.", Toast.LENGTH_SHORT).show()
                    }, 1500)
                }
            }

        }, object: Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("Error", error.toString())
            }

        }){
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("IdSocialNetwork", id)
                hashMap.put("fullName", fullName)
                hashMap.put("email", email)
                hashMap.put("image", image)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK
            && data != null
        ) {
            var result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            imageUri = result.uri

            imageView_user_profile2.setImageURI(imageUri)
        }
    }

    private fun loadUserProfile(url: String, condition: String) {
        var requestQueue = Volley.newRequestQueue(this@Profile2Activity)
        var stringRequest = object : StringRequest(Method.POST, url, object : Response.Listener<String>{
            override fun onResponse(response: String?) {

                if (response != null) {
                    var jsonArray = JSONArray(response)

                    var fullName: String
                    var email: String
                    var image: String

                    for (i in 0 until jsonArray.length()) {

                        var jsonObject = jsonArray.getJSONObject(i)
                        fullName = jsonObject.getString("fullName")
                        email = jsonObject.getString("email")
                        image = jsonObject.getString("image")

                        editText_fullName2.text = Editable.Factory.getInstance().newEditable(fullName)

                        editText_email_profile2.text = Editable.Factory.getInstance().newEditable(email)

                        if (image.equals("null")) {
                            Picasso.get().load(R.drawable.user_icon).placeholder(R.drawable.user_icon).into(imageView_user_profile2)
                        } else if (!image.equals("null")){
                            Picasso.get().load(image).into(imageView_user_profile2)
                        }
                    }
                }
            }

        }, object: Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("Error", error.toString())
            }

        }){
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("condition", condition)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun setToolbar() {
        setSupportActionBar(toolBar_profile2)
        toolBar_profile2.setNavigationIcon(R.drawable.ic_arrow_back)
        toolBar_profile2.setNavigationOnClickListener {
            finish()
        }
    }
}
