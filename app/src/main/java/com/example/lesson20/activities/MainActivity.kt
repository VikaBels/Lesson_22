package com.example.lesson20.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.PatternsCompat
import androidx.core.view.isVisible
import bolts.CancellationTokenSource
import com.example.lesson20.*
import com.example.lesson20.databinding.ActivityMainBinding
import com.example.lesson20.Tester
import com.example.lesson20.javaInterfaces.TesterAttribute
import com.example.lesson20.javaInterfaces.TesterMethod
import com.example.lesson20.models.LoginResponseBody
import com.example.lesson20.tasks.LoginRepository
import com.example.lesson20.utils.getTextError
import com.example.lesson20.utils.showToastError
import java.lang.reflect.Method

class MainActivity : AppCompatActivity() {
    companion object {
        const val KEY_VISIBLE_ERROR = "KEY_VISIBLE_ERROR"

        const val NAME_PUBLIC_METHOD = "doPublic"
        const val NAME_PRIVATE_METHOD = "doPrivate"
        const val NAME_PROTECTED_METHOD = "doProtected"

        const val TEXT_ABOUT_CONSTRUCTORS = "---Info about constructors---"
        const val TEXT_ABOUT_METHODS = "---Info about methods---"
        const val TEXT_ABOUT_ATTRIBUTES = "---Info about attributes---"
    }

    private var bindingMain: ActivityMainBinding? = null

    private val cancellationTokenSourceMain: CancellationTokenSource = CancellationTokenSource()

