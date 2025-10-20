package com.example.chatappgroupproject.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.example.chatappgroupproject.R
import com.example.chatappgroupproject.adapters.RecentConversationsAdapter
import com.example.chatappgroupproject.databinding.ActivityMainBinding
import com.example.chatappgroupproject.listeners.ConversionListener
import com.example.chatappgroupproject.models.ChatMessage
import com.example.chatappgroupproject.models.User
import com.example.chatappgroupproject.utilities.Constants
import com.example.chatappgroupproject.utilities.PreferenceManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging
import java.util.Collections
import java.util.Date
import java.util.Objects
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

class MainActivity : BaseActivity(), ConversionListener {
    private lateinit var binding : ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var conversations: MutableList<ChatMessage>
    private lateinit var conversationsAdapter: RecentConversationsAdapter
    private lateinit var database: FirebaseFirestore

    private lateinit var adView : AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        init()
        loadUserDetails()
        getToken()
        setListeners()
        listenConversations()

        createAd( )
    }

    private fun createAd( ) {
        adView = AdView( this )
        var adSize : AdSize = AdSize( AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT )
        adView.setAdSize( adSize )

        var adUnitId : String = // "ca-app-pub-3940256099942544/1033173712"
            "ca-app-pub-3940256099942544/6300978111"
        adView.adUnitId = adUnitId

        var builder : AdRequest.Builder = AdRequest.Builder( )
        builder.addKeyword( "fitness" ).addKeyword( "workout" )
        var request : AdRequest = builder.build()

        // add adView to linear layout
        var layout : LinearLayout = findViewById( R.id.ad_view )
        layout.addView( adView )

        // load the ad
        adView.loadAd( request )
    }

    private fun init() {
        conversations = ArrayList<ChatMessage>()
        conversationsAdapter = RecentConversationsAdapter(conversations, this)
        binding.conversationsRecyclerView.adapter = conversationsAdapter
        database = FirebaseFirestore.getInstance()
    }

    private fun setListeners() {
        binding.imageSignOut.setOnClickListener { signOut() }
        binding.fabNewChat.setOnClickListener {
            var intent : Intent = Intent(applicationContext,UsersActivity::class.java)
            startActivity(intent)
        }

    }
    private fun loadUserDetails() {
        binding.textName.text = preferenceManager.getString(Constants.KEY_USERNAME)
        var bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        binding.imageProfile.setImageBitmap(bitmap)
    }

    private fun showToast(message : String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun listenConversations() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }

    private val eventListener: EventListener<QuerySnapshot> = EventListener<QuerySnapshot>(){
            value, error ->
        if (error == null && value != null) {
            for (documentChange: DocumentChange in value.documentChanges) {
                if (documentChange.type == DocumentChange.Type.ADDED) {
                    var senderId: String? = documentChange.document.getString(Constants.KEY_SENDER_ID)
                    var receiverId: String? = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                    var chatMessage: ChatMessage = ChatMessage()
                    chatMessage.senderId = senderId
                    chatMessage.receiverId = receiverId
                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                        chatMessage.conversationImage = documentChange.document.getString(Constants.KEY_RECEIVER_IMAGE)
                        chatMessage.conversationName = documentChange.document.getString(Constants.KEY_RECEIVER_NAME)
                        chatMessage.conversationId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                    } else {
                        chatMessage.conversationImage = documentChange.document.getString(Constants.KEY_SENDER_IMAGE)
                        chatMessage.conversationName = documentChange.document.getString(Constants.KEY_SENDER_NAME)
                        chatMessage.conversationId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                    }
                    chatMessage.message = documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                    chatMessage.dateObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP)

                    conversations.removeIf { message -> message.conversationId.equals(chatMessage.conversationId) }
                    conversations.add(chatMessage)
                }else if(documentChange.type == DocumentChange.Type.MODIFIED) {
                    for (i: Int in conversations.indices) {
                        var senderId: String? = documentChange.document.getString(Constants.KEY_SENDER_ID)
                        var receiverId: String? = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        if((conversations[i].senderId.equals(senderId) && conversations[i].receiverId.equals(receiverId))
                            || (conversations[i].senderId.equals(receiverId) && conversations[i].receiverId.equals(senderId))) {
                            conversations[i].message = documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                            conversations[i].dateObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                            break
                        }
                    }
                }
            }
            Collections.sort(conversations) { obj1, obj2 -> obj2.dateObject!!.compareTo(obj1.dateObject!!) }

            conversationsAdapter.notifyDataSetChanged()
            binding.conversationsRecyclerView.smoothScrollToPosition(0)
            binding.conversationsRecyclerView.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            updateToken(token)
        }
    }
    private fun updateToken(token : String) {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token)
        var database = FirebaseFirestore.getInstance()
        var documentReference =
            preferenceManager.getString(Constants.KEY_USER_ID)?.let {
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                    it
                )
            }
        documentReference?.update(Constants.KEY_FCM_TOKEN, token)?.addOnFailureListener{ showToast("Unable to update token")}
    }

    private fun signOut() {
        showToast("Signing out...")
        val database = FirebaseFirestore.getInstance()
        val documentReference = preferenceManager.getString(Constants.KEY_USER_ID)?.let {
            database.collection(Constants.KEY_COLLECTION_USERS).document(
                it
            )
        }
        val updates = hashMapOf<String, Any>()
        updates[Constants.KEY_FCM_TOKEN] = FieldValue.delete()
        documentReference?.update(updates)?.addOnSuccessListener {
            //not sure how to translate .clear()
            preferenceManager.clear()
            startActivity(Intent(applicationContext, SignInActivity::class.java))
            this.overridePendingTransition(
                R.anim.fade_in, 0 )
            finish()
        }?.addOnFailureListener { showToast("Unable to sign out") }
    }

    override fun onConversionClicked(user: User?) {
        var intent: Intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER, user)
        startActivity(intent)
        this.overridePendingTransition(
            R.anim.slide_from_left, 0 )
    }


}