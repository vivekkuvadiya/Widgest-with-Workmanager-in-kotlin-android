package com.example.widgetsdemo.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.widgetsdemo.R
import com.example.widgetsdemo.constant.Constant
import com.example.widgetsdemo.worker.WidgetsWorker
import java.util.concurrent.TimeUnit

class MainWidgetProvider:AppWidgetProvider() {

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

            appWidgetIds?.forEach {appWidgetId ->
                createWidgets(appWidgetManager,appWidgetId, context!!)
            }
    }

    private fun createWidgets(
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        context: Context
    ){

        val view = RemoteViews(context.packageName, R.layout.layout_main_widgets)

        view.setTextViewText(R.id.tvCount,"1")
        appWidgetManager?.updateAppWidget(appWidgetId, view)

        setWorker(appWidgetId, context)
    }

    private fun setWorker(widgetId:Int,context: Context){
        val data = Data.Builder()
        data.putInt(Constant.KEY_WIDGETS_ID,widgetId)
        data.putInt(Constant.KEY_DATA,1)
        val worker = OneTimeWorkRequestBuilder<WidgetsWorker>()
            .addTag(widgetId.toString())
            .setInitialDelay(1,TimeUnit.MINUTES)
            .setInputData(data.build())
            .build()

        WorkManager.getInstance(context).enqueue(worker)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        appWidgetIds?.forEach {
            WorkManager.getInstance(context!!).cancelAllWorkByTag(it.toString())
        }
    }


}