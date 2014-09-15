package penInteraction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

public class TraceDetail{
    MyCanvas canvas;
	public TraceDetail(){
		
	}
	public TraceDetail(MyCanvas canvas_){
		this.canvas=canvas_;
		System.out.println("被点击的那些笔画:");
		PrintList(canvas.TraceIdList);
		String[] Names={"TraceId","X","Y"};
		InkmlHandler ih=new InkmlHandler();
		if(canvas.filename!=null){
		ih.readInkml(canvas.filename);
		ArrayList<Trace> traces=ih.traces;
		
		int count=0;
		for(int i=0;i<traces.size();i++){
			count+=traces.get(i).points.size();
		}
		
		Object[][] TraceInfo=new Object[count][3];
		int PreCount=-1;
		for(int i=0;i<traces.size();i++){
			Trace tempTrace=traces.get(i);
			for(int j=0;j<tempTrace.points.size();j++){
				PreCount++;
				TraceInfo[PreCount][0]=tempTrace.traceIndex;
				TraceInfo[PreCount][1]=tempTrace.points.get(j).x;
				TraceInfo[PreCount][2]=tempTrace.points.get(j).y;
			}
		}
		
		
		 	JTable table=new JTable(TraceInfo,Names){
		 		 DateRenderer dateRenderer = new DateRenderer();
		         public TableCellRenderer getCellRenderer(int row, int column) {
		                 return dateRenderer;
		         }
		 	};
		 	
            ArrayList<Integer> pointIndexList=new ArrayList<Integer>();
		 	for(int i=0;i<count;i++){
		 		int a=(Integer) TraceInfo[i][0];
		 		
		 		if(canvas.TraceIdList.contains(a)){
		 			pointIndexList.add(i);
		 		}
		 	
		 	}
		
//            PrintList(pointIndexList);
		 	DateRenderer t= ((DateRenderer) table.getCellRenderer(9999, 3));
		 	t.setColor(pointIndexList,  Color.RED);
		 
		 	
		    table.setPreferredScrollableViewportSize(new Dimension(550,300));
		    JScrollPane scrollPane=new JScrollPane(table);
		    JLabel jLable =new JLabel("查看笔画");
		    final JTextField jText=new JTextField(15);
		    
		    JButton jb=new JButton("确定");
		    JFrame f=new JFrame();
		    jb.addActionListener(new ActionListener(){
		    		
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					int input=Integer.parseInt(jText.getText());
					InkmlHandler ih=new InkmlHandler();
					if(canvas.filename!=null){
						ih.readInkml(canvas.filename);
						ArrayList<Trace> traces=ih.traces;
					    int start=traces.get(0).getTraceIndex();
						int end=traces.get(traces.size()-1).getTraceIndex();	
						if(input>=start&&input<=end){
							canvas.clear();
							canvas.paint(canvas.getGraphics());
							canvas.selectTrace(canvas.getGraphics(),input);
						}else{
							JOptionPane.showMessageDialog(null, "请输入正确笔画", "请输入正确笔画", JOptionPane.ERROR_MESSAGE); 
							
						}
					}
				}
				}
		    
		    );
		    f.setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
		    f.getContentPane().add(scrollPane);
		    f.getContentPane().add(jLable);
		    f.getContentPane().add(jText);
		    f.getContentPane().add(jb);
		    f.setTitle("Simple Table");
		    f.pack();
		    f.setVisible(true);
		    System.out.println(table.getHeight()+","+scrollPane.getHeight());
		}
		   
	}
	
	private void PrintList(ArrayList<Integer> pointIndexList) {
		// TODO Auto-generated method stub
		for(int i=0;i<pointIndexList.size();i++){
			System.out.println(pointIndexList.get(i));
		}
	}
}
