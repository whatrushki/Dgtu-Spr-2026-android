package app.what.schedule.features.onboarding.domain

import app.what.foundation.core.UIController
import app.what.schedule.data.local.settings.AppValues
import app.what.schedule.features.onboarding.domain.models.OnboardingAction
import app.what.schedule.features.onboarding.domain.models.OnboardingEvent
import app.what.schedule.features.onboarding.domain.models.OnboardingState


class OnboardingController(
    private val settings: AppValues
) : UIController<OnboardingState, OnboardingAction, OnboardingEvent>(
    OnboardingState()
) {

    override fun obtainEvent(viewEvent: OnboardingEvent) = when (viewEvent) {
        OnboardingEvent.Init -> {}
        OnboardingEvent.FinishOnboarding -> finishAndGoToMain()
    }

    private fun finishAndGoToMain() {
        settings.isFirstLaunch.set(false)
        setAction(OnboardingAction.NavigateToMain)
    }
}