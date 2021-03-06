package com.bignerdranch.android.geoquiz

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var backButton: ImageButton
    private lateinit var cheatButton: Button
    private lateinit var apiText: TextView
    private lateinit var cheatsText: TextView

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate(Bundle?) called")

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text_view)
        backButton = findViewById(R.id.back_button)
        cheatButton = findViewById(R.id.cheat_button)
        apiText = findViewById(R.id.api_text_view)
        cheatsText = findViewById(R.id.cheats_text_view)

        apiText.setText(getString(R.string.api_text, Build.VERSION.SDK_INT))
        cheatsText.setText(getString(R.string.cheats_text, quizViewModel.cheatsLeft))

        trueButton.setOnClickListener { view: View ->
            if (!quizViewModel.checkIfDone()) {
                if (!quizViewModel.currentQuestionAnswered) {
                    quizViewModel.currentQuestionAnswered = true
                    checkAnswer(true)
                }
                if (quizViewModel.checkIfDone()) {
                    Toast.makeText(this, "Finished! "+quizViewModel.score+"/"+quizViewModel.amountOfQuestions, Toast.LENGTH_SHORT).show()
                }
            }

        }

        falseButton.setOnClickListener { view: View ->
            if (!quizViewModel.checkIfDone()) {
                if (!quizViewModel.currentQuestionAnswered) {
                    quizViewModel.currentQuestionAnswered = true
                    checkAnswer(false)
                }
                if (quizViewModel.checkIfDone()) {
                    Toast.makeText(
                        this,
                        "Finished! " + quizViewModel.score + "/" + quizViewModel.amountOfQuestions,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        nextButton.setOnClickListener { view: View ->
            quizViewModel.moveToNext()
            updateQuestion()
        }

        questionTextView.setOnClickListener { view: View ->
           quizViewModel.moveToNext()
            updateQuestion()
        }

        backButton.setOnClickListener { view: View ->
            quizViewModel.moveToPrevious()
            updateQuestion()
        }

        cheatButton.setOnClickListener { view: View ->
            if (quizViewModel.cheatsLeft < 1) return@setOnClickListener
            //val intent = Intent(this, CheatActivity::class.java)
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val options = ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
            } else {
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            }
        }

        updateQuestion()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            val answerShown: Boolean = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false

            quizViewModel.currentQuestionCheated = answerShown

            quizViewModel.cheatsLeft =
                if (answerShown) --quizViewModel.cheatsLeft else quizViewModel.cheatsLeft
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
        cheatsText.setText(getString(R.string.cheats_text, quizViewModel.cheatsLeft))
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

//        val messageResId = if (userAnswer == correctAnswer) {
//            R.string.correct_toast
//        } else {
//            R.string.incorrect_toast
//        }

        val messageResId = when {
            quizViewModel.currentQuestionCheated -> R.string.judgement_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        if (userAnswer == correctAnswer) quizViewModel.score++
    }


}