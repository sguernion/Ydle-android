package org.ydle.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("serial")
public class Room implements Parcelable, Serializable {
	public String id;
	public String name;
	public boolean active = true;
	public TypeRoom type;
	public TypeRoomIcon typeIcon;
	public String description;

	public List<Sensor> sensor = new ArrayList<Sensor>();
	private Integer sensorSize;

	public Room(String id, String name, String description,TypeRoomIcon typeIcon) {
		this.id = id;
		this.description = description;
		this.name = name;
		this.typeIcon = typeIcon;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@SuppressWarnings("unchecked")
	public Room(Parcel in) {
		this.id = in.readString();
		this.name = in.readString();
		this.description = in.readString();
		this.active = Boolean.getBoolean(in.readString());
		this.type = in.readParcelable(TypeRoom.class.getClassLoader());
		sensor = in.readArrayList(Sensor.class.getClassLoader());
		this.typeIcon = TypeRoomIcon.fromDrawable(in.readInt());
	}

	public Room() {
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(this.id);
		dest.writeString(this.name);
		dest.writeString(this.description);
		dest.writeString(String.valueOf(this.active));
		dest.writeParcelable(this.type, 0);
		dest.writeArray(this.sensor.toArray());
		dest.writeInt(this.typeIcon.getDrawable());

	}

	public static final Parcelable.Creator<Room> CREATOR = new Parcelable.Creator<Room>() {
		@Override
		public Room createFromParcel(Parcel source) {
			return new Room(source);
		}

		@Override
		public Room[] newArray(int size) {
			return new Room[size];
		}
	};
	
	public int getSensorSize(){
		return sensorSize == null?sensor.size():sensorSize;
	}

	public void setSensorSize(int size) {
		this.sensorSize = size;
	}

}
