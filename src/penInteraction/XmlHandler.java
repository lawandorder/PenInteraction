package penInteraction;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlHandler {

	Map<String,String[]> aMap=new HashMap<String,String[]>();
	public Map<String,String[]> ReadXml(String fileName) {
		// TODO Auto-generated method stub
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
	       Element root=null;
	    
	       try {
	           factory.setIgnoringElementContentWhitespace(true);
	           
	           DocumentBuilder db=factory.newDocumentBuilder();
	           Document xmldoc=db.parse(new File(fileName));
	           root=xmldoc.getDocumentElement();
	           
	           NodeList sometagTypes=root.getElementsByTagName("tagType");
	           System.out.println("获得所有图形类别");
	           System.out.println("--- 共有　"+sometagTypes.getLength()+"个图形类别。 ---");
	           for(int i=0;i<sometagTypes.getLength();i++) {
		           	Element tagType=(Element) sometagTypes.item(i);
		           	String key=tagType.getAttribute("name");
		           	NodeList tags= tagType.getElementsByTagName("tag");
		           	String[] temp=new String[tags.getLength()];
		               for (int j=0;j<tags.getLength();j++){
		               	Element tag=(Element) tags.item(j);
		                temp[j]=tag.getTextContent();   	
		               }
		               
		               aMap.put(key, temp);
	               
	               
	           }

	       
	       } catch (ParserConfigurationException e) {
	           e.printStackTrace();
	       } catch (SAXException e) {
	           e.printStackTrace();
	       } catch (IOException e) {
	           e.printStackTrace();
	       }
	       
	       return aMap;
	}
	
	
	

}
