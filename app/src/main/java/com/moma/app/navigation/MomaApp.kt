package com.moma.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.EventNote
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.moma.app.ui.theme.Background
import com.moma.app.ui.theme.Green100
import com.moma.app.ui.theme.Green700
import com.moma.app.ui.theme.Ink300
import com.moma.app.ui.theme.Ink500
import com.moma.app.ui.theme.Ink900
import com.moma.app.ui.theme.Surface
import com.moma.feature.auth.LoginScreen
import com.moma.feature.booking.BookingsScreen
import com.moma.feature.booking.CheckoutScreen
import com.moma.feature.gym.CoachDetailScreen
import com.moma.feature.gym.CurriculumScreen
import com.moma.feature.gym.GymHomeScreen
import com.moma.feature.gym.RequirementScreen
import com.moma.feature.gym.ShortlistScreen
import com.moma.feature.profile.ProfileScreen

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val CURRICULUM = "curriculum"
    const val BOOKINGS = "bookings"
    const val PROFILE = "profile"
    const val REQUIREMENT = "requirement"
    const val SHORTLIST = "shortlist/{requirementId}"
    const val COACH = "coach/{coachId}/{requirementId}"
    const val CHECKOUT = "checkout/{bookingId}/{coachId}/{programId}"

    fun shortlist(requirementId: String) = "shortlist/$requirementId"
    fun coach(coachId: String, requirementId: String) = "coach/$coachId/$requirementId"
    fun checkout(bookingId: String, coachId: String, programId: String) =
        "checkout/$bookingId/$coachId/$programId"
}

private data class TabItem(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    TabItem(Routes.HOME, "Today", Icons.Rounded.Today),
    TabItem(Routes.CURRICULUM, "Week", Icons.Rounded.CalendarMonth),
    TabItem(Routes.BOOKINGS, "Bookings", Icons.Rounded.EventNote),
    TabItem(Routes.PROFILE, "Profile", Icons.Rounded.Person)
)

private val topLevelRoutes = tabs.map { it.route }.toSet()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MomaApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isTopLevel = currentRoute != null && currentRoute in topLevelRoutes
    val isLogin = currentRoute == Routes.LOGIN

    Scaffold(
        containerColor = Background,
        topBar = {
            if (!isTopLevel && !isLogin) {
                TopAppBar(
                    title = {
                        Text(
                            text = when {
                                currentRoute?.startsWith("shortlist") == true -> "Shortlist"
                                currentRoute?.startsWith("coach") == true -> "Coach profile"
                                currentRoute?.startsWith("checkout") == true -> "Checkout"
                                currentRoute == Routes.REQUIREMENT -> "New requirement"
                                else -> "MoMA"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            color = Ink900
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Ink900)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
                )
            }
        },
        bottomBar = {
            if (isTopLevel) {
                NavigationBar(containerColor = Surface) {
                    tabs.forEach { tab ->
                        val selected = backStackEntry?.destination?.hierarchy
                            ?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = {
                                Text(tab.label, style = MaterialTheme.typography.labelSmall)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Green700,
                                selectedTextColor = Green700,
                                indicatorColor = Green100,
                                unselectedIconColor = Ink300,
                                unselectedTextColor = Ink500
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.LOGIN
            ) {
                composable(Routes.LOGIN) {
                    LoginScreen(
                        onSignedIn = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.HOME) {
                    GymHomeScreen(
                        onOpenShortlist = { requirementId ->
                            navController.navigate(Routes.shortlist(requirementId))
                        },
                        onOpenCurriculum = { navController.navigate(Routes.CURRICULUM) },
                        onOpenRequirement = { navController.navigate(Routes.REQUIREMENT) }
                    )
                }

                composable(Routes.CURRICULUM) {
                    CurriculumScreen(
                        onOpenShortlist = { requirementId ->
                            navController.navigate(Routes.shortlist(requirementId))
                        }
                    )
                }

                composable(Routes.BOOKINGS) {
                    BookingsScreen(
                        onFindCoach = { navController.navigate(Routes.HOME) }
                    )
                }

                composable(Routes.PROFILE) {
                    ProfileScreen(
                        onSignedOut = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.REQUIREMENT) {
                    RequirementScreen(
                        onDone = { navController.popBackStack() }
                    )
                }

                composable(Routes.SHORTLIST) { entry ->
                    val requirementId = entry.arguments?.getString("requirementId").orEmpty()
                    ShortlistScreen(
                        onOpenCoach = { coachId, reqId ->
                            navController.navigate(Routes.coach(coachId, reqId.ifBlank { requirementId }))
                        }
                    )
                }

                composable(Routes.COACH) { entry ->
                    val coachId = entry.arguments?.getString("coachId").orEmpty()
                    val requirementId = entry.arguments?.getString("requirementId").orEmpty()
                    CoachDetailScreen(
                        onBook = { bookingId, bookedCoachId, programId ->
                            navController.navigate(
                                Routes.checkout(bookingId, bookedCoachId.ifBlank { coachId }, programId)
                            )
                        }
                    )
                }

                composable(Routes.CHECKOUT) {
                    CheckoutScreen(
                        onPaid = {
                            navController.navigate(Routes.BOOKINGS) {
                                popUpTo(Routes.HOME)
                            }
                        }
                    )
                }
            }
        }
    }
}
