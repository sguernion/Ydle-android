package org.ydle.remote.tasks;

import java.util.ArrayList;
import java.util.List;

import org.ydle.model.Sensor;
import org.ydle.model.SensorData;
import org.ydle.model.TimeEchelle;
import org.ydle.remote.YdleService;

import android.app.Activity;

public class SensorDataAsynkTask extends AbstractTask<Void,List<SensorData>> {

	private YdleService service;

	public ArrayList<SensorData> items = new ArrayList<SensorData>();

	private Sensor sensor;
	private TimeEchelle echelle;

	public SensorDataAsynkTask(Activity context,YdleService service, Sensor sensor, TimeEchelle echelle) {
		super(context);
		this.service = service;
		this.sensor = sensor;
		this.echelle = echelle;
	}

	@Override
	protected List<SensorData> doInBackground(Void... params) {
		items.addAll(service.getData(sensor,echelle));

		return items;
	}

}