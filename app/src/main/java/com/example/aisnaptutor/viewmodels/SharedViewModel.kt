package ke.derrick.imagetotext.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.text.Text.TextBlock

class SharedViewModel: ViewModel() {
    val textBlocks = MutableLiveData<List<TextBlock>>()
    var selectedImageUri: String? = null
    var camOrGal: Int? = 1
    var totalWords: Int? = 1
    var collectionText: String? = ""

    fun setTextBlocks(payload: List<TextBlock>) {
        textBlocks.value = payload
    }
    fun countWords(text: String): Int {
        val words = text.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
        totalWords = totalWords!! + words.size
        return words.size
    }
    fun setWords(){
        totalWords = 0
    }
}