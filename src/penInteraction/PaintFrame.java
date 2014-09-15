package penInteraction;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;

public class PaintFrame extends JFrame {
    private int initial_Width=0;
    private int initial_Height=0;
    private double initial_zoom=0;
	private static final long serialVersionUID = 1L;
	public MyCanvas canvas = new MyCanvas();
	private JTabbedPane tab = null;
	private JPanel jContentPane = null;
	Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	JButton button = new JButton(new ImageIcon("images/open.png"));
	JButton replay = new JButton(new ImageIcon("img/play.png"));
	JToggleButton mode1 = new JToggleButton(
			new ImageIcon("img/pencil_edit.png"));
	JButton mode2 = new JButton(new ImageIcon("img/chat_fill.png"));

	JMenuBar jMenuBar = new JMenuBar();
	private JScrollPane sketchscrollpane = null;
	JMenu jm1 = new JMenu("文件");
	JMenu jm2 = new JMenu("编辑");
	JMenu jm3 = new JMenu("帮助");

	JMenuItem jmi11 = new JMenuItem("打开");
	JMenuItem jmi12 = new JMenuItem("保存");

	JMenuItem jmi21 = new JMenuItem("编辑模式");
	JMenuItem jmi22 = new JMenuItem("查看模式");
	JMenuItem jmi23 = new JMenuItem("放大");
	JMenuItem jmi24 = new JMenuItem("缩小");

	JMenuItem jmi31 = new JMenuItem("软件信息");
	JMenuItem jmi32 = new JMenuItem("帮助信息");

	JToolBar toolBar = new JToolBar();

	JPopupMenu menu = new JPopupMenu();
	MyMenuButton menuButton = new MyMenuButton(new ImageIcon("img/opened.png"));
	JButton TraceDetailButton = new JButton(
			new ImageIcon("img/information.png"));

	JButton TreeButton = new JButton("XML");

	private int height = 0;
	private int width = 0;
	Map<String, String[]> zMap = new HashMap<String, String[]>();
	String key = null;
	String[] val = null;

	PaintFrame(String title) {
		super(title);
	
	 
		XmlHandler xh = new XmlHandler();
		zMap = xh.ReadXml("TagType.xml");

		jmi11.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				try {

					canvas.DrawImage();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		jmi12.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

			}

		});

		jmi21.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				canvas.setMode(1);
				canvas.clear();
				canvas.TraceIdList.clear();
				canvas.paintComponent(canvas.getGraphics());
			}

		});

		jmi22.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				canvas.setMode(0);
				canvas.clear();
				canvas.TraceIdList.clear();
				canvas.paintComponent(canvas.getGraphics());
			}

		});

		jmi23.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
		
				   if( initial_Width==0){
				    	 initial_Width=canvas.getWidth();
				    }
				   if( initial_Height==0){
				    	 initial_Height=canvas.getHeight();
				    }
				   if(initial_zoom==0){
					   initial_zoom=canvas.zoom; 
				   }
			
				canvas.zoom =canvas.zoom*2 ;
				if(canvas.zoom/ initial_zoom <= 1) {
					canvas.zoomx *= 2;
					canvas.zoomy *= 2;
				}

//				
				canvas.setPreferredSize(
					
					new Dimension(		
						(int)	(canvas.zoom/ initial_zoom* initial_Width),
						(int)	(canvas.zoom/ initial_zoom* initial_Height)));
//				canvas.zoomx /= canvas.zoom;
//				canvas.zoomy /= canvas.zoom;
				canvas.updateUI();
//				canvas.clear();
//				canvas.paint(canvas.getGraphics());
			}

		});

		jmi24.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				 if( initial_Width==0){
			    	 initial_Width=canvas.getWidth();
			    }
			   if( initial_Height==0){
			    	 initial_Height=canvas.getHeight();
			    }
			   if(initial_zoom==0){
				   initial_zoom=canvas.zoom; 
			   }
			   
			   
		
			canvas.zoom =canvas.zoom*0.5 ;
			double percent = canvas.zoom/ initial_zoom;
			if(canvas.zoom/ initial_zoom < 1) {
				percent = 1;
				canvas.zoomx *= 0.5;
				canvas.zoomy *= 0.5;
			}

//			
			canvas.setPreferredSize(
				
				new Dimension(		
					(int)	(percent* initial_Width),
					(int)	(percent* initial_Height)));
//			canvas.zoomx /= canvas.zoom;
//			canvas.zoomy /= canvas.zoom;
			canvas.updateUI();
			
