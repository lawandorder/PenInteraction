package penInteraction;

public class InkmlNode {
	
private String NodeId;
private String type;
private InkmlText text;
public String getNodeId() {
	return NodeId;
}
public void setNodeId(String nodeId) {
	NodeId = nodeId;
}
public String getType() {
	return type;
}
public InkmlNode(String nodeId, String type, InkmlText text) {
	super();
	NodeId = nodeId;
	this.type = type;
	this.text = text;
}
public void setType(String type) {
	this.type = type;
}
public InkmlNode() {
	super();
}
public InkmlText getText() {
	return text;
}
public void setText(InkmlText text) {
	this.text = text;
}

}
