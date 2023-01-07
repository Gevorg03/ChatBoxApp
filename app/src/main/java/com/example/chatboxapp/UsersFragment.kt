package com.example.chatboxapp

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatboxapp.databinding.FragmentUsersBinding
import com.google.firebase.database.*
import kotlinx.coroutines.*


class UsersFragment : Fragment() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var binding: FragmentUsersBinding? = null
    private lateinit var adapter: UserAdapter
    private val lst: MutableList<Users> = mutableListOf()
    private lateinit var recyclerViewer: RecyclerView
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference("users");

        return FragmentUsersBinding.inflate(inflater, container, false)
            .also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        coroutineScope.launch {
            val data = async { getData() }

            withContext(Dispatchers.Main) {
                data.await()
                recyclerViewer = binding!!.recyclerView
                println(lst)
                adapter = UserAdapter(lst)
                recyclerViewer.adapter = adapter
                recyclerViewer.layoutManager = LinearLayoutManager(context)
            }
        }
    }

    private fun getData() {
        val start = System.currentTimeMillis()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    val fullname = ds.child("username").value.toString()
                    val phone = ds.child("phone").value.toString()
                    val user = Users(fullname, phone)
                    lst.add(user)
                }
                println("list is $lst")
                val end = System.currentTimeMillis()
                println("Time is ${end - start}")
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}