//			canvas.clear();
//			canvas.paint(canvas.getGraphics());
			}

		});

		jm1.add(jmi11);
		jm1.add(jmi12);

		jm2.add(jmi21);
		jm2.add(jmi22);
		jm2.add(jmi23);
		jm2.add(jmi24);

		jm3.add(jmi31);
		jm3.add(jmi32);
		jMenuBar.add(jm1);
		jMenuBar.add(jm2);
		jMenuBar.add(jm3);

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try {

					canvas.DrawImage();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		replay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				canvas.ReplayImage();
				// System.out.println("回放1");
			}
		});

		mode1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (mode1.isSelected()) {
					// TODO Auto-generated method stub
					canvas.setMode(1);
					canvas.clear();
					canvas.TraceIdList.clear();
					canvas.paintComponent(canvas.getGraphics());

					// System.out.println("模式1");
				} else {
					canvas.setMode(0);
					canvas.clear();
					canvas.TraceIdList.clear();
					canvas.paintComponent(canvas.getGraphics());
				}
			}
		});

		mode2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				// canvas.ReplayImage();
				// canvas.setMode(2);
				// canvas.clear();
				//
				// canvas.paintComponent(canvas.getGraphics());
				// System.out.println("模式2");
			}
		});

		TraceDetailButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				TraceDetail tdt = new TraceDetail(canvas);

			}
		});

		TreeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

				AutoEditTree ej = new AutoEditTree();
				System.out.println(canvas.filename);
				if (canvas.filename != null) {
					ej.setCanvas(canvas);

					ej.init();

					canvas.attach(ej);
				}
			}
		});

		button.setBorderPainted(false);
		button.setPreferredSize(new Dimension(button.getIcon().getIconWidth(),
				button.getIcon().getIconHeight()));
		button.setToolTipText("打开");

		replay.setBorderPainted(false);
		replay.setPreferredSize(new Dimension(replay.getIcon().getIconWidth(),
				replay.getIcon().getIconHeight()));
		replay.setToolTipText("回放");

		mode1.setBorderPainted(false);
		mode1.setPreferredSize(new Dimension(mode1.getIcon().getIconWidth(),
				mode1.getIcon().getIconHeight()));
		mode1.setToolTipText("笔画分组");

		mode2.setBorderPainted(false);
		mode2.setPreferredSize(new Dimension(mode2.getIcon().getIconWidth(),
				mode2.getIcon().getIconHeight()));
		mode2.setToolTipText("显示标记");

		menuButton.setBorderPainted(false);
		menuButton.setPreferredSize(new Dimension(menuButton.getIcon()
				.getIconWidth(), menuButton.getIcon().getIconHeight()));
		menuButton.setToolTipText("选择图形类别");

		TraceDetailButton.setBorderPainted(false);
		TraceDetailButton.setPreferredSize(new Dimension(TraceDetailButton
				.getIcon().getIconWidth(), TraceDetailButton.getIcon()
				.getIconHeight()));
		TraceDetailButton.setToolTipText("查看笔画细节");

		TreeButton.setBorderPainted(false);
		// TreeButton.setPreferredSize(new Dimension(menuButton.getIcon()
		// .getIconWidth(), menuButton.getIcon().getIconHeight()));
		TreeButton.setToolTipText("修改树状结构");

		Container cp = getContentPane();

		Iterator iter = zMap.entrySet().iterator();

		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			key = (String) entry.getKey();
			val = (String[]) entry.getValue();
			JMenuItem mi = new JMenuItem(key);
			menu.add(mi);

			mi.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					canvas.popup.removeAll();

					JMenuItem j = (JMenuItem) arg0.getSource();

					// System.out.println(j.getText());
					canvas.prepareMenu(zMap.get(j.getText()));
				}

			});

			// System.out.println(key + "," + val.length);

		}

		// menu.add(new JMenuItem("流程图"));
		// menu.add(new JMenuItem("用例图"));
		// menu.add(new JMenuItem("顺序图"));
		menuButton.addMenu(menu);

		toolBar.add(button);
		toolBar.add(replay);
		toolBar.add(mode1);
		toolBar.add(mode2);
		toolBar.add(menuButton);
		toolBar.add(TraceDetailButton);
		toolBar.add(TreeButton);

		

		// cp.add(canvas);
		// cp.add(menuButton);

		setSize(scrSize.width, scrSize.height);
		width = this.getWidth();
		height = this.getHeight();

		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub

				// System.out.println("frame-show");
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				width = getWidth();
				height = getHeight();
				// System.out.println(width + " and " + height);
				// button.setBounds(0, 0, width, height / 6);
				// button.setBounds(0, 0, width / 4, height / 6);
				// replay.setBounds(width / 4, 0, width / 4, height / 6);
				// mode1.setBounds(width / 2, 0, width / 4, height / 6);
				// mode2.setBounds(width * 3 / 4, 0, width / 4, height / 6);
				//
			//	jMenuBar.setBounds(0, 0, width, height / 20);
				//toolBar.setBounds(0, height / 20, width, height / 20);
				//toolBar.setBorderPainted(false);
				// menuButton.setBounds(0, height/20, 400, height/20);
				//canvas.setBounds(0, height / 10, width, height * 18 / 20);
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub

				// System.out.println("frame-hidden");
			}
		});
		this.setJMenuBar(jMenuBar);
		this.setContentPane(getJContentPane());
		setVisible(true);
		// canvas.actionPerformed();
	}

	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());

			tab = new JTabbedPane(JTabbedPane.TOP);

			sketchscrollpane = new JScrollPane(canvas);
			sketchscrollpane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			sketchscrollpane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			tab.add("Draw", sketchscrollpane);

			jContentPane.add(toolBar, java.awt.BorderLayout.NORTH);

			jContentPane.add(tab, java.awt.BorderLayout.CENTER);
			System.out.println("getJContentPane");

		}
		return jContentPane;
	}

}
