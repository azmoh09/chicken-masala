package com.example.chickenmasala.ui.screen
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.chickenmasala.R
import com.example.chickenmasala.data.CsvDataSource
import com.example.chickenmasala.data.domain.GuessGamesName
import com.example.chickenmasala.data.domain.QuestionGames
import com.example.chickenmasala.data.interactors.GuessGamesInteractor
import com.example.chickenmasala.data.utils.RecipeParser
import com.example.chickenmasala.databinding.FragmentGuessTheMealBinding
import com.example.chickenmasala.util.Constants
import kotlinx.coroutines.*

class GuessTheGameFragment(private val gameName: GuessGamesName) :
    BaseFragment<FragmentGuessTheMealBinding>() {
    override val LOG_TAG: String = "GuessTheMealFragment"
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGuessTheMealBinding =
        FragmentGuessTheMealBinding::inflate
    private lateinit var guessTheMeal: QuestionGames
    private var selectedChoiceId: Int = 0
    private var yellowColor: Int? = null
    private var grayColor: Int? = null
    private var greenColor: Int? = null
    private var redColor: Int? = null
    private var multiChoices: List<TextView>? = null
    override fun setup() {
        val recipeParser = RecipeParser()
        val dataSource =
            CsvDataSource(requireContext(), Constants.RECIPES_CSV_FILE_NAME, recipeParser)
        guessTheMeal = GuessGamesInteractor(dataSource).guessGames(gameName)
        multiChoices =
            listOf(
                binding.answerOneText,
                binding.answerTwoText,
                binding.answerThreeText,
                binding.answerFourText
            )
        showGame()
    }

    private fun showGame() {
        prepareColors()
        prepareMultiChoices()
        prepareQuestion()
        setButtonStatus(binding.submitButton, false)
        prepareNextQuestion(clickChoicesState = true)
    }

    override fun addCallBacks() {
        setColorOfSelectedChoice()
        submitAnswer()
    }

    private fun prepareColors() {
        yellowColor = ContextCompat.getColor(requireContext(), R.color.yellow_600)
        grayColor = ContextCompat.getColor(requireContext(), R.color.gray_200)
        greenColor = ContextCompat.getColor(requireContext(), R.color.green_100)
        redColor = ContextCompat.getColor(requireContext(), R.color.red_100)
        setUpChoicesColor()
    }

    private fun setUpChoicesColor() {
        multiChoices?.forEach { choice ->
            colorizeTheView(choice, grayColor!!)
        }
    }

    private fun prepareMultiChoices() {
        val listOfChoices = getRandomArrangeOfChoices()
        binding.apply {
            answerOneText.text = listOfChoices[0]
            answerTwoText.text = listOfChoices[1]
            answerThreeText.text = listOfChoices[2]
            answerFourText.text = listOfChoices[3]
        }
    }

    private fun getRandomArrangeOfChoices(): List<String> {
        return listOf(
            guessTheMeal.correctAnswer,
            guessTheMeal.wrongAnswerOne,
            guessTheMeal.wrongAnswerTwo,
            guessTheMeal.wrongAnswerThree
        )
            .shuffled()
    }

    private fun prepareQuestion() {
        when (gameName) {
            GuessGamesName.GUESS_THE_MEAL -> setImageQuestion()
            GuessGamesName.GUESS_THE_CUISINE -> setTextQuestion(getString(R.string.questionGuessTheCusine))
            GuessGamesName.GUESS_THE_EXSTING_INGREDIENT -> setTextQuestion(getString(R.string.questionGuessTheMeal))
        }
    }

    private fun setTextQuestion(string: String) {
        binding.apply {
            guessTheMealQuestion.text = string.plus(
                guessTheMeal.question
            )
            mealImageView.visibility = View.GONE
        }
    }

    private fun setImageQuestion() {
        binding.apply {
            Glide.with(mealImageView)
                .load(guessTheMeal.question)
                .error(R.drawable.baseline_error_24)
                .into(mealImageView)
            mealImageView.visibility = View.VISIBLE
        }

    }

    private fun setColorOfSelectedChoice() {
        for (selectedChoice in multiChoices!!) {
            selectedChoice.setOnClickListener {
                setButtonStatus(binding.submitButton, true)
                for (oneChoice in multiChoices!!) {
                    if (isSelectedChoice(oneChoice, selectedChoice)) {
                        colorizeTheView(oneChoice, yellowColor!!)
                    } else
                        colorizeTheView(oneChoice, grayColor!!)
                }
            }
        }
    }

    private fun setButtonStatus(submitButton: Button, buttonStatus: Boolean) {
        submitButton.isEnabled = buttonStatus
    }

    private fun isSelectedChoice(oneChoice: TextView, selectedChoice: TextView): Boolean {
        oneChoice.isSelected = (oneChoice == selectedChoice)
        return when (oneChoice.isSelected) {
            true -> {
                selectedChoiceId = oneChoice.id
                true
            }
            else -> false
        }
    }

    private fun submitAnswer() {
        binding.submitButton.setOnClickListener {
            val selectedChoice = determineSelectedView()
            if (selectedChoice.text.toString() == guessTheMeal.correctAnswer)
                colorizeTheView(selectedChoice, greenColor!!)
            else {
                colorizeTheView(selectedChoice, redColor!!)
            }
            prepareNextQuestion(clickChoicesState = false)
        }
    }

    private fun determineSelectedView(): TextView {
        return binding.QuestionParentLayout.findViewById(selectedChoiceId)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun prepareNextQuestion(clickChoicesState: Boolean) {
        multiChoices!!.forEach { choice ->
            choice.isClickable = clickChoicesState
        }
        if (!clickChoicesState) {
            GlobalScope.launch {
                requireActivity().runOnUiThread {
                    setup()
                }
                delay(100)
            }
        }
    }

    private fun colorizeTheView(selectedChoice: TextView, color: Int) {
        if (color == redColor) {
            selectedChoice.setBackgroundColor(color)
            colorizeTheView(correctAnswer()!!, greenColor!!)
        } else
            selectedChoice.setBackgroundColor(color)
    }

    private fun correctAnswer(): TextView? {
        var correctAnswer: TextView? = null
        multiChoices!!.forEach { choice ->
            if (choice.text == guessTheMeal.correctAnswer)
                correctAnswer = choice
        }
        return correctAnswer
    }

}