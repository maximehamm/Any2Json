package io.nimbly.any2json.util

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project

private val NOTIFICATION_GROUPE =
    NotificationGroupManager.getInstance().getNotificationGroup("io.nimbly.notification.group")

private var LAST_NOTIFICATION: Notification? = null

fun info(message: String, project: Project) {
    notify(message, NotificationType.INFORMATION, project)
}

fun warn(message: String, project: Project) {
    notify(message, NotificationType.WARNING, project)
}

fun error(message: String, project: Project) {
    notify(message, NotificationType.ERROR, project)
}

fun notify(message: String, type: NotificationType, project: Project) {
    val success = NOTIFICATION_GROUPE.createNotification(message, type)
    LAST_NOTIFICATION = success
    Notifications.Bus.notify(success, project)
}

fun lastNotification()
    = LAST_NOTIFICATION?.content ?: ""

fun resetLastNotification() {
    LAST_NOTIFICATION = null
}