package qyoga

import javafx.stage.Screen
import javafx.stage.Stage
import qyoga.exercises.EditExerciseController
import qyoga.exercises.Exercises
import qyoga.exercises.ExercisesDashboardView
import tornadofx.App
import tornadofx.FX


class QyogaTfxApp : App(ExercisesDashboardView::class) {

    override fun start(stage: Stage) {
        super.start(stage)
        FX.dicontainer = DI(initComponents())
        if (Screen.getPrimary().bounds.width <= 1920.0) {
            stage.isMaximized = true
        } else {
            stage.width = 1920.0
            stage.height = 1080.0
            stage.x = (Screen.getPrimary().visualBounds.width - 1920.0) / 2
            stage.y = (Screen.getPrimary().visualBounds.height - 1080.0) / 2
        }
    }

    private fun initComponents(): Set<Any> {
        return setOf(Exercises(config), EditExerciseController())
    }

}