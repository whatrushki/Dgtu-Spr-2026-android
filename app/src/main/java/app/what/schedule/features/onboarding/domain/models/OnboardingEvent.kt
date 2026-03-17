package app.what.schedule.features.onboarding.domain.models

sealed interface OnboardingEvent {
    object Init : OnboardingEvent
    object FinishOnboarding : OnboardingEvent
}