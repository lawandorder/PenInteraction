package penInteraction;

import javax.swing.tree.DefaultMutableTreeNode;

public class InkmlTreeNode extends DefaultMutableTreeNode{
String id;
String type;

public String getType() {
	return type;
}

public void setType(String type) {
	this.type = type;
}

public InkmlTreeNode() {
	super();
	// TODO Auto-generated constructor stub
}

public InkmlTreeNode(Object userObject, boolean allowsChildren) {
	super(userObject, allowsChildren);
	// TODO Auto-generated constructor stub
}

public InkmlTreeNode(Object userObject) {
	super(userObject);
	// TODO Auto-generated constructor stub
}

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

}
