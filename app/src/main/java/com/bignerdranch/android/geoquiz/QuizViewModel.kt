package com.bignerdranch.android.geoquiz

import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {


    var done = false
    var score = 0
    var currentIndex = 0
    var cheatsLeft = 3

    private val questionBank = listOf(
        Question(R.string.question_australia, true, false, false),
        Question(R.string.question_oceans, true, false, false),
        Question(R.string.question_mideast, false, false, false),
        Question(R.string.question_africa, false, false, false),
        Question(R.string.question_americas, true, false, false),
        Question(R.string.question_asia, true, false, false)
    )
    val amountOfQuestions = questionBank.size


    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    var currentQuestionAnswered: Boolean
        get() = questionBank[currentIndex].answered
        set(answered) { questionBank[currentIndex].answered = answered }

    var currentQuestionCheated: Boolean
        get() = questionBank[currentIndex].cheated
        set(cheated) { questionBank[currentIndex].cheated = cheated }

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrevious() {
        currentIndex = if (currentIndex == 0) questionBank.size - 1 else (currentIndex - 1)
    }

    fun checkIfDone(): Boolean {
        if (done) return true
        for (question: Question in questionBank) {
            if (!question.answered) {
                return false
            }
        }
        done = true
        return true
    }
}