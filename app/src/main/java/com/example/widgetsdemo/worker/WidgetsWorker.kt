package com.example.widgetsdemo.worker

import android.appwidget.AppWidgetManager
import android.content.Context
import android.widget.RemoteViews
import androidx.work.*
import com.example.widgetsdemo.R
import com.example.widgetsdemo.constant.Constant
import java.util.concurrent.TimeUnit

class WidgetsWorker(context:Context,params:WorkerParameters):CoroutineWorker(context,params) {

    override suspend fun doWork(): Result {

        val widgetId = inputData.getInt(Constant.KEY_WIDGETS_ID,0)
        val data = inputData.getInt(Constant.KEY_DATA,0)
        //update widgets
        updateWidget(widgetId,data)
        return Result.success()
    }

    fun updateWidget(widgetId: Int, data:Int){
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val view = RemoteViews(applicationContext.packageName, R.layout.layout_main_widgets)

        view.setTextViewText(R.id.tvCount,"${data+1}")
        appWidgetManager.updateAppWidget(widgetId, view)
        setWorker(widgetId,applicationContext,data+1)
    }

    private fun setWorker(widgetId: Int, context: Context, count: Int){
        val data = Data.Builder()
        data.putInt(Constant.KEY_WIDGETS_ID,widgetId)
        data.putInt(Constant.KEY_DATA,count)
        val worker = OneTimeWorkRequestBuilder<WidgetsWorker>()
            .addTag(widgetId.toString())
            .setInitialDelay(1, TimeUnit.MINUTES)
            .setInputData(data.build())
            .build()
        WorkManager.getInstance(context).enqueue(worker)
    }

}