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
import com.bumptech.glide.Glide
import com.example.facilityshop.R
import com.example.facilityshop.model.CallProgressDialog
import com.example.facilityshop.model.Server
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_profile.*
import org.json.JSONArray

/*This activity use for normal user*/

class ProfileActivity : AppCompatActivity() {

    var imageUri: Uri? = null
    lateinit var progress: CallProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        progress = CallProgressDialog()

        editText_password_profile.isEnabled = false

        Paper.init(this@ProfileActivity)

        setToolbar()

        var intent = intent
        var phone = intent.getStringExtra("phoneNumber") // Get from HomeActivity
        var id = Paper.book().read("idUser", "null") // Get from HomeActivity

        loadUserProfile(Server.getUserInfo, phone)

        imageView_edit_image_profile.setOnClickListener {
            CropImage.activity(imageUri).setAspectRatio(1, 1)
                .start(this@ProfileActivity)
        }

        imageView_edit_password_profile.setOnClickListener {
            var intent = Intent(this@ProfileActivity, ChangePasswordActivity::class.java)
            intent.putExtra("phoneNumber", phone)
            intent.putExtra("password", editText_password_profile.text.toString())
            startActivity(intent)
        }

        imageView_save_profile.setOnClickListener {
            progress.showProgress(this@ProfileActivity,
                R.style.AppCompatAlertDialogStyle, "Updating your profile",
                "Please wait...")

            var fullName = editText_fullName.text.toString()
            var password = editText_password_profile.text.toString()
            var email = editText_email_profile.text.toString()
            var phoneNumber = editText_phoneNumber.text.toString()

            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phoneNumber)) {
                progress.dismissProgress()
                Toast.makeText(this@ProfileActivity, "Please fill full information !", Toast.LENGTH_SHORT)
                    .show()
            } else {
                var image = imageUri.toString()
                if (imageUri != null) {
                    updateImageAndInfo(Server.updateUserInfo, id, fullName, phoneNumber, password, email, image)
                } else {
                    updateAnotherInfo(Server.updateUserInfo2, id, fullName, phoneNumber, password, email)
                }

            }
        }

        textView_setup_securityQuestion.setOnClickListener {
            var intent = Intent(this@ProfileActivity, SecurityQuestionActivity::class.java)
            intent.putExtra("phoneNumber", phone)
            startActivity(intent)
        }
    }

    private fun updateAnotherInfo(url: String, id: String, fullName: String, phoneNumber: String, password: String,
                                  email: String) {
        var requestQueue = Volley.newRequestQueue(this@ProfileActivity)
        var stringRequest = object : StringRequest(Method.POST, url, object : Response.Listener<String>{

            override fun onResponse(response: String?) {
                if (response.equals("Success 1")) {
                    val handler = Handler()
                    handler.postDelayed(Runnable {
                        progress.dismissProgress()
                        Toast.makeText(this@ProfileActivity, "Update your profile success.", Toast.LENGTH_SHORT).show()
                    }, 1500)
                }
                else if (response.equals("Success 2")) {
                    val handler = Handler()
                    handler.postDelayed(Runnable {
                        progress.dismissProgress()
                        Toast.makeText(this@ProfileActivity, "Update your profile success.", Toast.LENGTH_SHORT).show()
                    }, 1500)
                }
                else {
                    Toast.makeText(this@ProfileActivity, "This phone number already exists, please " +
                            "choose another phone !", Toast.LENGTH_LONG).show()
                }
            }

        }, object: Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("Error", error.toString())
            }

        }){
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("id", id)
                hashMap.put("fullName", fullName)
                hashMap.put("phoneNumber", phoneNumber)
                hashMap.put("password", password)
                hashMap.put("email", email)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun updateImageAndInfo(url: String, id: String, fullName: String, phoneNumber: String, password: String,
                                   email: String, image: String) {
        var requestQueue = Volley.newRequestQueue(this@ProfileActivity)
        var stringRequest = object : StringRequest(Method.POST, url, object : Response.Listener<String>{

            override fun onResponse(response: String?) {
                if (response.equals("Success 1")) {
                    val handler = Handler()
                    handler.postDelayed(Runnable {
                        progress.dismissProgress()
                        Toast.makeText(this@ProfileActivity, "Update your profile success.", Toast.LENGTH_SHORT).show()
                    }, 1500)
                }
                else if (response.equals("Success 2")) {
                    val handler = Handler()
                    handler.postDelayed(Runnable {
                        progress.dismissProgress()
                        Toast.makeText(this@ProfileActivity, "Update your profile success.", Toast.LENGTH_SHORT).show()
                    }, 1500)
                }
                else {
                    Toast.makeText(this@ProfileActivity, "This phone number already exists, please " +
                            "choose another phone !", Toast.LENGTH_LONG).show()
                }
            }

        }, object: Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("Error", error.toString())
            }

        }){
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("id", id)
                hashMap.put("fullName", fullName)
                hashMap.put("phoneNumber", phoneNumber)
                hashMap.put("password", password)
                hashMap.put("email", email)
                hashMap.put("image", image)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
    }

    override fun onStart() {
        super.onStart()
        var newPassWord = Paper.book().read("newPassWord", editText_password_profile.text.toString()) // Get from ChangePasswordActivity after change password
        if (newPassWord != null) {
            editText_password_profile.text = Editable.Factory.getInstance().newEditable(newPassWord)
        } else {
            return
        }
    }

    private fun loadUserProfile(url: String, condition: String) {
        var requestQueue = Volley.newRequestQueue(this@ProfileActivity)
        var stringRequest = object : StringRequest(Method.POST, url, object : Response.Listener<String>{

            override fun onResponse(response: String?) {

                if (response != null) {
                    var jsonArray = JSONArray(response)

                    var fullName: String
                    var phoneNumber: String
                    var password: String
                    var email: String
                    var image: String

                    for (i in 0 until jsonArray.length()) {

                        var jsonObject = jsonArray.getJSONObject(i)
                        fullName = jsonObject.getString("fullName")
                        phoneNumber = jsonObject.getString("phoneNumber")
                        password = jsonObject.getString("password")
                        email = jsonObject.getString("email")
                        image = jsonObject.getString("image")

                        editText_fullName.text = Editable.Factory.getInstance().newEditable(fullName)
                        editText_phoneNumber.text = Editable.Factory.getInstance().newEditable(phoneNumber)
                        editText_password_profile.text = Editable.Factory.getInstance().newEditable(password)
                        editText_email_profile.text = Editable.Factory.getInstance().newEditable(email)

                        if (image.equals("null")) {
                            Picasso.get().load(R.drawable.user_icon).placeholder(R.drawable.user_icon).into(imageView_user_profile)
                        } else if (!image.equals("null")){
                            Glide.with(this@ProfileActivity).load(image).into(imageView_user_profile)
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
        setSupportActionBar(toolBar_profile)
        toolBar_profile.setNavigationIcon(R.drawable.ic_arrow_back)
        toolBar_profile.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK
            && data != null
        ) {
            var result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            imageUri = result.uri

            imageView_user_profile.setImageURI(imageUri)
        }
    }
}
