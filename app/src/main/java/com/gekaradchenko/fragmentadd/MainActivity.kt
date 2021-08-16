package com.gekaradchenko.fragmentadd

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gekaradchenko.fragmentadd.databinding.ActivityMainBinding


private lateinit var binding: ActivityMainBinding
private lateinit var sharedViewModel: SharedViewModel

private const val CHANNEL_ID = "channel_id"
private const val KEY_COUNT = "key_count"

private lateinit var notificationManager: NotificationManagerCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val viewModelFactory = ViewModelFactory(application)
        sharedViewModel = ViewModelProvider(this, viewModelFactory).get(SharedViewModel::class.java)

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)

        binding.viewPager2.adapter = adapter

        sharedViewModel.listFragment.observe(this, Observer {

            adapter.fragmentList = it
        })

        sharedViewModel.pushNotification.observe(this, ::pushNotification)
        sharedViewModel.deleteNotification.observe(this, ::deleteNotification)

        sharedViewModel.count.observe(this, Observer {
            sharedViewModel.setList()
            var extrasVal = intent.extras
            if (extrasVal != null) {
                val value = extrasVal.getInt(KEY_COUNT)
                binding.viewPager2.currentItem = value - 1
                intent.removeExtra(KEY_COUNT)
            }
        })
    }

    private fun deleteNotification(count: Int?) {
        count?.let {
            try {
                notificationManager.cancel(it)
            } catch (e: Exception) {
            }
        }
    }

    private fun pushNotification(count: Int?) {

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(KEY_COUNT, count)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)


        val notification1 = RemoteViews(packageName, R.layout.custom_notification_exam)
        notification1.setTextViewText(R.id.text_view_collapsed_1,
            getString(R.string.notification_title))
        notification1.setTextViewText(R.id.text_view_collapsed_2,
            "${getString(R.string.notification_text)} $count")

        val notification2 = RemoteViews(packageName, R.layout.custom_notification_two)
        notification2.setTextViewText(R.id.text_view_collapsed_2_1,
            getString(R.string.notification_title_2))
        notification2.setTextViewText(R.id.text_view_collapsed_2_2,
            "${getString(R.string.notification_text)} $count")

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText("${getString(R.string.notification_text)} $count")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCustomBigContentView(notification1)
            .setCustomContentView(notification2)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())

        count?.let {
            notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(it, notification.build())

        }
    }
}