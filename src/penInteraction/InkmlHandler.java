package penInteraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;
import javax.xml.xpath.*;

public class InkmlHandler {

	ArrayList<Trace> traces = new ArrayList<Trace>();
	ArrayList<TraceGroup> traceGroups = new ArrayList<TraceGroup>();

	ArrayList<InkmlNode> nodes = new ArrayList<InkmlNode>();
	ArrayList<InkmlArrow> arrows = new ArrayList<InkmlArrow>();

	public void readInkml(String fileName) {
		// TODO Auto-generated method stub
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			factory.setIgnoringElementContentWhitespace(true);

			DocumentBuilder db = factory.newDocumentBuilder();
			Document xmldoc = db.parse(new File(fileName));
			Element root = xmldoc.getDocumentElement();

			readTraces(root);
			readTraceGroups(root);
			readNode(root);
			readArrow(root);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<InkmlNode> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<InkmlNode> nodes) {
		this.nodes = nodes;
	}

	public ArrayList<InkmlArrow> getArrows() {
		return arrows;
	}

	public void setArrows(ArrayList<InkmlArrow> arrows) {
		this.arrows = arrows;
	}

	private void readTraces(Element root) {
		// TODO Auto-generated method stub
		/* 处理流程图的笔画 */
		traces.clear();
		NodeList someTraces = root.getElementsByTagName("trace");
		// System.out.println("获得所有笔画");
		// System.out.println("--- 共有　"+someTraces.getLength()+"个笔画。 ---");
		for (int i = 0; i < someTraces.getLength(); i++) {
			Trace tempTrace = new Trace();
			String trace = someTraces.item(i).getTextContent();
			int traceId = Integer.parseInt(((Element) someTraces.item(i))
					.getAttribute("id"));

			tempTrace.setTraceIndex(traceId);

			String[] points = trace.split(",");
			double tempx = Double.parseDouble(points[0].split(" ")[0]);
			double tempy = Double.parseDouble(points[0].split(" ")[1]);
			tempTrace.points.add(new Point(tempx, tempy));

			for (int j = 1; j < points.length; j++) {
				String[] X_Y = points[j].split(" ");

				double x = Double.parseDouble(X_Y[1]);
				double y = Double.parseDouble(X_Y[2]);
				tempTrace.points.add(new Point(x, y));

			}

			traces.add(tempTrace);
		}

		// printTraces();

	}

	private void readTraceGroups(Element root) {
		// TODO Auto-generated method stub
		/* 处理流程图各元素描述信息 */
		traceGroups.clear();
		NodeList someTraceGroups = root.getElementsByTagName("traceGroup");
		System.out.println("获得所有描述信息");
		System.out.println("--- 共有　" + someTraceGroups.getLength()
				+ "个描述信息。 ---");
		for (int i = 0; i < someTraceGroups.getLength(); i++) {

			TraceGroup tempTraceGroup = new TraceGroup();
			Element traceGroup_ = (Element) someTraceGroups.item(i);
			int traceGroupId = Integer.parseInt(traceGroup_
					.getAttribute("xml:id"));

			tempTraceGroup.setTraceGroupIndex(traceGroupId);

			String annotation = traceGroup_.getElementsByTagName("annotation")
					.item(0).getTextContent();

			tempTraceGroup.setAnnotation(annotation);

			if (traceGroup_.getElementsByTagName("annotationXML").getLength() > 0) {
				Element axml = (Element) traceGroup_.getElementsByTagName(
						"annotationXML").item(0);
				tempTraceGroup.setHref(axml.getAttribute("href"));
			}
			NodeList traceViews = traceGroup_.getElementsByTagName("traceView");
			for (int j = 0; j < traceViews.getLength(); j++) {
				Element traceView = (Element) traceViews.item(j);
				int traceDataRef = Integer.parseInt(traceView
						.getAttribute("traceDataRef"));

				tempTraceGroup.traceDataRefs.add(traceDataRef);

			}

			// String href=traceGroup_.getElementsByTagName("annotation")
			// .item(0).getTextContent();
			if (!annotation.equals("From ITF")) {
				traceGroups.add(tempTraceGroup);
			}
		}

		// printTraceGroups();

	}

	public void readNode(Element root) {
		NodeList someNodes = root.getElementsByTagName("node");
		// System.out.println("节点个数："+someNodes.getLength());
		for (int i = 0; i < someNodes.getLength(); i++) {
			InkmlNode tempNode = new InkmlNode();
			InkmlText tempText = new InkmlText();
			// System.out.println(someNodes.item(i));
			Element node_ = (Element) someNodes.item(i);
			String nodeId = node_.getAttribute("xml:id");
			String nodeType = node_.getAttribute("type");
			if (node_.getElementsByTagName("text").getLength() > 0) {
				Element text = (Element) node_.getElementsByTagName("text")
						.item(0);
				String textId = text.getAttribute("xml:id");
				
				String textContent = text.getTextContent();
				tempText.setTextId(href2id(textId));
				tempText.setContent(textContent);
			}
			tempNode.setNodeId(href2id(nodeId));
			tempNode.setType(nodeType);
			tempNode.setText(tempText);

			nodes.add(tempNode);
			// System.out.println(nodeId+" "+nodeType+" "+tempText.textId+" "+tempText.content);
		}

	}

