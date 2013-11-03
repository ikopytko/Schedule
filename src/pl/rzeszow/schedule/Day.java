package pl.rzeszow.schedule;

import java.util.ArrayList;

public class Day {
	public String day_id;
	public String day_name;
	public ArrayList<Lecture> list_of_lectures;
	
	public class Lecture {
		public String start;
		public String end;
		public String przedmiot;
		public String nauczyciel;
		public String sala;
	}
}
