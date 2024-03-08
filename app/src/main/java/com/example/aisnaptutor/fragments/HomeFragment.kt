package com.example.aisnaptutor.fragments

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aisnaptutor.Message
import com.example.aisnaptutor.MessageAdapter
import com.example.aisnaptutor.OnSaveClick
import com.example.aisnaptutor.R
import com.example.aisnaptutor.databinding.BottomSheetLayoutBinding
import com.example.aisnaptutor.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.mlkit.vision.text.Text
import ke.derrick.imagetotext.viewmodels.SharedViewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment(), OnSaveClick {
    private val binding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: SharedViewModel
    companion object{
        val REQUEST_GALLERY = 2
    }
    lateinit var rotateAnimation:ObjectAnimator
    var messageList: MutableList<Message>? = null
    var messageAdapter: MessageAdapter? = null
    val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    var client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.SECONDS)
        .build()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        viewModel.textBlocks.observe(viewLifecycleOwner) {
            Log.d("MyCheck", "${it.size}")
            processTextRecognitionResult(it)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        messageList = ArrayList()

        messageAdapter = MessageAdapter(messageList!!, this)
        binding.recyclerView.adapter = messageAdapter
        val llm = LinearLayoutManager(requireActivity())
        llm.stackFromEnd = true
        binding.btnChatSend.visibility = View.GONE
        binding.recyclerView.layoutManager = llm

        //Opening camera for image
        binding.btnCamera.setOnClickListener {
            if (allPermissionsGranted()) {
                viewModel.camOrGal = 1
                findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
            } else {
                ActivityCompat.requestPermissions(
                    requireContext() as Activity,
                    CameraFragment.REQUIRED_PERMISSIONS,
                    CameraFragment.REQUEST_CODE_PERMISSIONS
                )
            }
        }

        binding.btnChatSend.setOnClickListener { v: View? ->
            if(binding.editQuestion.text.isNullOrBlank())
            {
                Toast.makeText(requireContext(), "Please insert text", Toast.LENGTH_SHORT).show()
            }
            val question = binding.editQuestion.text.toString().trim { it <= ' ' }
            addToChat(question, Message.SENT_BY_ME)
            binding.editQuestion.setText("")
            callAPI(question)
        }

        binding.editQuestion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateButtonVisibility(s.toString().isNotEmpty())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btnGallery.setOnClickListener {
            openGallery()
        }

        binding.btnDrawer.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_navigationFragment)
        }

        binding.btnRefresh.setOnClickListener {
            binding.editQuestion.setText("")
        }

        return binding.root
    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        if (requestCode == CameraFragment.REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {

            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                (requireContext() as Activity).finish()
            }
        }
    }

    private fun allPermissionsGranted() = CameraFragment.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }


    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_GALLERY -> {
                    val selectedImageUri: Uri? = data?.data
                    viewModel.selectedImageUri = selectedImageUri.toString()
                    viewModel.camOrGal = 2
                    findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
                }
            }
        }
    }

    // Function to update button visibility based on text presence
    fun updateButtonVisibility(hasText: Boolean) {
        binding.btnChatSend.visibility = if (hasText) View.VISIBLE else View.INVISIBLE
        binding.btnCamera.visibility = if (hasText) View.INVISIBLE else View.VISIBLE
        binding.btnGallery.visibility = if (hasText) View.INVISIBLE else View.VISIBLE
    }
    @SuppressLint("NotifyDataSetChanged")
    fun addToChat(message: String?, sentBy: String?) {
        requireActivity().runOnUiThread(Runnable {
            messageList!!.add(Message(message!!, sentBy.toString()))
            messageAdapter?.notifyDataSetChanged()
            binding.recyclerView!!.smoothScrollToPosition(messageAdapter!!.getItemCount())
        })
    }

    fun addResponse(response: String?) {
        messageList!!.removeAt(messageList!!.size - 1)
        addToChat(response, Message.SENT_BY_BOT)
    }

    fun callAPI(question: String?) {
        binding.imgLogoBack.alpha = 0.99f
        activity?.runOnUiThread {
            binding.imgLogoBack.alpha = 0.89f
            rotateAnimation = ObjectAnimator.ofFloat(binding.imgLogoBack, "rotation", 0f, 360f)
            rotateAnimation.duration = 2000
            rotateAnimation.start()
        }

        messageList!!.add(Message("typing...", Message.SENT_BY_BOT))
        val jsonBody = JSONObject()
        try {
            jsonBody.put("model", "gpt-3.5-turbo")
            val messageArr = JSONArray()
            val obj = JSONObject()
            obj.put("role", "user")
            obj.put("content", question + ":Provide concise answer")
            messageArr.put(obj)
            jsonBody.put("messages", messageArr)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        val body: RequestBody = RequestBody.create(JSON, jsonBody.toString())

        val request: Request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer ")
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                addResponse("Failed to load due to" + e.message)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                binding.imgLogoBack.alpha = 0.2f
                activity?.runOnUiThread{
                    rotateAnimation.cancel()
                }
                if (response.isSuccessful) {
                    var jsonObject: JSONObject? = null
                    try {
                        jsonObject = JSONObject(response.body?.string()!!)
                        val jsonArray = jsonObject.getJSONArray("choices")
                        val result = jsonArray.getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")
                        addResponse(result.trim { it <= ' ' })
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                } else {
                    addResponse("Failed to load due to " + response.body?.string())
                }
            }
        })
    }

    override fun onItemClick(outputText: String) {
        val bottomSheetDialog = BottomSheetDialog(requireActivity())

        // Inflate the layout for the bottom sheet dialog
        val bindingBottom = BottomSheetLayoutBinding.inflate(layoutInflater)

        bindingBottom.parentCopy.setOnClickListener {
            val clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", outputText)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(requireActivity(), "Text copied", Toast.LENGTH_SHORT).show()
            bottomSheetDialog.dismiss()
        }

        bindingBottom.parentShare.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, outputText)
            }
            startActivity(Intent.createChooser(sendIntent, "Share via"))
            bottomSheetDialog.dismiss()
        }

        bindingBottom.btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        // Set the content view of the bottom sheet dialog to the inflated layout
        bottomSheetDialog.setContentView(bindingBottom.root)
        bottomSheetDialog.show()
    }

    private fun processTextRecognitionResult(blocks: List<Text.TextBlock>) {
        var resultText = ""
        for (i in blocks.indices) {
            val lines: List<Text.Line> = blocks[i].lines
            for (j in lines.indices) {
                val elements: List<Text.Element> = lines[j].elements
                var lineOfText = ""
                for (k in elements.indices) {
                    lineOfText += elements[k].text + "  "
                    Log.d("MyCheck", elements[k].text)
                }
                lineOfText += "\n"
                resultText += lineOfText
            }
        }
        binding.editQuestion.setText(resultText)
    }

}