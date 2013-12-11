package pl.rzeszow.schedule;

import java.util.ArrayList;

import pl.rzeszow.schedule.Day.Lecture;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DayAdapter extends BaseAdapter {
	LayoutInflater lInflater;
	DayArray objects;

	DayAdapter(Context context, DayArray products) {
		objects = products;
		lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// elements amount
	@Override
	public int getCount() {
		int size = 0;
		for (Day day : objects.list_of_days) {
			size += day.list_of_lectures.size();
		}
		Log.i("Adapter", "size " + size);
		return size;
	}

	// element by position
	@Override
	public Object getItem(int position) {
		int localPos = 0;
		for (Day day : objects.list_of_days) {
			if ((position-localPos) < day.list_of_lectures.size())
				return day;
			
			localPos += day.list_of_lectures.size();
		}
		// in case of bad position return first day 
		return objects.list_of_days.get(0);
	}

	// id by position
	@Override
	public long getItemId(int position) {
		return position;
	}

	// list item
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// use created, but unused view
		// (the views that are not visible on the screen)
		View view = convertView;
		if (view == null) {
			view = lInflater.inflate(R.layout.list_item, parent, false);
		}

		Day p = getDay(position);
		Lecture l = getLecture(position);

		// fill item view
		TextView title = (TextView) view.findViewById(R.id.title);
		if (is_lecture_first_in_day(position)) { // Show day name?
			title.setVisibility(View.VISIBLE);
			title.setText(p.day_name);
		} else {
			title.setVisibility(View.GONE);
		}
		
		((TextView) view.findViewById(R.id.lecture)).setText(l.przedmiot + "");
		((TextView) view.findViewById(R.id.timeStart)).setText(l.start + "");
		((TextView) view.findViewById(R.id.timeEnd)).setText(l.end + "");
		((TextView) view.findViewById(R.id.teacher)).setText(l.nauczyciel + "");
		((TextView) view.findViewById(R.id.cab)).setText(l.sala + "");
		
		// write position
		// view.setTag(position);
		return view;
	}
	
	boolean is_lecture_first_in_day(int position) {
		int localPos = 0;
		for (Day day : objects.list_of_days) {
			if (localPos == position)
				return true;
			localPos += day.list_of_lectures.size();
		}
		return false;
	}

	// day by position
	Day getDay(int position) {
		return ((Day) getItem(position));
	}

	// lecture by position
	Lecture getLecture(int position) {
		int localPos = 0;
		for (Day day : objects.list_of_days) {
			for (Lecture lecture : day.list_of_lectures) {
				if (localPos == position)
					return lecture;
				localPos++;
			}
		}
		Log.i("Adapter", "getLecture Error " + position);
		// in case of bad position return first lecture
		return objects.list_of_days.get(0).list_of_lectures.get(0);
	}
}
