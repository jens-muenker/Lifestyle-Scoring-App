package com.frosch2010.lifestyle_scoring_app.ui

import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.test.espresso.IdlingResource
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage

/**
 * Espresso idling resource that becomes idle once the view identified by [viewId] is visible
 * in the active window (dialog or activity). Work is performed on the main thread so view-tree
 * and accessibility reads stay consistent with framework threading rules.
 *
 * @param viewId Android resource id whose visibility drives the idle transition
 */
class PlayerNameFieldIdlingResource(
    @param:IdRes private val viewId: Int
) : IdlingResource {

    /**
     * Instrumentation handle used to access UI automation and main-thread execution.
     */
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    /**
     * Target context used to resolve a stable resource name for accessibility queries.
     */
    private val targetContext = instrumentation.targetContext
    /**
     * Fully-qualified Android resource name derived from [viewId].
     */
    private val viewIdResourceName: String =
        targetContext.resources.getResourceName(viewId)

    /**
     * Callback registered by Espresso for idle transition notifications.
     */
    @Volatile
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    /**
     * Unique name for Espresso diagnostics, including the fully qualified resource id segment.
     *
     * @return stable idling-resource name derived from [viewIdResourceName]
     */
    override fun getName(): String = "${PlayerNameFieldIdlingResource::class.java.simpleName}:$viewIdResourceName"

    /**
     * Stores the callback Espresso invokes when this resource first reports idle.
     *
     * @param callback listener supplied by the Espresso framework
     */
    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        resourceCallback = callback
    }

    /**
     * Whether the target view is already visible; when `true`, notifies [resourceCallback].
     *
     * @return `true` if [viewId] is shown in the current UI, otherwise `false`
     */
    override fun isIdleNow(): Boolean {
        // Espresso may call this from the main thread; runOnMainSync is forbidden there.
        val idle = if (Looper.myLooper() == Looper.getMainLooper()) {
            isTargetViewVisibleOnMainThread()
        } else {
            val idleHolder = booleanArrayOf(false)
            instrumentation.runOnMainSync {
                idleHolder[0] = isTargetViewVisibleOnMainThread()
            }
            idleHolder[0]
        }
        if (idle) {
            resourceCallback?.onTransitionToIdle()
        }
        return idle
    }

    /**
     * Resolves visibility on the main thread using accessibility first, then the resumed activity decor view.
     *
     * @return `true` when the view is visible to the user in the current foreground UI
     */
    private fun isTargetViewVisibleOnMainThread(): Boolean {
        if (isVisibleViaAccessibility()) {
            return true
        }
        val resumed = ActivityLifecycleMonitorRegistry.getInstance()
            .getActivitiesInStage(Stage.RESUMED)
        for (activity in resumed) {
            if (activity != null && viewTreeContainsDisplayedId(activity.window.decorView, viewId)) {
                return true
            }
        }
        return false
    }

    /**
     * Fast path using the UI-automation root when the active window exposes accessibility nodes.
     *
     * @return `true` when any node for [viewIdResourceName] reports visible to the user
     */
    private fun isVisibleViaAccessibility(): Boolean {
        val root = instrumentation.uiAutomation.rootInActiveWindow ?: return false
        val nodes = root.findAccessibilityNodeInfosByViewId(viewIdResourceName)
        return nodes?.any { it.isVisibleToUser } == true
    }

    /**
     * Depth-first search for a shown view with the given id under [root].
     *
     * @param root root of the subtree to inspect (typically a window decor view)
     * @param id resource id to match
     * @return `true` when a matching visible child exists
     */
    private fun viewTreeContainsDisplayedId(root: View, @IdRes id: Int): Boolean {
        if (root.id == id && root.isShown) {
            return true
        }
        if (root is ViewGroup) {
            repeat(root.childCount) { i ->
                if (viewTreeContainsDisplayedId(root.getChildAt(i), id)) {
                    return true
                }
            }
        }
        return false
    }
}
