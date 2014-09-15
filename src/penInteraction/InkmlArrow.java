package penInteraction;

public class InkmlArrow {
String ArrowId;
String source;
String target;
InkmlText text;
public String getArrowId() {
	return ArrowId;
}
public void setArrowId(String arrowId) {
	ArrowId = arrowId;
}
public InkmlArrow(String arrowId) {
	super();
	ArrowId = arrowId;
}
public String getSource() {
	return source;
}
public void setSource(String source) {
	this.source = source;
}
public String getTarget() {
	return target;
}
public InkmlArrow() {
	super();
}
public void setTarget(String target) {
	this.target = target;
}
public InkmlText getText() {
	return text;
}
public void setText(InkmlText text) {
	this.text = text;
}

}
