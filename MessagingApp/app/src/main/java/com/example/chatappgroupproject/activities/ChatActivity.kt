package com.example.chatappgroupproject.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.chatappgroupproject.adapters.ChatAdapter
import com.example.chatappgroupproject.databinding.ActivityChatBinding
import com.example.chatappgroupproject.models.ChatMessage
import com.example.chatappgroupproject.models.User
import com.example.chatappgroupproject.network.ApiClient.client
import com.example.chatappgroupproject.network.ApiService
import com.example.chatappgroupproject.utilities.Constants
import com.example.chatappgroupproject.utilities.Constants.Companion.gtRemoteMsgHeaders
import com.example.chatappgroupproject.utilities.PreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

class ChatActivity : BaseActivity() {
    private var binding: ActivityChatBinding? = null
    private var receiverUser: User? = null
    private var chatMessages: MutableList<ChatMessage>? = null
    private var chatAdapter: ChatAdapter? = null
    private var preferenceManager: PreferenceManager? = null
    private var database: FirebaseFirestore? = null
    private var conversionId: String? = null
    private var isReceiverAvailable = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        setListeners()
        loadReceiverDetails()
        init()
        listenMessages()
    }

    private fun init() {
        preferenceManager = PreferenceManager(
            applicationContext
        )
        chatMessages = ArrayList()
        chatAdapter = ChatAdapter(
            chatMessages as ArrayList<ChatMessage>,
            getBitmapFromEncodedString(receiverUser!!.image)!!,
            preferenceManager!!.getString(Constants.KEY_USER_ID)!!
        )
        binding!!.chatRecyclerView.adapter = chatAdapter
        database = FirebaseFirestore.getInstance()
    }

    private fun sendMessage() {
        val message = HashMap<String, Any?>()
        message[Constants.KEY_SENDER_ID] = preferenceManager!!.getString(Constants.KEY_USER_ID)
        message[Constants.KEY_RECEIVER_ID] = receiverUser!!.id
        message[Constants.KEY_MESSAGE] = binding!!.inputMessage.text.toString()
        message[Constants.KEY_TIMESTAMP] = Date()
        database!!.collection(Constants.KEY_COLLECTION_CHAT).add(message)
        if (conversionId != null) {
            updateConversion(binding!!.inputMessage.text.toString())
        } else {
            var conversion = HashMap<String, Any?>()
            conversion[Constants.KEY_SENDER_ID] =
                preferenceManager!!.getString(Constants.KEY_USER_ID)
            conversion[Constants.KEY_SENDER_NAME] =
                preferenceManager!!.getString(Constants.KEY_USERNAME)
            conversion[Constants.KEY_SENDER_IMAGE] =
                preferenceManager!!.getString(Constants.KEY_IMAGE)
            conversion[Constants.KEY_RECEIVER_ID] = receiverUser!!.id
            conversion[Constants.KEY_RECEIVER_NAME] = receiverUser!!.name
            conversion[Constants.KEY_RECEIVER_IMAGE] = receiverUser!!.image
            conversion[Constants.KEY_LAST_MESSAGE] = binding!!.inputMessage.text.toString()
            conversion[Constants.KEY_TIMESTAMP] = Date()
            addConversion(conversion)
        }
        if (!isReceiverAvailable) {
            try {
                val tokens = JSONArray()
                tokens.put(receiverUser!!.token)
                val data = JSONObject()
                data.put(
                    Constants.KEY_USER_ID,
                    preferenceManager!!.getString(Constants.KEY_USER_ID)
                )
                data.put(
                    Constants.KEY_USERNAME,
                    preferenceManager!!.getString(Constants.KEY_USERNAME)
                )
                data.put(
                    Constants.KEY_FCM_TOKEN,
                    preferenceManager!!.getString(Constants.KEY_FCM_TOKEN)
                )
                data.put(Constants.KEY_MESSAGE, binding!!.inputMessage.text.toString())
                val body = JSONObject()
                body.put(Constants.REMOTE_MSG_DATA, data)
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)
                sendNotification(body.toString())
            } catch (exception: Exception) {
                Log.w("CHECK", "" + exception.message)
            }
        }
        binding!!.inputMessage.text = null
    }

    private fun showToast(message: String?) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun sendNotification(messageBody: String) {
        client!!.create(ApiService::class.java).sendMessage(
            gtRemoteMsgHeaders(),
            messageBody
        )!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    try {
                        if (response.body() != null) {
                            val responseJson = JSONObject(response.body())
                            val results = responseJson.getJSONArray("results")
                            if (responseJson.getInt("failure") == 1) {
                                val error = results[0] as JSONObject
                                showToast(error.getString("error"))
                                return
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    showToast("Notification sent successfully")
                } else {
                    showToast("Error: " + response.code())
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                showToast(t.message)
            }
        })
    }

    private fun listenMessages() {
        database!!.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(
                Constants.KEY_SENDER_ID,
                preferenceManager!!.getString(Constants.KEY_USER_ID)
            )
            .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser!!.id)
            .addSnapshotListener(eventListener)
        database!!.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser!!.id)
            .whereEqualTo(
                Constants.KEY_RECEIVER_ID,
                preferenceManager!!.getString(Constants.KEY_USER_ID)
            )
            .addSnapshotListener(eventListener)
    }

    private fun listenAvailabilityOfReceiver() {
        database!!.collection(Constants.KEY_COLLECTION_USERS).document(
            receiverUser!!.id
        )
            .addSnapshotListener(this@ChatActivity) { value: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    if (value.getLong(Constants.KEY_AVAILABILITY) != null) {
                        val availability = Objects.requireNonNull(
                            value.getLong(Constants.KEY_AVAILABILITY)
                        )?.toInt()
                        isReceiverAvailable = availability == 1
                    }

                    if (value.getString(Constants.KEY_FCM_TOKEN) != null) {
                        receiverUser!!.token = value.getString(Constants.KEY_FCM_TOKEN)!!
                        if (receiverUser!!.image == null) {
                            receiverUser!!.image = value.getString(Constants.KEY_IMAGE)!!
                            chatAdapter!!.setReceiverProfileImage(
                                getBitmapFromEncodedString(
                                    receiverUser!!.image
                                )!!
                            )
                            chatAdapter!!.notifyItemRangeChanged(0, chatMessages!!.size)
                        }
                    }
                }
                if (isReceiverAvailable) {
                    binding!!.textAvailability.visibility = View.VISIBLE
                } else {
                    binding!!.textAvailability.visibility = View.GONE
                }
            }
    }

    private val eventListener =
        EventListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
            if (error != null) {
                return@EventListener
            }
            if (value != null) {
                val count = chatMessages!!.size
                for (documentChange in value.documentChanges) {
                    if (documentChange.type == DocumentChange.Type.ADDED) {
                        val chatMessage = ChatMessage()
                        chatMessage.senderId =
                            documentChange.document.getString(Constants.KEY_SENDER_ID)
                        chatMessage.receiverId =
                            documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        chatMessage.message =
                            documentChange.document.getString(Constants.KEY_MESSAGE)
                        chatMessage.dateTime =
                            getReadableDateTime(documentChange.document.getDate(Constants.KEY_TIMESTAMP))
                        chatMessage.dateObject =
                            documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                        chatMessages!!.add(chatMessage)
                    }
                }
                chatMessages!!.sortWith { obj1: ChatMessage, obj2: ChatMessage ->
                    obj1.dateObject!!.compareTo(
                        obj2.dateObject
                    )
                }
                if (count == 0) {
                    chatAdapter!!.notifyDataSetChanged()
                } else {
                    chatAdapter!!.notifyItemRangeInserted(chatMessages!!.size, chatMessages!!.size)
                    binding!!.chatRecyclerView.smoothScrollToPosition(chatMessages!!.size - 1)
                }
                binding!!.chatRecyclerView.visibility = View.VISIBLE
            }
            binding!!.progressBar.visibility = View.GONE
            if (conversionId == null) {
                checkForConversion()
            }
        }

    private fun getBitmapFromEncodedString(encodedImage: String?): Bitmap? {
        return if (encodedImage != null) {
            val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else {
            null
        }
    }

    private fun loadReceiverDetails() {
        receiverUser = intent.getSerializableExtra(Constants.KEY_USER) as User?
        binding!!.textName.text = receiverUser!!.name
    }

    private fun setListeners() {
        binding!!.imageBack.setOnClickListener { v: View? -> onBackPressed() }
        binding!!.layoutSend.setOnClickListener { v: View? -> sendMessage() }
    }

    private fun getReadableDateTime(date: Date?): String {
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }

    private fun addConversion(conversion: HashMap<String, Any?>) {
        database!!.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .add(conversion)
            .addOnSuccessListener { documentReference: DocumentReference ->
                conversionId = documentReference.id
            }
    }

    private fun updateConversion(message: String) {
        val documentReference =
            database!!.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(
                conversionId!!
            )
        documentReference.update(
            Constants.KEY_LAST_MESSAGE, message,
            Constants.KEY_TIMESTAMP, Date()
        )
    }

    private fun checkForConversion() {
        if (chatMessages!!.size != 0) {
            checkForConversionRemotely(
                preferenceManager!!.getString(Constants.KEY_USER_ID),
                receiverUser!!.id
            )
            checkForConversionRemotely(
                preferenceManager!!.getString(Constants.KEY_USER_ID),
                receiverUser!!.id
            )
        }
    }

    private fun checkForConversionRemotely(senderId: String?, receiverId: String) {
        database!!.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
            .get()
            .addOnCompleteListener(conversationOnCompleteListener)
    }

    private val conversationOnCompleteListener = OnCompleteListener { task: Task<QuerySnapshot?> ->
        if (task.isSuccessful && task.result != null && task.result!!
                .documents.size > 0
        ) {
            val documentSnapshot = task.result!!.documents[0]
            conversionId = documentSnapshot.id
        }
    }

    override fun onResume() {
        super.onResume()
        listenAvailabilityOfReceiver()
    }
}