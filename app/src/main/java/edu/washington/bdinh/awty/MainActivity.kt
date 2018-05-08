package edu.washington.bdinh.awty

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var context: Context
    lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        button_startStop.isEnabled = false

        editText_message.addTextChangedListener(SimpleTextWatcher(::validInputFields, button_startStop))
        editText_phoneNumber.addTextChangedListener(SimpleTextWatcher(::validInputFields, button_startStop))
        editText_timeInterval.addTextChangedListener(SimpleTextWatcher(::validInputFields, button_startStop))
        val intent = Intent(context, Receiver::class.java)
        button_startStop.setOnClickListener{
            intent.putExtra("message", "${editText_phoneNumber.text}: ${editText_message.text}")
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            if (button_startStop.text == "Start") {
                val timeInterval  = (editText_timeInterval.text.toString().toInt() * 60000).toLong()
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeInterval, timeInterval, pendingIntent)
                button_startStop.text = "Stop"
            } else {
                alarmManager.cancel(pendingIntent)
                button_startStop.text = "Start"
            }
        }

    }

    fun validInputFields() :Boolean {
        return !editText_message.text.isEmpty()
                && !editText_phoneNumber.text.isEmpty()
                && !editText_timeInterval.text.isEmpty()
                && validTimeInterval()
    }

    fun validTimeInterval() :Boolean {
        val phoneNumber = editText_timeInterval.text.toString().toInt()
        return phoneNumber > 0
    }

    class SimpleTextWatcher : TextWatcher {
        private var validator: (() -> Boolean)
        private var button: Button? = null

        constructor(validator: () -> Boolean, button: Button?) {
            this.validator = validator
            this.button = button
        }

        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            this.button?.isEnabled = this.validator()
        }

    }

}

class Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("message")
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}


