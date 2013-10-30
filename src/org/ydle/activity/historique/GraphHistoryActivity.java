package org.ydle.activity.historique;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.ydle.R;
import org.ydle.activity.IntentConstantes;
import org.ydle.model.Room;
import org.ydle.model.Sensor;
import org.ydle.model.SensorData;
import org.ydle.model.SensorType;
import org.ydle.model.TimeEchelle;
import org.ydle.remote.FiltreSensorDataAsynkTask;
import org.ydle.remote.SensorDataAsynkTask;
import org.ydle.remote.YdleService;

import android.os.Bundle;
import android.util.Log;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.google.inject.Inject;

public class GraphHistoryActivity extends PlotActivity<Sensor> {

	private static final String TAG = "Ydle.GraphHistoryActivity";
	@Inject
	private YdleService service;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadData();
	}

	@Override
	public void initPlot(final XYPlot plot, List<SensorData> result) {
		plot.setAlpha(1f);
		if (result != null)
			Log.d(TAG, "initPlot size : " + result.size());

		final Room room = getIntent().getParcelableExtra(
				IntentConstantes.ITEM_ROOM);

		plot.setTitle(SensorType.fromCode(item.type).getLabel());
		plot.setRangeLabel(item.unit);
		plot.setDomainLabel(echelle.getLabel());

		FiltreSensorDataAsynkTask task = new FiltreSensorDataAsynkTask(this,
				result, echelle) {
			@Override
			protected void onPostExecute(List<SensorData> datas) {
				super.onPostExecute(datas);
				drawGraph(plot, room, datas);
			}

		};

		task.setDialogueMsg(R.string.init_data);
		task.execute((Void) null);

	}

	private void drawGraph(final XYPlot plot, final Room room,
			List<SensorData> datas) {
		// Create a couple arrays of y-values to plot:
		Number[] series1Numbers = new Number[datas.size()];
		Integer[] series1Date = new Integer[datas.size()];

		SimpleDateFormat sdf = null;

		if (TimeEchelle.MONTH.equals(echelle)) {
			sdf = new SimpleDateFormat("dd");
		} else if (echelle.equals(TimeEchelle.DAY)) {
			sdf = new SimpleDateFormat("HH");
		} else if (echelle.equals(TimeEchelle.YEAR)) {
			sdf = new SimpleDateFormat("MM");
		} else if (echelle.equals(TimeEchelle.WEEK)) {
			sdf = new SimpleDateFormat("dd");
		} else {
			sdf = new SimpleDateFormat("dd");
		}

		int i = 0;
		for (SensorData data : datas) {
			series1Numbers[i] = Math.round(Float.valueOf(data.valeur));
			series1Date[i] = Integer.valueOf(sdf.format(data.date));
			i++;
		}

		// Turn the above arrays into XYSeries':
		XYSeries series1 = new SimpleXYSeries(Arrays.asList(series1Date),
				Arrays.asList(series1Numbers), room.name);

		// Create a formatter to use for drawing a series using
		// LineAndPointRenderer
		// and configure it from xml:
		LineAndPointFormatter series1Format = new LineAndPointFormatter();
		series1Format.setPointLabelFormatter(new PointLabelFormatter());
		series1Format.configure(getApplicationContext(),
				R.xml.line_point_formatter_with_plf1);

		// add a new series' to the xyplot:
		plot.addSeries(series1, series1Format);
		plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
		plot.setDomainValueFormat(new DecimalFormat("0"));

		// reduce the number of range labels
		plot.setTicksPerRangeLabel(9);
		plot.getGraphWidget().setDomainLabelOrientation(-45);
		plot.redraw();
	}

	@Override
	public void loadData() {
		SensorDataAsynkTask task = new SensorDataAsynkTask(this, service, item,
				echelle) {
			@Override
			protected void onPostExecute(List<SensorData> result) {
				super.onPostExecute(result);
				datas = result;
				initPlot(plot, result);

			}
		};

		task.setDialogueMsg(R.string.recherche_server);
		task.execute((Void) null);
		
	}

}