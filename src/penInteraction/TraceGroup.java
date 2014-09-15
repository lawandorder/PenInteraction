package penInteraction;

import java.util.ArrayList;

public class TraceGroup {
	int TraceGroupIndex;
    ArrayList<Integer> traceDataRefs=new  ArrayList<Integer>();
    String annotation;
    String href;
    String conTent;

	public String getConTent() {
		return conTent;
	}


	public void setConTent(String conTent) {
		this.conTent = conTent;
	}


	public String getHref() {
		return href;
	}


	public void setHref(String href) {
		this.href = href;
	}


	public int getTraceGroupIndex() {
		return TraceGroupIndex;
	}


	public void setTraceGroupIndex(int traceGroupIndex) {
		TraceGroupIndex = traceGroupIndex;
	}


	public ArrayList<Integer> getTraceDataRefs() {
		return traceDataRefs;
	}


	public void setTraceDataRefs(ArrayList<Integer> traceDataRefs) {
		this.traceDataRefs = traceDataRefs;
	}


	public String getAnnotation() {
		return annotation;
	}


	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}


	public TraceGroup(int traceGroupIndex, ArrayList<Integer> traceDataRefs,
			String annotation) {
		super();
		this.TraceGroupIndex = traceGroupIndex;
		this.traceDataRefs = traceDataRefs;
		this.annotation = annotation;
	}


	public TraceGroup() {
		super();
	}
}
