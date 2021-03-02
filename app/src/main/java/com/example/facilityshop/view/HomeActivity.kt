package com.example.facilityshop.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.navigation.NavigationView
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONArray
import org.json.JSONException
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.example.facilityshop.R
import com.example.facilityshop.model.*
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.paperdb.Paper


class HomeActivity : AppCompatActivity() {

    lateinit var googleSignInClient: GoogleSignInClient

    lateinit var listCategorys: ArrayList<Category>
    lateinit var listSellings: ArrayList<Product>
    lateinit var listLatests: ArrayList<Product>
    lateinit var listPromotions: ArrayList<Product>

    lateinit var categoryAdapter: CategoryAdapter

    companion object {
        lateinit var sellingAdapter: ProductAdapter
        lateinit var latestAdapter: ProductAdapter
        lateinit var promotionAdapter: ProductAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        Paper.init(this@HomeActivity)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this@HomeActivity, gso)

        setImageSlider()
        setNavigationView()
        getDataCategory()

        listSellings = ArrayList()
        listLatests = ArrayList()
        listPromotions = ArrayList()

        sellingAdapter = ProductAdapter(this@HomeActivity, listSellings)
        latestAdapter = ProductAdapter(this@HomeActivity, listLatests)
        promotionAdapter = ProductAdapter(this@HomeActivity, listPromotions)

        getDataProduct(Server.getProductByType, "Selling", listSellings, sellingAdapter, recycler_selling)
        getDataProduct(Server.getProductByType, "Latest", listLatests, latestAdapter, recycler_latest)
        getDataProduct(Server.getProductByType, "Promotion", listPromotions, promotionAdapter, recycler_promotion)

        text_search.isFocusable = false

        sendSeemoreContent()

        icon_cart.setOnClickListener {
            startActivity(Intent(this@HomeActivity, CartActivity::class.java))
        }

        getLoginInfo()

        text_search.setOnClickListener {
            startActivity(Intent(this@HomeActivity, SearchActivity::class.java))
        }

