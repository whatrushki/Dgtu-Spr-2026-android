package app.what.navigation.core

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlin.reflect.KClass

typealias Registry = NavGraphBuilder.() -> Unit

inline fun <reified P : NavProvider, S : NavComponent<P>> NavGraphBuilder.register(screen: KClass<S>) {
    composable<P>(
        enterTransition = {
            fadeIn(animationSpec = tween(320, easing = FastOutSlowInEasing)) +
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(320, easing = FastOutSlowInEasing),
                    initialOffset = { fullWidth -> fullWidth / 14 }
                ) +
                scaleIn(
                    initialScale = 0.985f,
                    animationSpec = tween(320, easing = FastOutSlowInEasing)
                )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(220, easing = FastOutSlowInEasing)) +
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(220, easing = FastOutSlowInEasing),
                    targetOffset = { fullWidth -> -fullWidth / 18 }
                ) +
                scaleOut(
                    targetScale = 0.992f,
                    animationSpec = tween(220, easing = FastOutSlowInEasing)
                )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) +
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300, easing = FastOutSlowInEasing),
                    initialOffset = { fullWidth -> -fullWidth / 14 }
                ) +
                scaleIn(
                    initialScale = 0.985f,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(220, easing = FastOutSlowInEasing)) +
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(220, easing = FastOutSlowInEasing),
                    targetOffset = { fullWidth -> fullWidth / 18 }
                ) +
                scaleOut(
                    targetScale = 0.992f,
                    animationSpec = tween(220, easing = FastOutSlowInEasing)
                )
        }
    ) {
        val s = remember { screen.constructors.first().call(it.toRoute<P>()) }
        s.content(Modifier)
    }
}