	private String href2id(String textId) {
		
		for(TraceGroup tg:traceGroups){
			if(tg.getHref()!=null&&tg.getHref().equals(textId)){
				return String.valueOf(tg.getTraceGroupIndex());
			}
		}
		return "-1";
		// TODO Auto-generated method stub
		
	}
	
	private String id2href(String id){
		for(TraceGroup tg:traceGroups){
			if(tg.getHref()!=null&&String.valueOf(tg.getTraceGroupIndex()).equals(id)){
				return tg.getHref();
			}
		}
		return "-1";
	}

	public void readArrow(Element root) {
		NodeList someArrows = root.getElementsByTagName("arrow");
		// System.out.println("箭头个数："+someArrows.getLength());
		for (int i = 0; i < someArrows.getLength(); i++) {
			InkmlArrow tempArrow = new InkmlArrow();
			InkmlText tempText = new InkmlText();
			// System.out.println(someNodes.item(i));
			Element arrow_ = (Element) someArrows.item(i);
			String arrowId = arrow_.getAttribute("xml:id");
			String arrowSource = arrow_.getAttribute("source");
			String arrowTarget = arrow_.getAttribute("target");
			if (arrow_.getElementsByTagName("text").getLength() > 0) {
				Element text = (Element) arrow_.getElementsByTagName("text")
						.item(0);
				String textId = text.getAttribute("xml:id");
				String textContent = text.getTextContent();
				tempText.setTextId(href2id(textId));
				tempText.setContent(textContent);
				tempArrow.setText(tempText);
			}
			tempArrow.setArrowId(href2id(arrowId));
			tempArrow.setSource(href2id(arrowSource));
			tempArrow.setTarget(href2id(arrowTarget));
			

			arrows.add(tempArrow);
			// System.out.println(arrowId+" "+arrowSource+" "+arrowTarget+" "+tempText.textId+" "+tempText.content);
		}
	}

