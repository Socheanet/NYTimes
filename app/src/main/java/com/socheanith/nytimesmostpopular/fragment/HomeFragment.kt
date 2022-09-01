package com.socheanith.nytimesmostpopular.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.socheanith.nytimesmostpopular.R
import com.socheanith.nytimesmostpopular.adaptor.HomePageRecyclerAdaptor
import com.socheanith.nytimesmostpopular.checkConnectivity.ConnectionManager
import com.socheanith.nytimesmostpopular.model.News
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var progrssLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdaptor: HomePageRecyclerAdaptor
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var searchView: SearchView

    val newsInfo = arrayListOf<News>()
    val tempInfo = arrayListOf<News>()

    //use for date sorting
    var dateComparator = Comparator<News>{news1, news2 ->
        if( news1.date.compareTo(news2.date, true) ==0){
            news1.tile.compareTo(news2.tile,true)
        }else{
            news1.date.compareTo(news2.date, true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true) // tell fragment that it has menu in toolbar

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        progrssLayout = view.findViewById(R.id.progress_layout)
        progressBar = view.findViewById(R.id.progress_bar)
        recyclerView = view.findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(activity)

        //setting up the threads while fetching data from api
        progrssLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity)
        val url = "https://api.nytimes.com/svc/mostpopular/v2/viewed/7.json?api-key=E5Bi17AVGcLogJr4oSsKXwoGUNnlEXqO"
        if(ConnectionManager().checkConnectivity(activity as Context)){
            progrssLayout.visibility = View.GONE
            progressBar.visibility = View.GONE
            val jsonObjectRequest = object: JsonObjectRequest(Method.GET, url,null, Response.Listener {
                try{
                    //check is status is OK same as api
                    val status = it.getString("status")
                    if(status == "OK"){
                        val num = it.getInt("num_results")
                        val result = it.getJSONArray("results")
                        for(i in 0 until num){
                            val newsJsonObject = result.getJSONObject(i)
                            val newsObject = News(
                                newsJsonObject.getString("title"),
                                newsJsonObject.getString("byline"),
                                newsJsonObject.getString("published_date"),
                                newsJsonObject.getString("url"),
                                newsJsonObject.getString("uri")
                            )
                            //get every single data to newsInfo
                            newsInfo.add(newsObject)

                        }
                        //create a tempInfo for easy using search view
                        tempInfo.addAll(newsInfo)
                        //sending tempInfo to recycler view
                        recyclerAdaptor = HomePageRecyclerAdaptor(activity as Context, tempInfo)
                        recyclerView.adapter = recyclerAdaptor
                        recyclerView.layoutManager = layoutManager
                    }
                    //incase there are errors toasts will appear
                    else{
                        Toast.makeText(context, "Some Unexpected Error!1", Toast.LENGTH_SHORT).show()
                    }
                }catch (e:Exception){
                    Toast.makeText(context, "Some Unexpected Error!", Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                Toast.makeText(context, "Some Error Occurred!", Toast.LENGTH_SHORT).show()
            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["Content-type"] = "application/json"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)

        }else{
            //dialog message will pop up if there is no Internet connection
            val dialog = AlertDialog.Builder(activity)
            dialog.setTitle("No Internet Connection")
            dialog.setMessage("Please Check Your Internet Connection")
            dialog.setPositiveButton("GoTo Setting"){text, listener ->
                val settingIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){text, listener ->
                activity?.finish()
            }
            dialog.create()
            dialog.show()
        }

        return view
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //inflate action search in toolbar
        inflater?.inflate(R.menu.serch, menu)
        val item = menu?.findItem(R.id.search)
        val searchView = item?.actionView as SearchView
        searchView.queryHint = "Search Here"    //hint
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempInfo.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault()) //prevent uppercase letter from the user
                if(searchText.isNotEmpty()){
                    newsInfo.forEach {
                        //if it contains the text from the user's input
                        if(it.tile.toLowerCase(Locale.getDefault()).contains(searchText)){
                            //add that to temp
                            tempInfo.add(it)
                        }
                    }
                    //notify the recycler view
                    recyclerView.adapter!!.notifyDataSetChanged()
                }else{
                    tempInfo.clear()
                    tempInfo.addAll(newsInfo)
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                return false
            }

        })
        //inflate action sort menu
        inflater?.inflate(R.menu.sort,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item?.itemId
        if (id == R.id.search) {
            Toast.makeText(context, "Search is Activated",Toast.LENGTH_SHORT).show()

        }
        else if(id == R.id.sort){
            Toast.makeText(context, "Sort by Date is Activated",Toast.LENGTH_SHORT).show()
            val id = item?.itemId
            //perform sorting by date
            if(id==R.id.sort){
                Collections.sort(tempInfo, dateComparator)
                tempInfo.reverse()

            }

            recyclerAdaptor.notifyDataSetChanged()
            return super.onOptionsItemSelected(item)
        }

        return super.onOptionsItemSelected(item)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}