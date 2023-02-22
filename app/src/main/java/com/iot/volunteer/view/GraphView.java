package com.iot.volunteer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.model.ItemHeartRate;

import java.util.ArrayList;

public class GraphView extends View {
    ArrayList<ItemHeartRate> valueList = new ArrayList<>();
    String[] marksH = {
            "", "02:00", "", "06:00",
            "", "10:00", "", "14:00",
            "", "18:00", "", "22:00",
            ""
    };
    String[] marksV = {
            "", "", "", "", "40", "", "60", "", "80", "", "100", "", "120"
    };
    int divideH = 12;   // Horizontal Dots Count
    int divideV = 14;   // Vertical Dots Count
    int limitLower = Prefs.DEFAULT_MIN_RATE;    // Heart Rate Lower Limit
    int limitUpper = Prefs.DEFAULT_MAX_RATE;   // Heart Rate Upper Limit
    int vGap = 10;  // Gap of values per Dot
    int hGap = 2;  // Gap of values per Dot
    int radiusDot = 3;

    public GraphView(Context context) {
        super(context);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setRateData(ArrayList<ItemHeartRate> rateData) {
        valueList = rateData;

        invalidate();
    }

    public void setHeartRateRange(int low, int high) {
        limitLower = low;
        limitUpper = high;

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect drawingRect = new Rect();
        getDrawingRect(drawingRect);

        int axisL = drawingRect.left + 60;
        int axisT = drawingRect.top + 40;
        int axisR = drawingRect.right - 60;
        int axisB = drawingRect.bottom - 40;
        int axisW = axisR - axisL;  // Width
        int axisH = axisB - axisT;  // Height
        int unitH = axisW /divideH; // Horizontal
        int unitV = axisH /divideV; // Vertical

        int x = 0;
        int y = 0;
        int count = 0;

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setTextSize(20);
        paint.setStrokeWidth(2);

        //-- Draw Axis
        paint.setColor(getContext().getColor(R.color.color_tab_selected));
        canvas.drawLine(axisL, axisT, axisL, axisB, paint);
        canvas.drawLine(axisL, axisB, axisR, axisB, paint);

        //-- Horizontal Dots
        for (int i = 1; i <= divideH; i++) {
            x = axisL + i * unitH;
            drawDot(canvas, x, axisB, paint);
        }

        //-- Vertical Dots
        for (int i = 1; i <= divideV; i++) {
            y = axisB - i * unitV;
            drawDot(canvas, axisL, y, paint);
        }

        paint.setColor(Color.BLACK);
        //-- Horizontal Marks
        count = marksH.length < (divideH + 1) ? marksH.length : divideH + 1;
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                if (marksH[i].isEmpty())    continue;
                x = axisL + unitH * i;
                y = axisB;

                Rect textBound = new Rect();
                paint.getTextBounds(marksH[i], 0, marksH[i].length(), textBound);
                canvas.drawText(marksH[i], x - textBound.width() / 2, y + textBound.height() + 10, paint);
            }
        }

        //-- Veritical Marks
        count = marksV.length < (divideV + 1) ? marksV.length : divideV + 1;
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                if (marksV[i].isEmpty())    continue;
                x = axisL - 25;
                y = axisB - unitV * i - 10;