    private val loginRepository = LoginRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bindingMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingMain.root)

        this.bindingMain = bindingMain

        checkVisibilityTextError(savedInstanceState)

        setupListeners(bindingMain)

        reflectionMethods()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val isVisibleError = bindingMain?.textViewError?.isVisible

        if (isVisibleError != null) {
            outState.putBoolean(KEY_VISIBLE_ERROR, isVisibleError)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bindingMain = null
        cancellationTokenSourceMain.cancel()
    }

    private fun reflectionMethods() {
        val tester = Tester::class.java

        callMethodDoPublic(tester)

        callMethodDoProtected(tester)

        callMethodDoPrivate(tester)

        infoAboutConstructor(tester)

        infoAboutAttributes(tester)

        infoAboutMethods(tester)
    }

    private fun callMethodDoPublic(tester: Class<Tester>) {
        val methodDoPublic: Method? = tester.methods.find { method ->
            NAME_PUBLIC_METHOD == method.name
        }

        val objTester = Tester("method doPublic")
        methodDoPublic?.invoke(objTester)
    }

    private fun callMethodDoProtected(tester: Class<Tester>) {
        val methodDoProtected: Method? = tester.getDeclaredMethod(NAME_PROTECTED_METHOD)
        methodDoProtected?.isAccessible = true

        val objTester2 = Tester("method doProtected")
        methodDoProtected?.invoke(objTester2)
    }

    private fun callMethodDoPrivate(tester: Class<Tester>) {
        val methodDoPrivate: Method? = tester.getDeclaredMethod(NAME_PRIVATE_METHOD)
        methodDoPrivate?.isAccessible = true

        val objTester3 = Tester("method doPrivate")
        methodDoPrivate?.invoke(objTester3)
    }

    private fun infoAboutConstructor(tester: Class<Tester>) {
        val constructors = tester.constructors

        Log.e("TAG_CONSTRUCTOR", TEXT_ABOUT_CONSTRUCTORS)
        constructors.forEach { constructor ->
            val paramTypes = constructor.parameterTypes

            paramTypes.forEach { paramType ->
                Log.e("TAG_CONSTRUCTOR", "Param: ${paramType.name}")
            }
        }
    }

    private fun infoAboutAttributes(tester: Class<Tester>) {
        val fields = tester.declaredFields

        Log.e("TAG_ATTRIBUTES", TEXT_ABOUT_ATTRIBUTES)
        fields.forEach { field ->
            Log.e("TAG_ATTRIBUTES", "Name: ${field.name}")
            Log.e("TAG_ATTRIBUTES", "Type: ${field.type.name}")

            if (field.isAnnotationPresent(TesterAttribute::class.java)) {
                field.annotations.forEach { annotation ->
                    Log.e("TAG_ATTRIBUTES", "Annotation: $annotation")
                }
            }
        }
    }

    private fun infoAboutMethods(tester: Class<Tester>) {
        val methods = tester.declaredMethods

        Log.e("TAG_METHODS", TEXT_ABOUT_METHODS)
        methods.forEach { method ->
            Log.e("TAG_METHODS", "Name: ${method.name}")

            if (method.isAnnotationPresent(TesterMethod::class.java)) {
                method.annotations.forEach { annotation ->
                    Log.e("TAG_METHODS", "Annotation: $annotation")
                }
            }
        }
    }

    private fun checkVisibilityTextError(savedInstanceState: Bundle?) {
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_VISIBLE_ERROR)) {
            val isVisibleError = savedInstanceState.getBoolean(KEY_VISIBLE_ERROR)
            setVisibleTextError(isVisibleError)
        }
    }

    private fun setDefaultValues() {
        bindingMain?.apply {
            textViewError.isVisible = false

            editTextPassword.clearFocus()
            editTextEmail.clearFocus()
        }
    }

    private fun setupListeners(bindingMain: ActivityMainBinding) {
        bindingMain.buttonLogin.setOnClickListener {
            startServerLoginRepository()
        }
    }

    private fun startServerLoginRepository() {
        val email = getValidEmail()
        val password = getValidPassword()

        if (email != null && password != null) {
            setVisibleProgressbar(true)

            val loginRepository = loginRepository.startTask(email, password)

            loginRepository.task.continueWith {
                when {
                    it.result != null -> {
                        onReceiveResult(it.result)
                    }
                    it.error != null -> {
                        setVisibleProgressbar(false)
                        setVisibleTextError(false)
                        showToastError(getTextError(it.error), this)
                    }
                }
            }
        }
    }

    private fun onReceiveResult(loginResponseBody: LoginResponseBody) {
        setVisibleProgressbar(false)
        checkServerResponse(loginResponseBody)
    }

    private fun getValidEmail(): String? {
        val email = bindingMain?.editTextEmail?.text?.toString()

        return when {
            email.isNullOrEmpty() -> {
                showEmailError(R.string.error_empty_email_field)
                null
            }
            !isMatchesEmailPattern(email) -> {
                showEmailError(R.string.error_not_valid_email_field)
                null
            }
            else -> email
        }
    }

    private fun isMatchesEmailPattern(email: String): Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showEmailError(idResource: Int) {
        bindingMain?.inputLayoutEmail?.error =
            resources.getString(idResource)
    }

    private fun getValidPassword(): String? {
        val password = bindingMain?.editTextPassword?.text?.toString()

        return when {
            password.isNullOrEmpty() -> {
                showPasswordError(R.string.error_empty_password_field)
                null
            }
            else -> password
        }
    }

    private fun showPasswordError(idResource: Int) {
        bindingMain?.inputLayoutPassword?.error =
            getString(idResource)
    }

    private fun setVisibleProgressbar(isVisible: Boolean) {
        bindingMain?.progressBar?.isVisible = isVisible
    }

    private fun checkServerResponse(loginResponseBody: LoginResponseBody) {
        val token = loginResponseBody.token

        when (loginResponseBody.token) {
            null -> {
                setVisibleTextError(true)
            }
            else -> {
                setVisibleTextError(false)
                startProfileActivity(token)
            }
        }
    }

    private fun setVisibleTextError(isVisible: Boolean) {
        bindingMain?.textViewError?.isVisible = isVisible
    }

    private fun startProfileActivity(token: String?) {
        val intent = Intent(this, ProfileActivity::class.java)
        val email = bindingMain?.editTextEmail?.text?.toString()

        intent.apply {
            putExtra(KEY_FOR_SEND_TOKEN, token)
            putExtra(KEY_FOR_SEND_EMAIL, email)
        }

        startActivity(intent)
        setDefaultValues()
    }
}
