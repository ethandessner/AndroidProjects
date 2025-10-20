package com.example.chatappgroupproject.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.chatappgroupproject.adapters.UsersAdapter
import com.example.chatappgroupproject.databinding.ActivityUsersBinding
import com.example.chatappgroupproject.listeners.UserListener
import com.example.chatappgroupproject.models.User
import com.example.chatappgroupproject.utilities.Constants
import com.example.chatappgroupproject.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore

class UsersActivity : BaseActivity(), UserListener {
    private lateinit var binding: ActivityUsersBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        setListeners()
        getUsers()
    }

    private fun setListeners() {
        binding.imageBack.setOnClickListener { onBackPressed() }
    }

    private fun showErrorMessage() {
        binding.textErrorMessage.text = "No User available"
        binding.textErrorMessage.visibility = View.VISIBLE
    }

    private fun getUsers() {
        loading(true)
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .get()
            .addOnCompleteListener { task ->
                loading(false)
                val currentUserId = preferenceManager.getString(Constants.KEY_USER_ID)
                if (task.isSuccessful && task.result != null) {
                    val users = mutableListOf<User>()
                    for (queryDocumentSnapshot in task.result!!) {
                        if (currentUserId == queryDocumentSnapshot.id) {
                            continue
                        }
                        val user = User().apply {
                            name = queryDocumentSnapshot.getString(Constants.KEY_USERNAME).toString()
                            email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL).toString()
                            image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE).toString()
                            token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN).toString()
                            id = queryDocumentSnapshot.id
                        }
                        users.add(user)
                    }
                    if (users.size > 0) {
                        val usersAdapter = UsersAdapter(users, this)
                        binding.usersRecyclerView.adapter = usersAdapter
                        binding.usersRecyclerView.visibility = View.VISIBLE
                    } else {
                        showErrorMessage()
                    }
                } else {
                    showErrorMessage()
                }
            }
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onUserClicked(user: User?) {
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER, user)
        startActivity(intent)
        finish()
    }
}
