package penInteraction;

public class InkmlText {
String textId;
String content;
public String getTextId() {
	return textId;
}
public void setTextId(String textId) {
	this.textId = textId;
}
public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}
public InkmlText(String textId) {
	super();
	this.textId = textId;
}
public InkmlText() {
	super();
}

}