        refreshData()
    }

    private fun setImageSlider() {
        val sliderAdapter = SliderAdapter(this)

        imageSlider.setSliderAdapter(sliderAdapter)

        imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM)
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH)
        imageSlider.setIndicatorSelectedColor(Color.WHITE)
        imageSlider.setIndicatorUnselectedColor(Color.GRAY)
        imageSlider.setScrollTimeInSec(2) //set scroll delay in seconds
        imageSlider.startAutoCycle()
    }

    private fun refreshData() {
        swipeRefresh_layout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                swipeRefresh_layout.isRefreshing = true

                getDataCategory()

                listSellings = ArrayList()
                listLatests = ArrayList()
                listPromotions = ArrayList()

                sellingAdapter = ProductAdapter(this@HomeActivity, listSellings)
                latestAdapter = ProductAdapter(this@HomeActivity, listLatests)
                promotionAdapter = ProductAdapter(this@HomeActivity, listPromotions)

                getDataProduct(Server.getProductByType, "Selling", listSellings, sellingAdapter, recycler_selling)
                getDataProduct(Server.getProductByType, "Latest", listLatests, latestAdapter, recycler_latest)
                getDataProduct(Server.getProductByType, "Promotion", listPromotions, promotionAdapter, recycler_promotion)

                swipeRefresh_layout.isRefreshing = false
            }

        })
    }

    private fun getLoginInfo() {
        // Get info from LoginActivity
        var type = intent.getStringExtra("type")

        if (type == "normal") {
            var phone = intent.getStringExtra("phoneNumber") // get from LoginActivity
            loadUserProfile(Server.getUserInfo, phone)
            // Get user id
            var requestQueue = Volley.newRequestQueue(this@HomeActivity)
            var stringRequest = object : StringRequest(Method.POST, Server.getUserId, object : Response.Listener<String>{

                override fun onResponse(response: String?) {
                    if (response != null) {
                        var jsonArray = JSONArray(response)
                        var id = 0
                        for (i in 0 until jsonArray.length()) {
                            var jsonObject = jsonArray.getJSONObject(i)
                            id = jsonObject.getInt("id")
                        }
                        Paper.book().write("idUser", id.toString()) // Use for ProfileActivity and DetailProductActivity
                    }
                }

            }, object: Response.ErrorListener{
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("Error", error.toString())
                }

            }){
                override fun getParams(): MutableMap<String, String> {
                    var hashMap: HashMap<String, String> = HashMap()
                    hashMap.put("phoneNumber", phone)
                    return hashMap
                }
            }
            requestQueue.add(stringRequest)

        }

        else if (type == "facebook") {
            var id = intent.getStringExtra("idFb") // get from LoginActivity
            loadUserProfile(Server.getSocialUserInfo, id.toString())
            getUserIdOfSocial(Server.getUserId2, id)
        }

        else if (type == "google") {
            var id = intent.getStringExtra("idGoogle") // get from LoginActivity
            loadUserProfile(Server.getSocialUserInfo, id.toString())
            getUserIdOfSocial(Server.getUserId2, id)
        }
    }

    private fun sendSeemoreContent() {
        text_seemore_selling.setOnClickListener {
            var intent = Intent(this@HomeActivity, SeeMoreActivity::class.java)
            intent.putExtra("Seemore", "Selling")
            startActivity(intent)
        }

        text_seemore_latest.setOnClickListener {
            var intent = Intent(this@HomeActivity, SeeMoreActivity::class.java)
            intent.putExtra("Seemore", "Latest")
            startActivity(intent)
        }

        text_seemore_promotion.setOnClickListener {
            var intent = Intent(this@HomeActivity, SeeMoreActivity::class.java)
            intent.putExtra("Seemore", "Promotion")
            startActivity(intent)
        }
    }

    private fun getUserIdOfSocial(url: String, idSocial: String) {
        var requestQueue: RequestQueue = Volley.newRequestQueue(this@HomeActivity)
        var stringRequest = object : StringRequest(Method.POST, url, object : Response.Listener<String>{
            override fun onResponse(response: String?) {
                if (response != null) {

                    var jsonArray = JSONArray(response)

                    var id = ""

                    for (i in 0 until jsonArray.length()) {
                        try {
                            var jsonObject = jsonArray.getJSONObject(i)
                            id = jsonObject.getString("id")

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    Paper.book().write("idUser", id)
                }

            }

        }, object: Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("Error", error.toString())
            }

        }){
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("IdSocialNetwork", idSocial)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun getDataCategory() {
        var requestQueue = Volley.newRequestQueue(this@HomeActivity)
        var jsonArrayRequest = object :
            JsonArrayRequest(Server.getCategory, object : Response.Listener<JSONArray> {
                override fun onResponse(response: JSONArray?) {
                    if (response != null) {
                        var id: Int
                        var name: String
                        var image: String

                        listCategorys = ArrayList()

                        for (i in 0 until response.length()) {
                            try {
                                var jsonObject = response.getJSONObject(i)
                                id = jsonObject.getInt("id")
                                name = jsonObject.getString("name")
                                image = jsonObject.getString("image")

                                listCategorys.add(Category(id, name, image))

                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                        setRecyclerCategory(listCategorys)
                        categoryAdapter.notifyDataSetChanged()
                    }
                }

            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {

                }

            }){}

        requestQueue.add(jsonArrayRequest)

    }

    private fun getDataProduct(url: String, condition: String, list: ArrayList<Product>, adapter: ProductAdapter,
                               recyclerView: RecyclerView) {

        var requestQueue: RequestQueue = Volley.newRequestQueue(this@HomeActivity)
        var stringRequest = object : StringRequest(Method.POST, url, object : Response.Listener<String>{
            override fun onResponse(response: String?) {
                if (response != null) {
                    var jsonArray = JSONArray(response)
                    var id: Int
                    var category: String
                    var ptype: String
                    var pname: String
                    var price: Int
                    var description: String
                    var image1: String
                    var image2: String
                    var image3: String

                    for (i in 0 until jsonArray.length()) {
                        try {
                            var jsonObject = jsonArray.getJSONObject(i)
                            id = jsonObject.getInt("id")
                            category = jsonObject.getString("category")
                            ptype = jsonObject.getString("ptype")
                            pname = jsonObject.getString("pname")
                            price = jsonObject.getInt("price")
                            description = jsonObject.getString("description")
                            image1 = jsonObject.getString("image1")
                            image2 = jsonObject.getString("image2")
                            image3 = jsonObject.getString("image3")

                            list.add(Product(id, category, ptype, pname, price, description, image1, image2, image3))

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    setRecyclerProduct(recyclerView, list, adapter)
                    adapter.notifyDataSetChanged()
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

    private fun setRecyclerCategory(list: ArrayList<Category>) {
        recycler_category.setHasFixedSize(true)
        var layoutManager = LinearLayoutManager(
            this@HomeActivity, LinearLayoutManager.HORIZONTAL,
            false
        )
        recycler_category.layoutManager = layoutManager

        categoryAdapter = CategoryAdapter(this@HomeActivity, list)
        recycler_category.adapter = categoryAdapter

    }

    private fun setRecyclerProduct(recyclerView: RecyclerView, list: ArrayList<Product>, adapter: ProductAdapter) {
        recyclerView.setHasFixedSize(true)
        var layoutManager = GridLayoutManager(
            this@HomeActivity, 2
        )
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun setNavigationView() {
        var toggle = ActionBarDrawerToggle(
            this@HomeActivity, drawer_layout, toolbar_home,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = Color.parseColor("#FFFFFF")

        navView.setNavigationItemSelectedListener(object :
            NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(p0: MenuItem): Boolean {
                when (p0.itemId) {
                    R.id.nav_about -> {
                        startActivity(Intent(this@HomeActivity, AboutActivity::class.java))
                    }
                    R.id.nav_logout -> {
                        var type = intent.getStringExtra("type")
                        if (type == "normal") {
                            Paper.book().destroy()
                            var intent = Intent(this@HomeActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                        else if (type == "facebook") {
                            Paper.book().destroy()
                            LoginManager.getInstance().logOut()
                            Toast.makeText(this@HomeActivity, "Sign out success.", Toast.LENGTH_SHORT).show()
                            var intent = Intent(this@HomeActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        }
                        else if (type == "google") {
                            Paper.book().destroy()
                            googleSignInClient.signOut().addOnCompleteListener(this@HomeActivity) {
                                Toast.makeText(this@HomeActivity, "Sign out success.", Toast.LENGTH_SHORT).show()
                                var intent = Intent(this@HomeActivity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                                finish()
                            }
                        }
                    }

                    R.id.nav_profile -> {
                        // Get from LoginActivity
                        var type = intent.getStringExtra("type")
                        if (type == "normal") {
                            var intent = Intent(this@HomeActivity, ProfileActivity::class.java)
                            var phone = getIntent().getStringExtra("phoneNumber")
                            intent.putExtra("phoneNumber", phone)
                            startActivity(intent)
                        }
                        else if (type == "facebook") {
                            var intent = Intent(this@HomeActivity, Profile2Activity::class.java)
                            var id = getIntent().getStringExtra("idFb")
                            intent.putExtra("idSocial", id)
                            startActivity(intent)
                        }
                        else if (type == "google") {
                            var intent = Intent(this@HomeActivity, Profile2Activity::class.java)
                            var id = getIntent().getStringExtra("idGoogle")
                            intent.putExtra("idSocial", id)
                            startActivity(intent)
                        }
                    }

                    R.id.nav_favourite -> {
                        startActivity(Intent(this@HomeActivity, FavouriteActivity::class.java))
                    }

                    R.id.nav_history -> {
                        startActivity(Intent(this@HomeActivity, HistoryActivity::class.java))
                    }
                }
                drawer_layout.closeDrawer(GravityCompat.START)
                return true
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
        System.exit(0)
    }

    override fun onStart() {
        super.onStart()

        recycler_selling.adapter = sellingAdapter
        sellingAdapter.notifyDataSetChanged()

        recycler_latest.adapter = latestAdapter
        latestAdapter.notifyDataSetChanged()

        recycler_promotion.adapter = promotionAdapter
        promotionAdapter.notifyDataSetChanged()

        var type = intent.getStringExtra("type")
        if (type == "normal") {
            var phone = intent.getStringExtra("phoneNumber")
            loadUserProfile(Server.getUserInfo, phone)
        }
        else if (type == "facebook") {
            var id = intent.getStringExtra("idFb")
            loadUserProfile(Server.getSocialUserInfo, id)
        }
        else if (type == "google") {
            var id = intent.getStringExtra("idGoogle")
            loadUserProfile(Server.getSocialUserInfo, id)
        }

        // Send info for CompletePaymentActivity
        var pref: SharedPreferences.Editor = getSharedPreferences("dataPref", Context.MODE_PRIVATE).edit()
        pref.putString("type2", type)
        if (type == "normal") {
            var phone = intent.getStringExtra("phoneNumber")
            pref.putString("phoneCP", phone)
            pref.apply()
        }
        if (type == "facebook") {
            var id = intent.getStringExtra("idFb")
            pref.putString("id", id)
            pref.apply()
        }
        if (type == "google") {
            var id = intent.getStringExtra("idGoogle")
            pref.putString("id", id)
            pref.apply()
        }
    }

    private fun loadUserProfile(url: String, condition: String) {
        var requestQueue = Volley.newRequestQueue(this@HomeActivity)
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

                        // Set info for image and 2 textView of navigation view
                        var headerView = navView.getHeaderView(0)
                        var image_user_nav = headerView.findViewById<CircleImageView>(R.id.image_user_nav)
                        var text_user_name_nav = headerView.findViewById<TextView>(R.id.text_user_name_nav)
                        var text_user_email_nav = headerView.findViewById<TextView>(R.id.text_user_email_nav)

                        //image_user_nav.setImageResource(R.drawable.user_icon)
                        if (image.equals("null")) {
                            Picasso.get().load(R.drawable.user_icon).into(image_user_nav)
                        } else if (!image.equals("null")){
                            Picasso.get().load(image).into(image_user_nav)
                        }
                        text_user_name_nav.text = fullName
                        text_user_email_nav.text = email
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
}