	public static void output(Node node) {// 将node的XML字符串输出到控制台
		TransformerFactory transFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty("encoding", "gb2312");
			transformer.setOutputProperty("indent", "yes");

			DOMSource source = new DOMSource();
			source.setNode(node);
			StreamResult result = new StreamResult();
			result.setOutputStream(System.out);

			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	public static Node selectSingleNode(String express, Object source) {// 查找节点，并返回第一个符合条件节点
		Node result = null;
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			result = (Node) xpath
					.evaluate(express, source, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static NodeList selectNodes(String express, Object source) {// 查找节点，返回符合条件的节点集。
		NodeList result = null;
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			result = (NodeList) xpath.evaluate(express, source,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static void saveInkml(String fileName, Document doc) {// 将Document输出到文件
		TransformerFactory transFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty("encoding", "gb2312");
			transformer.setOutputProperty("indent", "yes");

			DOMSource source = new DOMSource();
			source.setNode(doc);
			StreamResult result = new StreamResult();
			result.setOutputStream(new FileOutputStream(fileName));

			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Trace> getTraces() {
		return traces;
	}

	public void setTraces(ArrayList<Trace> traces) {
		this.traces = traces;
	}

	public ArrayList<TraceGroup> getTraceGroups() {
		return traceGroups;
	}

	public void setTraceGroups(ArrayList<TraceGroup> traceGroups) {
		this.traceGroups = traceGroups;
	}

	public InkmlHandler() {
		super();
	}

	public void printTraces() {
		for (int i = 0; i < traces.size(); i++) {
			System.out.println("\n TraceId:" + traces.get(i).traceIndex);
			ArrayList<Point> temp = traces.get(i).points;
			for (int j = 0; j < temp.size(); j++) {
				System.out
						.println("x:" + temp.get(j).x + ",y:" + temp.get(j).y);
			}

		}
	}

	public void printTraceGroups() {
		// TODO Auto-generated method stub
		for (int i = 0; i < traceGroups.size(); i++) {
			System.out.println("\n TraceGroupId:"
					+ traceGroups.get(i).TraceGroupIndex);
			System.out.println(traceGroups.get(i).annotation);
			ArrayList<Integer> refs = traceGroups.get(i).traceDataRefs;
			for (int j = 0; j < refs.size(); j++) {
				System.out.print(refs.get(j) + ",");
			}
			System.out.println();
		}
	}

	public void writeInkml(ArrayList<Integer> traceIdList, String annotation,
			String fileName) {
		// TODO Auto-generated method stub
		readInkml(fileName);
		System.out.println("writeInkml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Element theAllTG = null, theTraceGroup = null, theElem = null, root = null;

		try {
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder db = factory.newDocumentBuilder();
			Document xmldoc = db.parse(new File(fileName));
			root = xmldoc.getDocumentElement();

			int TraceGroupCount = root.getElementsByTagName("traceGroup")
					.getLength();
			System.out.println("TraceGroupCount:" + TraceGroupCount);
			if ((TraceGroupCount == 0)) {
				theAllTG = xmldoc.createElement("traceGroup");
				theAllTG.setAttribute("xml:id", String.valueOf(traces.size()));
				theElem = xmldoc.createElement("annotation");
				theElem.setAttribute("type", "truth");
				theElem.setTextContent("From ITF");
				theAllTG.appendChild(theElem);
				root.appendChild(theAllTG);

				// 新建一个traceGroup
				theTraceGroup = xmldoc.createElement("traceGroup");
				theTraceGroup.setAttribute("xml:id",
						String.valueOf(traces.size() + 1));
				theElem = xmldoc.createElement("annotation");
				theElem.setAttribute("type", "truth");
				theElem.setTextContent(annotation);
				theTraceGroup.appendChild(theElem);

				for (int i = 0; i < traceIdList.size(); i++) {
					theElem = xmldoc.createElement("traceView");
					theElem.setAttribute("traceDataRef",
							String.valueOf(traceIdList.get(i)));
					theTraceGroup.appendChild(theElem);
				}

				theAllTG.appendChild(theTraceGroup);

			} else {
				System.out.println("else");
				NodeList someTraceGroups = root
						.getElementsByTagName("traceGroup");
				theAllTG = (Element) someTraceGroups.item(0);

				// 新建一个traceGroup
				theTraceGroup = xmldoc.createElement("traceGroup");

				theTraceGroup.setAttribute("xml:id",
						String.valueOf(traces.size() + TraceGroupCount));
				theElem = xmldoc.createElement("annotation");
				theElem.setAttribute("type", "truth");
				theElem.setTextContent(annotation);
				theTraceGroup.appendChild(theElem);

				for (int i = 0; i < traceIdList.size(); i++) {
					theElem = xmldoc.createElement("traceView");
					theElem.setAttribute("traceDataRef",
							String.valueOf(traceIdList.get(i)));
					theTraceGroup.appendChild(theElem);
				}

				// 移除已有的traceGroup（实现覆盖）
				IsAlike(traceIdList, root, fileName, xmldoc);
				// int TraceGroupIndex=-1;
				// Element TempTG=null;
				// if (index != -1) {
				// System.out.println(index);
				// TraceGroupIndex=traceGroups.get(index).TraceGroupIndex;
				// System.out.println(TraceGroupIndex);
				// String z=String.valueOf(TraceGroupIndex);
				// System.out.println(z);
				// TempTG=(Element)
				// selectSingleNode("/ink/traceGroup/traceGroup[@id="+z+"]",
				// root);
				//
				//
				// System.out.println("--- 用xml:id属性删除原本的TG ----");
				// String annotation_ =
				// TempTG.getElementsByTagName("annotation")
				// .item(0).getTextContent();
				//
				// System.out.println(annotation_);
				// TempTG.getParentNode().removeChild(TempTG);
				// }

				theAllTG.appendChild(theTraceGroup);
			}

			saveInkml(fileName, xmldoc);
			System.out.println("写入完毕");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// private void AlreadyExist(ArrayList<Integer> traceIdList) {
	// // TODO Auto-generated method stub
	// int index = -1;
	// for (int i = 0; i < traceGroups.size(); i++) {
	// IsAlike(traceGroups.get(i).traceDataRefs, traceIdList);
	//
	// }
	// }

	private void IsAlike(ArrayList<Integer> traceIdList, Element root,
			String fileName, Document xmldoc) {
		// TODO Auto-generated method stub
		for (int k = 0; k < traceGroups.size(); k++) {

			ArrayList<Integer> traceDataRefs = traceGroups.get(k).traceDataRefs;

			Element TempTraceView = null;
			Element TempTraceGroup = null;
			int aLength = traceDataRefs.size();
			int bLength = traceIdList.size();

			for (int i = 0; i < aLength; i++) {
				for (int j = 0; j < bLength; j++) {
					if (traceDataRefs.get(i) == traceIdList.get(j)) {

						TempTraceView = (Element) selectSingleNode(
								"/ink/traceGroup/traceGroup/traceView[@traceDataRef="
										+ String.valueOf(traceDataRefs.get(i))
										+ "]", root);
						String annotation_ = TempTraceView
								.getAttribute("traceDataRef");

						System.out.println("at:" + annotation_);

						TempTraceView.getParentNode()
								.removeChild(TempTraceView);

					}
				}
			}

			int TraceGroupIndex = traceGroups.get(k).TraceGroupIndex;
			TempTraceGroup = (Element) selectSingleNode(
					"/ink/traceGroup/traceGroup[@id="
							+ String.valueOf(TraceGroupIndex) + "]", root);
			NodeList nl = TempTraceGroup.getElementsByTagName("traceView");
			if (nl.getLength() == 0) {
				TempTraceGroup.getParentNode().removeChild(TempTraceGroup);
			}

		}

		saveInkml(fileName, xmldoc);

	}

	public void writeJtree(ArrayList<InkmlNode> nodes,
			ArrayList<InkmlArrow> arrows, String fileName) {
		readInkml(fileName);
		System.out.println("writeInkml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Element annotationXML = null, flowchart = null, theElem = null, theText = null, root = null;

		try {
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder db = factory.newDocumentBuilder();
			Document xmldoc = db.parse(new File(fileName));
			root = xmldoc.getDocumentElement();

			Element temp = (Element) selectSingleNode("/ink/annotationXML",
					root);
			if (temp != null) {
				temp.getParentNode().removeChild(temp);
			}
			annotationXML = xmldoc.createElement("annotationXML");

			annotationXML.setAttribute("type", "truth");
			annotationXML.setAttribute("encoding", "Content-FlowchartML");

			flowchart = xmldoc.createElement("flowchart");
			flowchart.setAttribute("xmlns", "LUNAM/IRCCyN/FlowchartML");

			for (InkmlNode node : nodes) {
				theElem = xmldoc.createElement("node");
				theElem.setAttribute("xml:id", id2href(node.getNodeId()));
				theElem.setAttribute("type", node.getType());
				theText = xmldoc.createElement("text");
				theText.setAttribute("xml:id", id2href(node.getText().getTextId()));
				theText.setTextContent(node.getText().getContent());
				theElem.appendChild(theText);
				flowchart.appendChild(theElem);
			}

			for (InkmlArrow arrow : arrows) {
				theElem = xmldoc.createElement("arrow");
				theElem.setAttribute("xml:id",id2href(arrow.getArrowId()));
				theElem.setAttribute("source", id2href(arrow.getSource()));
				theElem.setAttribute("target", id2href(arrow.getTarget()));
				if (arrow.getText() != null) {
					theText = xmldoc.createElement("text");
					theText.setAttribute("xml:id",id2href( arrow.getText().getTextId()));
					theText.setTextContent(arrow.getText().getContent());
					theElem.appendChild(theText);
				}
				flowchart.appendChild(theElem);
			}
			annotationXML.appendChild(flowchart);
			root.appendChild(annotationXML);

			saveInkml(fileName, xmldoc);
			System.out.println("写入jTree完毕");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void createHrefForTraceGroup(ArrayList<InkmlNode> nodes,
			ArrayList<InkmlArrow> arrows,ArrayList<InkmlText> texts, String fileName){
		readInkml(fileName);
	
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Element theHref=null,theElement=null, root = null;

		try {
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder db = factory.newDocumentBuilder();
			Document xmldoc = db.parse(new File(fileName));
			root = xmldoc.getDocumentElement();
			
//		Element temp= (Element) selectSingleNode(
//				"/ink/traceGroup/traceGroup[@id="
//						+ traceGroupId
//						+ "]", root);
//		
////		System.out.println("element:"+temp+"tt"+temp.getAttribute("xml:id"));
//		theHref=xmldoc.createElement("annotationXML" );
//		theHref.setAttribute("href", "text_1");
//		
//	
//		temp.appendChild(theHref);
	
		int nodeCount=0,arrowCount=0,textCount=0;
		
		for(TraceGroup tg:traceGroups){
			if(tg.getHref()==null){
				theHref = xmldoc.createElement("annotationXML");
				if(tg.getAnnotation().equals("arrow")){
					
					theHref.setAttribute("href", "arrow_"+arrowCount);
					arrowCount++;
				}else if(tg.getAnnotation().equals("text")){
					theHref.setAttribute("href", "text_"+textCount);
					textCount++;
				}else{
					theHref.setAttribute("href", "node_"+nodeCount);
					nodeCount++;
				}
				
				theElement= (Element) selectSingleNode(
						"/ink/traceGroup/traceGroup[@id="
								+ String.valueOf(tg.getTraceGroupIndex())
								+ "]", root);
				
				theElement.appendChild(theHref);
			}
		}
		
		saveInkml(fileName, xmldoc);
		System.out.println("写入content");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isHaveJtree(){
		return false;
	}
}
