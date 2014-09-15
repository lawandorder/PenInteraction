package penInteraction;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DateRenderer extends DefaultTableCellRenderer {
	  int n;
      ArrayList<Integer> selected;
      Color c;
      
	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column){
            Component com =  super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus,
                row, column);
            if (selected.contains(row))//你要变色的行
                com.setBackground(c);
            
            
            else com.setBackground(null);
            
           

            return com;
        }
      
        public void setColor(int row, Color color){
            n = row;
            c=color;
        }

		public void setColor(ArrayList<Integer> pointIndexList, Color color) {
			// TODO Auto-generated method stub
			selected=pointIndexList;
			c=color;
		}
}