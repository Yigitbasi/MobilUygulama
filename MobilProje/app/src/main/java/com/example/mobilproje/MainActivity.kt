package com.example.mobilproje

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mobilproje.model.Event
import com.example.mobilproje.ui.screens.EventDetailScreen
import com.example.mobilproje.ui.screens.HomeScreen
import com.example.mobilproje.ui.screens.LoginScreen
import com.example.mobilproje.ui.screens.ProfileScreen
import com.example.mobilproje.ui.theme.MobilProjeTheme
import com.example.mobilproje.viewmodel.ProfileViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            MobilProjeTheme {
                val navController = rememberNavController()
                var selectedEvent by remember { mutableStateOf<Event?>(null) }

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            onEventClick = { event ->
                                selectedEvent = event
                                navController.navigate("eventDetail")
                            },
                            onProfileClick = {
                                navController.navigate("profile")
                            }
                        )
                    }
                    composable("eventDetail") {
                        selectedEvent?.let { event ->
                            EventDetailScreen(
                                event = event,
                                onNavigateBack = {
                                    navController.navigateUp()
                                },
                                onToggleFavorite = { updatedEvent ->
                                    selectedEvent = updatedEvent.copy(isFavorite = !updatedEvent.isFavorite)
                                    ProfileViewModel.getInstance().toggleFavorite(selectedEvent!!)
                                },
                                onAddComment = { updatedEvent, comment, rating ->
                                    // TODO: Implement comment adding
                                },
                                onAttendEvent = { updatedEvent ->
                                    // TODO: Implement event attendance
                                }
                            )
                        }
                    }
                    composable("profile") {
                        val viewModel = remember { ProfileViewModel.getInstance() }
                        val userProfile by viewModel.userProfile.collectAsState()
                        val favoriteEvents by viewModel.favoriteEvents.collectAsState()
                        
                        ProfileScreen(
                            userProfile = userProfile,
                            favoriteEvents = favoriteEvents,
                            onUpdateProfile = viewModel::updateProfile,
                            onToggleFavorite = viewModel::toggleFavorite,
                            onUpdateNotifications = viewModel::updateNotificationPreferences,
                            onLogout = {
                                viewModel.logout()
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToEvent = { event ->
                                selectedEvent = event
                                navController.navigate("eventDetail")
                            }
                        )
                    }
                }
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    setContent {
                        MobilProjeTheme {
                            HomeScreen(
                                onEventClick = { event ->
                                    // Bu kısım artık kullanılmayacak
                                },
                                onProfileClick = {
                                    // Bu kısım artık kullanılmayacak
                                }
                            )
                        }
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 9001
    }
}
