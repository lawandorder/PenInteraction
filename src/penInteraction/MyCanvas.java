package penInteraction;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class MyCanvas extends JPanel implements MouseListener,
		MouseMotionListener, ActionListener, MySubject {

	/**
* 
*/
	ArrayList<MyObserver> obList = new ArrayList<MyObserver>();
	
	private static final long serialVersionUID = 1L;
	public InkmlHandler inkmlHandler;
	public double zoomx;
	public double zoomy;
	public double k_factor = 0.33;
	int traceId = -1;
	int mode;
	ArrayList<Integer> TraceIdList = new ArrayList<Integer>();
	PopupMenu popup;
	String filename;
	int first = -1;
	int second = -1;
	EditJTree ejTree;
	public double zoom;
	private HashMap<String,String> id_content=new HashMap<String,String>();
	private HashMap<String,String[]> arrow_SaT=new HashMap<String,String[]>();
	ArrayList<Color> colorList=new ArrayList<Color>();
	
	public HashMap<String, String[]> getArrow_SaT() {
		return arrow_SaT;
	}


	public void setArrow_SaT(HashMap<String, String[]> arrow_SaT) {
		this.arrow_SaT = arrow_SaT;
	}


	MyCanvas() {

		inkmlHandler = new InkmlHandler();
		popup = new PopupMenu("A Popup Menu");
		zoom=1;
		//this.setPreferredSize(new Dimension(300, 200));
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.add(popup);

		// this.add(show);
		// this.add(jtextfield);

		this.setLayout(null);

		// show.setBounds(50, 50, 30, 30);

	}
	

	public HashMap getId_content() {
		return id_content;
	}


	public void setId_content(HashMap id_content) {
		this.id_content = id_content;
	}


	public void prepareMenu(String[] a) {
		// TODO Auto-generated method stub

		for (int i = 0; i < a.length; i++) {
			MenuItem temp = new MenuItem(a[i]);
			popup.add(temp);
			popup.addSeparator();
			temp.setActionCommand(a[i]);
		}

		popup.addActionListener(this);

	}

	@Override
	public void paintComponent(Graphics g) {
		// System.out.println("组件" + this.getComponentCount());
//		zoomx = (double) getWidth()
//				/ Toolkit.getDefaultToolkit().getScreenSize().width;
//		zoomy = (double) getHeight()
//				/ Toolkit.getDefaultToolkit().getScreenSize().height;
//	
		super.paintComponent(g);
		ArrayList<Trace> traces = inkmlHandler.getTraces();
		//g = this.getGraphics();
		paintTraces(g, traces);
		if (mode == 0) {
			if (traceId >= 0) {
				TraceGroup traceGroup = getTraceGroup(traceId);
				if (traceGroup != null) {
					g.setColor(Color.BLACK);
					CaculateLabelLocation(traceGroup, g);
					SelectItem(g, traceGroup, traces);
				}
			}

		} else if (mode == 1) {
			// ShowElement();
			if (filename != null) {
				inkmlHandler.readInkml(filename);
				ArrayList<TraceGroup> traceGroups = inkmlHandler.traceGroups;

				for (int i = 0; i < traceGroups.size(); i++) {
					// System.out.println("元素加粗显示");
					g.setColor(Color.BLACK);
					CaculateLabelLocation(traceGroups.get(i), g);
					SelectItem(g, traceGroups.get(i), traces,i);
				}

				for (int i = 0; i < TraceIdList.size(); i++) {
					// System.out.println("元素加粗显示");
					g.setColor(Color.BLACK);

					SelectTrace(g, (TraceIdList.get(i)));
				}
			}

		}

		notifyObservers();
	}

	public void clear() {
		Graphics g = getGraphics();
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
	}

	protected void DrawImage() throws Exception {
		// TODO Auto-generated method stub
		 zoomx = (double) this.getWidth()
					/ Toolkit.getDefaultToolkit().getScreenSize().width;
			 zoomy = (double) this.getHeight()
					/ Toolkit.getDefaultToolkit().getScreenSize().height;
		Graphics g = getGraphics();
		g.setColor(Color.BLACK);
		JFileChooser fc = null;
		// filename = "";
		File f = null;

		try {
			f = new File("path.txt");
			if (f.exists()) {
				System.out.print("文件存在");
				BufferedReader input = new BufferedReader(new FileReader(f));
				String filePath = input.readLine();

				fc = new JFileChooser(filePath);

			} else {
				System.out.print("文件不存在");
				f.createNewFile();// 不存在则创建
				fc = new JFileChooser("F:\\ejava\\test\\src\\Windows");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		int openResult = fc.showOpenDialog(getParent());// 参数为父窗口null默认就为父
		if (openResult == JFileChooser.APPROVE_OPTION) {
			clear();
			mode = 0;
			// this.removeAll();
			filename = (fc.getSelectedFile().getAbsolutePath());
			BufferedWriter output;
			try {
				output = new BufferedWriter(new FileWriter(f));
				output.write(fc.getCurrentDirectory().toString());
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			traceId = -1;
			inkmlHandler.readInkml(filename);

			ArrayList<Trace> traces = inkmlHandler.getTraces();

			paintTraces(g, traces);
			notifyObservers();

		} else {

			System.out.println("filename:" + filename);

			clear();

			paintComponent(g);
		}

	}

	public void paintTraces(Graphics g, ArrayList<Trace> traces) {
		// TODO Auto-generated method stub


		k_factor = kFactor(traces);

		for (Trace trace : traces) {

			penInteraction.Point pt0 = trace.getPoints().get(0);
			for (int i = 1; i < trace.getPoints().size(); ++i) {
				penInteraction.Point pt = trace.getPoints().get(i);
				g.drawLine((int) (pt0.x * k_factor * zoomx), (int) (pt0.y
						* k_factor * zoomy), (int) (pt.x * k_factor * zoomx),
						(int) (pt.y * k_factor * zoomy));
				pt0 = pt;
			}

		}
	}

	private double kFactor(ArrayList<Trace> traces) {
		// TODO Auto-generated method stub
		double MinX = Double.MAX_VALUE;
		double MinY = Double.MAX_VALUE;
		double MaxX = -1;
		double MaxY = -1;

		for (int i = 0; i < traces.size(); i++) {
			Trace tempTrace = traces.get(i);
			for (int j = 0; j < tempTrace.getPoints().size(); j++) {
				Point tempPoint = tempTrace.getPoints().get(j);
				if (tempPoint.x < MinX) {
					MinX = tempPoint.x;
				}
				if (tempPoint.x > MaxX) {
					MaxX = tempPoint.x;
				}
				if (tempPoint.y < MinY) {
					MinY = tempPoint.y;
				}
				if (tempPoint.y > MaxY) {
					MaxY = tempPoint.y;
				}
			}
		}

		double Xgap = MaxX - MinX;
		double Ygap = MaxY - MinY;
		k_factor = Math.min(this.getWidth() / Xgap, this.getHeight() / Ygap) * 0.9;

		// System.out
		// .println("Xgap:" + Xgap + "Ygap:" + Ygap + "缩放系数：" + k_factor);
		return k_factor;
	}

	public void paintTracesWithWait(Graphics g, ArrayList<Trace> traces) {
		// TODO Auto-generated method stub
//		double zoomx = (double) this.getWidth()
//				/ Toolkit.getDefaultToolkit().getScreenSize().width;
//		double zoomy = (double) this.getHeight()
//				/ Toolkit.getDefaultToolkit().getScreenSize().height;
		for (Trace trace : traces) {

			penInteraction.Point pt0 = trace.getPoints().get(0);
			for (int i = 1; i < trace.getPoints().size(); ++i) {
				penInteraction.Point pt = trace.getPoints().get(i);
				g.drawLine((int) (pt0.x * k_factor * zoomx), (int) (pt0.y
						* k_factor * zoomy), (int) (pt.x * k_factor * zoomx),
						(int) (pt.y * k_factor * zoomy));
				pt0 = pt;
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	// public void repaintTraces() {
	// Graphics g = getGraphics();
	// g.setColor(Color.BLACK);
	//
	// ArrayList<Trace> traces = inkmlHandler.getTraces();
	// paintTraces(g, traces);
	// if (traceId >= 0) {
	// TraceGroup traceGroup = getTraceGroup(traceId);
	// CaculateLabelLocation(traceGroup);
	// SelectItem(g, traceGroup, traces);
	// }
	// }

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println("============点击点坐标==================");
		// System.out.println("x:"+arg0.getX()+",y:"+arg0.getY());
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if (mode == 0) {
	

		}

		if (mode == 1) {

			if (arg0.getButton() == 3) {
				popup.show(this, arg0.getX(), arg0.getY());
				System.out.println("右键被按下");
			} else if (arg0.getModifiersEx() == (arg0.SHIFT_DOWN_MASK + arg0.BUTTON1_DOWN_MASK)) {

				System.out.println("shift+左键");
				int x = arg0.getX();
				int y = arg0.getY();
				Graphics g = getGraphics();

				ArrayList<Trace> traces = inkmlHandler.getTraces();
				traceId = GetClosestTrace(x, y);

				if (traceId != -1) {
					if (first == -1) {
						first = traceId;
					} else {
						second = traceId;
					}
					
					System.out.println("first:" + first + ",second:" + second);
					g.setColor(Color.BLACK);
					if(first!=-1&&second!=-1){
						int max = Math.max(first, second);
						int min = Math.min(first, second);
						for (int i = min; i <= max; i++) {
							if (!TraceIdList.contains(i)) {
								TraceIdList.add(i);
							}

							SelectTrace(g, i);
						}
					}
					
					// paintTraces(g, traces);
					

				}

			} else {
				int x = arg0.getX();
				int y = arg0.getY();
				Graphics g = getGraphics();
				ArrayList<Trace> traces = inkmlHandler.getTraces();
				traceId = GetClosestTrace(x, y);

				if (traceId != -1) {
					if (first == -1) {
						first = traceId;
					}

					if (!TraceIdList.contains(traceId)) {
						TraceIdList.add(traceId);
					}

					g.setColor(Color.BLACK);
					// paintTraces(g, traces);

					SelectTrace(g, traceId);

				}

			}

		}

	}

	private void SelectTrace(Graphics g, int traceId2) {
		// TODO Auto-generated method stub
		Graphics2D gg = (Graphics2D) g;
		gg.setStroke(new BasicStroke(2));

		ArrayList<Trace> traces = inkmlHandler.getTraces();
		Trace temptrace = traces.get(traceId2);
		penInteraction.Point pt0 = temptrace.getPoints().get(0);
		for (int j = 1; j < temptrace.getPoints().size(); j++) {

			penInteraction.Point pt = temptrace.getPoints().get(j);

			gg.drawLine((int) (pt0.x * k_factor * zoomx), (int) (pt0.y
					* k_factor * zoomy), (int) (pt.x * k_factor * zoomx),
					(int) (pt.y * k_factor * zoomy));
			pt0 = pt;
		}

	}
	
	public void selectTrace(Graphics g, int traceId2){
		SelectTrace(g,traceId2);
	}

	private int GetClosestTrace(int x, int y) {
		// TODO Auto-generated method stub
		double MinDistance = Double.MAX_VALUE;
		int tempID = -1;
		ArrayList<Trace> traces = inkmlHandler.getTraces();
		// System.out.println(traces.size());
		for (int i = 0; i < traces.size(); i++) {
			Trace trace = traces.get(i);
			// System.out.println("trace" + i + "中点的个数："
			// + trace.getPoints().size());
			for (int j = 0; j < trace.getPoints().size(); j++) {

				penInteraction.Point pt = trace.getPoints().get(j);
				if (distance(x, y, pt.x * k_factor * zoomx, pt.y * k_factor
						* zoomy) < MinDistance) {

					MinDistance = distance(x, y, pt.x * k_factor * zoomx, pt.y
							* k_factor * zoomy);
					// System.out.println(MinDistance);
					tempID = trace.traceIndex;
					// System.out.println(traceId);
				}
			}
		}

		if (MinDistance >= this.getHeight() / 72) {
			tempID = -1;
		}
		return tempID;

	}

	private void CaculateLabelLocation(TraceGroup traceGroup, Graphics g) {
		// TODO Auto-generated method stub
		double MaxX = -1;
		double MinX = 10000;
		double MaxY = -1;
		double MinY = 10000;
		if (traceGroup != null) {
			ArrayList<Integer> refs = traceGroup.traceDataRefs;
			ArrayList<Trace> traces = inkmlHandler.getTraces();
			for (int i = 0; i < refs.size(); i++) {

				Trace temptrace = traces.get(refs.get(i));
				ArrayList<penInteraction.Point> tempPoints = temptrace.points;
				for (int j = 0; j < tempPoints.size(); j++) {

					if (tempPoints.get(j).x > MaxX) {
						MaxX = tempPoints.get(j).x;
					}

					if (tempPoints.get(j).x < MinX) {
						MinX = tempPoints.get(j).x;
					}
					if (tempPoints.get(j).y > MaxY) {
						MaxY = tempPoints.get(j).y;
					}

					if (tempPoints.get(j).y < MinY) {
						MinY = tempPoints.get(j).y;
					}
				}
			}

			String annotation = traceGroup.annotation;
			Font font = new Font(annotation, Font.PLAIN, (int) (15 * zoomy));

			if (annotation.equals("text")) {
				// g.setFont(font);
				// g.drawString(annotation, (int) ((MaxX) * k_factor * zoomx),
				// (int) (MaxY * k_factor * zoomy));
				g.setFont(font);
				g.drawString(annotation, (int) ((MinX) * k_factor * zoomx),
						(int) (MinY * k_factor * zoomy));
			} else if (annotation.equals("arrow")) {
				g.setFont(font);
				g.drawString(annotation,
						(int) ((MinX + MaxX) / 2 * k_factor * zoomx),
						(int) ((MinY + MaxY) / 2 * k_factor * zoomy));
			} else {
				g.setFont(font);
				g.drawString(annotation, (int) ((MaxX) * k_factor * zoomx),
						(int) ((MinY + MaxY) / 2 * k_factor * zoomy));
			}

			float[] dash1 = { 5.0f };
			BasicStroke s = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
			Rectangle2D mfRect = new Rectangle2D.Float();

			Graphics2D g2d = (Graphics2D) g;
			// 设置边框颜色
			g2d.setColor(Color.BLACK);
			// 设置边框范围

			if (!annotation.equals("process")) {
				mfRect.setRect(MinX * k_factor * zoomx,
						MinY * k_factor * zoomy, (MaxX - MinX) * k_factor
								* zoomx, (MaxY - MinY) * k_factor * zoomy);
			} else {

				mfRect.setRect((MinX - 25) * k_factor * zoomx, (MinY - 25)
						* k_factor * zoomy, (MaxX - MinX + 50) * k_factor
						* zoomx, (MaxY - MinY + 50) * k_factor * zoomy);
			}
			// 设置边框类型
			g2d.setStroke(s);

			g2d.draw(mfRect);

		}
	}

	private void CaculateLabelLocation(ArrayList<TraceGroup> traceGroups,
			Graphics g) {
		// TODO Auto-generated method stub

		for (int k = 0; k < traceGroups.size(); k++) {

			double MaxX = -1;
			double MinX = 10000;
			double MaxY = -1;
			double MinY = 10000;
			if (traceGroups.get(k) != null) {

				ArrayList<Integer> refs = traceGroups.get(k).traceDataRefs;
				ArrayList<Trace> traces = inkmlHandler.getTraces();
				for (int i = 0; i < refs.size(); i++) {

					Trace temptrace = traces.get(refs.get(i));
					ArrayList<penInteraction.Point> tempPoints = temptrace.points;
					for (int j = 0; j < tempPoints.size(); j++) {

						if (tempPoints.get(j).x > MaxX) {
							MaxX = tempPoints.get(j).x;
						}

						if (tempPoints.get(j).x < MinX) {
							MinX = tempPoints.get(j).x;
						}
						if (tempPoints.get(j).y > MaxY) {
							MaxY = tempPoints.get(j).y;
						}

						if (tempPoints.get(j).y < MinY) {
							MinY = tempPoints.get(j).y;
						}
					}
				}

				String annotation = traceGroups.get(k).annotation;

				Font font = new Font(annotation, Font.PLAIN, (int) (15 * zoomy));

				// System.out.println(jlabel.getWidth()+" jlabel "+jlabel.getHeight());
				if (annotation.equals("text")) {
					g.setFont(font);
					g.drawString(annotation, (int) ((MinX) * k_factor * zoomx),
							(int) (MinY * k_factor * zoomy));
				} else if (annotation.equals("arrow")) {
					g.setFont(font);
					g.drawString(annotation, (int) ((MinX + MaxX) / 2
							* k_factor * zoomx), (int) ((MinY + MaxY) / 2
							* k_factor * zoomy));
				} else {
					g.setFont(font);
					g.drawString(annotation, (int) ((MaxX) * k_factor * zoomx),
							(int) ((MinY + MaxY) / 2 * k_factor * zoomy));
				}
			}
		}

	}

	private TraceGroup getTraceGroup(int traceId) {
		// TODO Auto-generated method stub
		ArrayList<TraceGroup> traceGroups = inkmlHandler.getTraceGroups();
		TraceGroup ttg = null;
		int TraceGroupId = -1;
		for (int i = 0; i < traceGroups.size(); i++) {
			TraceGroup tempTraceGroup = traceGroups.get(i);
			if (tempTraceGroup.traceDataRefs.contains(traceId)) {
				TraceGroupId = i;
				break;
			}
		}
		if (TraceGroupId != -1) {
			ttg = traceGroups.get(TraceGroupId);
		}

		return ttg;
	}

	private void SelectItem(Graphics g, TraceGroup traceGroup,
			ArrayList<Trace> traces,int index) {
		// TODO Auto-generated method stub
		ArrayList<TraceGroup> traceGroups = inkmlHandler.getTraceGroups();
		if (traceGroup != null) {
			Graphics2D gg = (Graphics2D) g;
			gg.setStroke(new BasicStroke(2));
			String annotation = traceGroup.annotation;
			if(colorList.size()<traceGroups.size()){
				gg.setColor(getColor(annotation));
				colorList.add(getColor(annotation));
			}else{
                gg.setColor(colorList.get(index));				
			}
		
			ArrayList<Integer> refs = traceGroup.traceDataRefs;
			for (int i = 0; i < refs.size(); i++) {
				int traceIndex = refs.get(i);
				SelectTrace(gg, traceIndex);
				// penInteraction.Point pt0 = temptrace.getPoints().get(0);
				// for (int j = 1; j < temptrace.getPoints().size(); j++) {
				//
				// penInteraction.Point pt = temptrace.getPoints().get(j);
				//
				// gg.drawLine((int) (pt0.x * k_factor * zoomx), (int) (pt0.y
				// * k_factor * zoomy),
				// (int) (pt.x * k_factor * zoomx), (int) (pt.y
				// * k_factor * zoomy));
				// pt0 = pt;
				// }
			}
		}
	}

	
	private void SelectItem(Graphics g, TraceGroup traceGroup,
			ArrayList<Trace> traces) {
		// TODO Auto-generated method stub
		ArrayList<TraceGroup> traceGroups = inkmlHandler.getTraceGroups();
		if (traceGroup != null) {
			Graphics2D gg = (Graphics2D) g;
			gg.setStroke(new BasicStroke(2));
			String annotation = traceGroup.annotation;
			
				gg.setColor(getColor(annotation));
			
		
			ArrayList<Integer> refs = traceGroup.traceDataRefs;
			for (int i = 0; i < refs.size(); i++) {
				int traceIndex = refs.get(i);
				SelectTrace(gg, traceIndex);
				// penInteraction.Point pt0 = temptrace.getPoints().get(0);
				// for (int j = 1; j < temptrace.getPoints().size(); j++) {
				//
				// penInteraction.Point pt = temptrace.getPoints().get(j);
				//
				// gg.drawLine((int) (pt0.x * k_factor * zoomx), (int) (pt0.y
				// * k_factor * zoomy),
				// (int) (pt.x * k_factor * zoomx), (int) (pt.y
				// * k_factor * zoomy));
				// pt0 = pt;
				// }
			}
		}
	}
	private Color getColor(String annotation) {
		// TODO Auto-generated method stub
		ArrayList<TraceGroup> traceGroups = inkmlHandler.getTraceGroups();
		HashSet<String> types=new HashSet<String>();
		for(TraceGroup tg:traceGroups ){
			types.add(tg.getAnnotation());
		}
		
		int typeNum=types.size();
		int[] a=new int[256];
		for(int i=0;i<a.length;i++){
			a[i]=i;
		}
		
		Color color=null;
				
		
		
		for(String type:types){
			if(annotation.equals(type)){
				color=new Color(a[(int)(256*Math.random())],a[(int)(256*Math.random())],a[(int)(256*Math.random())]);;
			}
		}
		
		return color;
	}

	private double distance(int x, int y, double x2, double y2) {
		// TODO Auto-generated method stub
		return Math.sqrt(Math.pow((x - x2), 2) + Math.pow((y - y2), 2));
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void ReplayImage() {
		// TODO Auto-generated method stub
		clear();
		Graphics g = getGraphics();
		g.setColor(Color.BLACK);
		ArrayList<Trace> traces = inkmlHandler.getTraces();
		traceId = -1;
		paintTracesWithWait(g, traces);

	}

	public void setMode(int i) {
		mode = i;
		clear();
		Graphics g = getGraphics();
		g.setColor(Color.BLACK);
		ArrayList<Trace> traces = inkmlHandler.getTraces();
		traceId = -1;
		paintTraces(g, traces);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		first = -1;
		second = -1;
		String annotation = e.getActionCommand();
		inkmlHandler.writeInkml(TraceIdList, annotation, filename);
		TraceIdList.clear();
		clear();

		Graphics g = getGraphics();
		ArrayList<Trace> traces = inkmlHandler.getTraces();

		g.setColor(Color.BLACK);
		paintTraces(g, traces);
		paintComponent(g);
	}

	@Override
	public void attach(penInteraction.MyObserver observer) {
		// TODO Auto-generated method stub
		obList.add(observer);
	}

	@Override
	public void detach(penInteraction.MyObserver observer) {
		// TODO Auto-generated method stub
		obList.remove(observer);
	}

	@Override
	public void notifyObservers() {
		// TODO Auto-generated method stub
		for (MyObserver ob : obList) {
			ob.update(this);

		}
	}

	public void drawRect(int index) {
		double MaxX = -1;
		double MinX = 10000;
		double MaxY = -1;
		double MinY = 10000;
		Graphics g = this.getGraphics();
		ArrayList<TraceGroup> traceGroups = inkmlHandler.getTraceGroups();
		TraceGroup traceGroup = traceGroups.get(index);
		if (traceGroup != null) {
			ArrayList<Integer> refs = traceGroup.traceDataRefs;
			ArrayList<Trace> traces = inkmlHandler.getTraces();
			for (int i = 0; i < refs.size(); i++) {

				Trace temptrace = traces.get(refs.get(i));
				ArrayList<penInteraction.Point> tempPoints = temptrace.points;
				for (int j = 0; j < tempPoints.size(); j++) {

					if (tempPoints.get(j).x > MaxX) {
						MaxX = tempPoints.get(j).x;
					}

					if (tempPoints.get(j).x < MinX) {
						MinX = tempPoints.get(j).x;
					}
					if (tempPoints.get(j).y > MaxY) {
						MaxY = tempPoints.get(j).y;
					}

					if (tempPoints.get(j).y < MinY) {
						MinY = tempPoints.get(j).y;
					}
				}
			}

			String annotation = traceGroup.annotation;

			// float[] dash1 = { 5.0f };
			// BasicStroke s = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
			// BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
			Rectangle2D mfRect = new Rectangle2D.Float();

			Graphics2D g2d = (Graphics2D) g;
			// 设置边框颜色
			g2d.setColor(Color.BLACK);
			// 设置边框范围

			if (!annotation.equals("process")) {
				mfRect.setRect(MinX * k_factor * zoomx,
						MinY * k_factor * zoomy, (MaxX - MinX) * k_factor
								* zoomx, (MaxY - MinY) * k_factor * zoomy);
			} else {

				mfRect.setRect((MinX - 25) * k_factor * zoomx, (MinY - 25)
						* k_factor * zoomy, (MaxX - MinX + 50) * k_factor
						* zoomx, (MaxY - MinY + 50) * k_factor * zoomy);
			}
			// 设置边框类型
			// g2d.setStroke(s);

			g2d.draw(mfRect);

		}
	}
	// public void ShowElement() {
	// // TODO Auto-generated method stub
	//
	// inkmlHandler.readInkml(filename);
	// ArrayList<Trace> traces = inkmlHandler.getTraces();
	// ArrayList<TraceGroup> traceGroups = inkmlHandler.traceGroups;
	// Graphics g = getGraphics();
	// g.setColor(Color.BLACK);
	// paintTraces(g, traces);
	//
	// CaculateLabelLocation(traceGroups, g);
	//
	// for (int i = 0; i < traceGroups.size(); i++) {
	// // System.out.println("元素加粗显示");
	// SelectItem(g, traceGroups.get(i), traces);
	// }
	//
	// }

}
