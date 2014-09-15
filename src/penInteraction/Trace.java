package penInteraction;

import java.util.ArrayList;

public class Trace {
	int traceIndex;
    ArrayList<Point> points=new ArrayList<Point>();
	public Trace(int traceIndex, ArrayList<Point> points) {
		super();
		this.traceIndex = traceIndex;
		this.points = points;
	}
	public int getTraceIndex() {
		return traceIndex;
	}
	public void setTraceIndex(int traceIndex) {
		this.traceIndex = traceIndex;
	}
	public ArrayList<Point> getPoints() {
		return points;
	}
	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}
	
	public Trace() {
		super();
		
	}
	
	
}
