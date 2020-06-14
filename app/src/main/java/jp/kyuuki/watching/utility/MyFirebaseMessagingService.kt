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
import jp.kyuuki.watching.model.FcmSend
import jp.kyuuki.watching.model.UserForFcmToken
import jp.kyuuki.watching.model.UserPublic

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
        sendToSever(token, MainActivity())
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        remoteMessage.data.isNotEmpty().let {
            val data = remoteMessage.data
            val activityName = data["name"]
            val userName = data["user_nickname"]
            createNotificationChannel()
            if (activityName != null && userName != null ) {
                sendNotification(activityName, userName)
            }
        }

    }


    fun sendInitialTokenToServer(mainActivity: MainActivity){
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->

                // Get new Instance ID token
                val token = task.result?.token
                if (token != null) {
                    sendToSever(token, mainActivity)
                }
            })
    }

    fun sendToSever(token : String , mainActivity: MainActivity) {
        Preferences.apiKey?.let { RetrofitFunctions.registerFcmToken(it, UserForFcmToken(token) , mainActivity) }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(activityName : String, userName : String) {
        var message : String = ""
        if (Event.NAME_GET_UP.equals(activityName)) {
            message = applicationContext.getString(R.string.message_received_event_get_up, userName)
        }else{
            message = applicationContext.getString(R.string.message_received_event_go_to_bed, userName)
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_MESSAGE_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(message)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_message_name)
            val descriptionText = getString(R.string.channel_message_description)
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