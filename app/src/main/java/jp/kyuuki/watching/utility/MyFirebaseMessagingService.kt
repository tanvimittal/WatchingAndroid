package jp.kyuuki.watching.utility

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import jp.kyuuki.watching.MainActivity
import jp.kyuuki.watching.R
import jp.kyuuki.watching.database.Preferences
import jp.kyuuki.watching.database.RetrofitFunctions
import jp.kyuuki.watching.model.Event
import jp.kyuuki.watching.model.UserForFcmToken

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
        const val CHANNEL_MESSAGE_ID = "channel_message"
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        sendToSever(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        createNotificationChannel()
        remoteMessage.data?.let {
            val activityName = it["name"]
            val userName = it["user_nickname"]
            if (activityName != null && userName != null ) {
                sendNotification(activityName, userName)
            }
        }
    }

    fun sendInitialTokenToServer(){
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->

                // Get new Instance ID token
                val token = task.result?.token
                if (token != null) {
                    sendToSever(token)
                }
            })
    }

    fun sendToSever(token : String) {
        Preferences.apiKey?.let { RetrofitFunctions.registerFcmToken(it, UserForFcmToken(token)) }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(activityName: String, userName: String) {
        val message = when (activityName) {
            Event.NAME_GET_UP -> getString(R.string.message_received_event_get_up, userName)
            Event.NAME_GO_TO_BED -> getString(R.string.message_received_event_go_to_bed, userName)
            else -> {
                // TODO: エラー処理
                ""
            }
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_MESSAGE_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(message)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_notification_large))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setColor(resources.getColor(R.color.colorPrimary, null))
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_evemt_name)
            val descriptionText = getString(R.string.channel_event_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_MESSAGE_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}