                Rect textBound = new Rect();
                paint.getTextBounds(marksV[i], 0, marksV[i].length(), textBound);
                canvas.drawText(marksV[i], x - textBound.width() / 2, y + textBound.height(), paint);
            }
        }

        //-- Lower Limit Line
        paint.setColor(getContext().getColor(android.R.color.darker_gray));
        x = axisL;
        y = axisB - limitLower * unitV / vGap;
        while (x <= axisR) {
            canvas.drawLine(x, y, x + 3, y, paint);
            x = x + 6;
        }

        //-- Upper Limit Line
        x = axisL;
        y = axisB - limitUpper * unitV / vGap;
        while (x <= axisR) {
            canvas.drawLine(x, y, x + 3, y, paint);
            x = x + 6;
        }

        if (valueList == null || valueList.isEmpty()) {
            drawEmptyText(canvas, paint);
            return;
        }

        //-- Graph
        paint.setColor(getContext().getColor(R.color.color_tab_selected));

        count = valueList.size();
        if (count > 0) {
            canvas.save();

            paint.setStyle(Paint.Style.STROKE);
            Path path = new Path();
            x = (int) (axisL + valueList.get(0).getHourPercent() * unitH / hGap);
            y = axisB - valueList.get(0).heartRate * unitV / vGap;
            path.moveTo(x, y);
            for (int i = 1; i < count; i++) {
                if (valueList.get(i).heartRate < 0)  continue;
                if (valueList.get(i).getHourPercent() < 0 || valueList.get(i).getHourPercent() > 24)  continue;
                x = (int) (axisL + valueList.get(i).getHourPercent() * unitH / hGap);
                y = axisB - valueList.get(i).heartRate * unitV / vGap;
                path.lineTo(x, y);
            }
            canvas.drawPath(path, paint);

            canvas.restore();
        }

        //-- Graph Dots
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                if (valueList.get(i).heartRate < 0)  continue;
                if (valueList.get(i).getHourPercent() < 0 || valueList.get(i).getHourPercent() > 24)  continue;
                x = (int) (axisL + valueList.get(i).getHourPercent() * unitH / hGap);
                y = axisB - valueList.get(i).heartRate * unitV / vGap;
                drawDot(canvas, x, y, paint);
            }
        }

        //-- Graph Text
        count = valueList.size();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                if (valueList.get(i).heartRate < 0)  continue;
                if (valueList.get(i).getHourPercent() < 0 || valueList.get(i).getHourPercent() > 24)  continue;
                x = (int) (axisL + valueList.get(i).getHourPercent() * unitH / hGap);
                y = axisB - valueList.get(i).heartRate * unitV / vGap;

                String strRate = String.valueOf(valueList.get(i).heartRate);
                Rect textBound = new Rect();
                paint.getTextBounds(strRate, 0, strRate.length(), textBound);
                RectF rectRate = new RectF(x - textBound.width() / 2 - 5, y - textBound.height() - 20, x + textBound.width() / 2 + 5, y - 10);
                if (valueList.get(i).heartRate < limitLower) {
                    rectRate.offset(0, textBound.height() + 30);
                }
                if (valueList.get(i).heartRate < limitLower || valueList.get(i).heartRate > limitUpper) {
                    drawAlertRect(canvas, rectRate, Color.RED, paint);
                    drawRateText(canvas, strRate, Color.WHITE, rectRate, paint);
                }
                else {
                    drawRateText(canvas, strRate, getContext().getColor(R.color.color_green), rectRate, paint);
                }
            }
        }
    }

    private void drawDot(Canvas canvas, int x, int y, Paint paint) {
        canvas.save();
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, radiusDot, paint);
        canvas.restore();
    }


    private void drawAlertRect(Canvas canvas, RectF rect, int color, Paint paint) {
        canvas.save();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawRoundRect(rect, 2, 2, paint);
        canvas.restore();
    }


    private void drawRateText(Canvas canvas, String strRate, int color, RectF rect, Paint paint) {
        canvas.save();
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(color);
        canvas.drawText(strRate, rect.left + 5, rect.bottom - 5, paint);
        canvas.restore();
    }

    private void drawEmptyText(Canvas canvas, Paint paint) {
        canvas.save();
        paint.setStrokeWidth(1);
        paint.setTextSize(24);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(getContext().getColor(R.color.color_border));

        String strEmpty = getContext().getString(R.string.str_no_database);
        Rect textBound = new Rect();
        paint.getTextBounds(strEmpty, 0, strEmpty.length(), textBound);

        canvas.drawText(strEmpty, (getWidth() - textBound.width()) / 2, getHeight() / 2, paint);
        canvas.restore();
    }
}
