package com.example.chatappgroupproject.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.example.chatappgroupproject.databinding.ActivitySignUpBinding
import com.example.chatappgroupproject.utilities.Constants
import com.example.chatappgroupproject.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    private lateinit var preferenceManager: PreferenceManager
    private var encodedImage : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        setListeners()
    }

    private fun setListeners() {
        binding.textSignIn.setOnClickListener{ onBackPressed() }
        binding.buttonSignUp.setOnClickListener{
            if(isValidSignUpDetails()) {
                signUp()
            }
        }
        binding.layoutImage.setOnClickListener{
            var intent : Intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }
    }

    private fun showToast(message : String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun signUp() {
        loading(true)
        var db = FirebaseFirestore.getInstance()
        var user = HashMap<String, String>()
        user[Constants.KEY_USERNAME] = binding.inputUsername.text.toString()
        user[Constants.KEY_EMAIL] = binding.inputEmail.text.toString()
        user[Constants.KEY_PASSWORD] = binding.inputPassword.text.toString()
        user[Constants.KEY_IMAGE] = encodedImage!!
        db.collection(Constants.KEY_COLLECTION_USERS)
            .add(user)
            .addOnSuccessListener{documentReference ->
                loading(false)
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                preferenceManager.putString(Constants.KEY_USER_ID, documentReference.id)
                preferenceManager.putString(Constants.KEY_USERNAME, binding.inputUsername.text.toString())
                preferenceManager.putString(Constants.KEY_IMAGE, encodedImage!!)
                var intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{ exception ->
                loading(false)
                if(exception.message != null) {
                    showToast(exception.message!!)
                }else {
                    showToast("Fail to add user to database")
                }
            }
    }

    private fun encodeImage(bitmap : Bitmap) : String {
        val previewWidth : Int = 150
        val previewHeight : Int = bitmap.height * previewWidth / bitmap.width
        val previewBitmap : Bitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        var byteArrayOutputStream : ByteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        var bytes : ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun getRoundedBitmapDrawable(originalBitmap: Bitmap) : RoundedBitmapDrawable {
        val roundedBitmapDrawable: RoundedBitmapDrawable = RoundedBitmapDrawableFactory.create(
            resources, originalBitmap
        )
        roundedBitmapDrawable.cornerRadius = 50.0f
        roundedBitmapDrawable.setAntiAlias(true)
        return roundedBitmapDrawable
    }

    private fun pickImageCallBack(res : ActivityResult) {
        if (res.resultCode == RESULT_OK) {
            if (res.data != null) {
                val imageUri: Uri = res.data!!.data!!
                try {
                    var inputStream: InputStream = contentResolver.openInputStream(imageUri)!!
                    var originalBitmap : Bitmap = BitmapFactory.decodeStream(inputStream)
                    var roundedBitmapDrawable : RoundedBitmapDrawable = getRoundedBitmapDrawable(originalBitmap)
                    binding.signUpProfileImage.setImageDrawable(roundedBitmapDrawable)
                    binding.textAddImage.visibility = View.GONE
                    encodedImage = encodeImage(originalBitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private val pickImage : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res -> pickImageCallBack(res) }

    private fun isValidSignUpDetails() : Boolean {
        if(encodedImage == null) {
            showToast("Select profile image")
            return false
        }else if(binding.inputUsername.text.toString().trim().isEmpty()) {
            showToast("Please enter username")
            return false
        }else if(binding.inputEmail.text.toString().trim().isEmpty()) {
            showToast("Please enter email")
            return false
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()) {
            showToast("Email invalid")
            return false
        }else if(binding.inputPassword.text.toString().trim().isEmpty()) {
            showToast("Please enter password")
            return false
        }else if(binding.inputConfirmPassword.text.toString().trim().isEmpty()) {
            showToast("Please confirm your password")
            return false
        }else if(!binding.inputPassword.text.toString().equals(binding.inputConfirmPassword.text.toString())) {
            showToast("Passwords don't match")
            return false
        }else {
            return true
        }
    }

    private fun loading(isLoading : Boolean) {
        if(isLoading) {
            binding.buttonSignUp.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        }else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.buttonSignUp.visibility = View.VISIBLE
        }
    }
}