package com.example.facilityshop.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.R
import com.example.facilityshop.model.CallProgressDialog
import com.example.facilityshop.model.Server
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap


class LoginActivity : AppCompatActivity() {

    lateinit var callbackManager: CallbackManager
    var RC_SIGN_IN = 0
    lateinit var progress: CallProgressDialog
    lateinit var mGoogleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        intent.removeExtra("type")

        textView_forgot_pass.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }

        progress = CallProgressDialog()

        button_login_login.setOnClickListener {
            var phoneNumber = editText_phone_login.text.toString()
            var password = editText_pass_login.text.toString()

            if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(password)) {
                Toast.makeText(
                    this@LoginActivity,
                    "Please fill full information !",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                progress.showProgress(
                    this@LoginActivity,
                    R.style.AppCompatAlertDialogStyle, "Login account",
                    "Please wait when we login your account..."
                )
                verifyLogin(phoneNumber, password)
            }

        }

        // Facebook initialization
        fb_button.setReadPermissions(Arrays.asList("email", "public_profile"))
        callbackManager = CallbackManager.Factory.create();
        fb_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                tokenTracker
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
                Toast.makeText(this@LoginActivity, error.toString(), Toast.LENGTH_LONG).show()
            }

        })

        button_fb_signin.setOnClickListener {
            fb_button.performClick()
        }

        // Google initialization
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this@LoginActivity, gso)

        button_google_signin.setOnClickListener {
            signInGoogle()
        }

        Paper.init(this@LoginActivity)

        textView_create_newAccount.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }


    // Check normal user login
    private fun verifyLogin(phoneNumber: String, password: String) {
        //region handle
        var requestQueue = Volley.newRequestQueue(this@LoginActivity)
        var stringRequest = object : StringRequest(
            Method.POST,
            Server.login,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    if (response.equals("Successfull")) {
                        val handler = Handler()
                        handler.postDelayed(Runnable {
                            progress.dismissProgress()

                            // Save session login
                            if (checkBox_remember.isChecked) {
                                Paper.book().write("sessionPhone", phoneNumber)
                                Paper.book().write("sessionPassword", password)
                            }

                            var intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            intent.putExtra("type", "normal")
                            intent.putExtra("phoneNumber", phoneNumber)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)

                        }, 2000)

                    } else {
                        val handler = Handler()
                        handler.postDelayed(Runnable {
                            progress.dismissProgress()
                            Toast.makeText(
                                this@LoginActivity, "Phone number or password not correct !",
                                Toast.LENGTH_LONG
                            ).show()
                        }, 2000)

                    }
                }

            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("Error", error.toString())
                }

            }) {
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("phoneNumber", phoneNumber)
                hashMap.put("password", password)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
        //endregion
    }

    private fun loadUserFBProfile(newAccessToken: AccessToken) {
        var request = GraphRequest.newMeRequest(
            newAccessToken,
            object : GraphRequest.GraphJSONObjectCallback {
                override fun onCompleted(`object`: JSONObject?, response: GraphResponse?) {
                    var first_name = `object`!!.getString("first_name")
                    var last_name = `object`.getString("last_name")
                    var email = `object`.getString("email")
                    var id = `object`.getString("id")
                    var image_url = "https://graph.facebook.com/" + id + "/picture?type=normal"
                    var full_name = first_name + " " + last_name

                    insertFbUser(full_name, email, image_url, id)

                    // Save session login
                    if (checkBox_remember.isChecked) {
                        Paper.book().write("sessionIdFacebook", id)
                    }

                    var intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    var type = "facebook"
                    intent.putExtra("type", type)
                    intent.putExtra("idFb", id)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }



            })

        var parameters = Bundle()
        parameters.putString("fields", "first_name, last_name, email, id")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun insertFbUser(
        fullName: String,
        email: String,
        image: String,
        IdSocialNetwork: String
    ) {
        //region handle
        var requestQueue = Volley.newRequestQueue(this@LoginActivity)
        var stringRequest = object : StringRequest(
            Method.POST,
            Server.fbUserInfo,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    if (response.equals("Successfull")) {
                        return
                    } else {
                        return
                    }
                }

            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("Error insert", error.toString())
                }

            }) {
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("fullName", fullName)
                hashMap.put("email", email)
                hashMap.put("image", image)
                hashMap.put("IdSocialNetwork", IdSocialNetwork)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
        //endregion
    }

    // TokenTracker for Facebook account
    var tokenTracker = object : AccessTokenTracker() {
        override fun onCurrentAccessTokenChanged(
            oldAccessToken: AccessToken?,
            currentAccessToken: AccessToken?
        ) {
            if (currentAccessToken == null) {
            } else {
                loadUserFBProfile(currentAccessToken)
            }
        }
    }

    private fun signInGoogle() {
        var signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // for google
        if (requestCode == RC_SIGN_IN) {

            var task =
                GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account :GoogleSignInAccount? = task.getResult(ApiException::class.java)

                if (account != null) {
                    var name = account.displayName
                    var email = account.email
                    var photoUri = account.photoUrl
                    var id = account.id
                    var strPhoto: String = photoUri.toString()

                    insertGoogleUser(name, email, strPhoto, id.toString())

                    var intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.putExtra("type", "google")
                    intent.putExtra("idGoogle", id)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()

                    // Save session login
                    if (checkBox_remember.isChecked) {
                        Paper.book().write("sessionIdGoogle", id)
                    }
                }
            } catch (e: ApiException) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Toast.makeText(this@LoginActivity, e.toString(), Toast.LENGTH_LONG).show()
                Log.e("Google Error","signInResult:failed code=" + e.statusCode)
            }
            //handleSignInResult(task)

        }

        // for facebook
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }


    private fun insertGoogleUser(
        name: String?,
        email: String?,
        strPhoto: String,
        IdSocialNetwork: String
    ) {
        //region handle
        var requestQueue = Volley.newRequestQueue(this@LoginActivity)
        var stringRequest = object : StringRequest(
            Method.POST,
            Server.googleUserInfo,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    if (response.equals("Successfull")) {
                        return
                    } else {
                        return
                    }
                }

            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("Error insert", error.toString())
                }

            }) {
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("fullName", name!!)
                hashMap.put("email", email!!)
                hashMap.put("image", strPhoto)
                hashMap.put("IdSocialNetwork", IdSocialNetwork)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
        //endregion
    }


}