package penInteraction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;





class MyMenuButton extends JButton{  
    private JPopupMenu menu;  
    private static int i=0;  
    
    
    public MyMenuButton(){  
        super();  
        
        this.setHorizontalTextPosition(SwingConstants.RIGHT );  
        addActionListener(new ActionListener(){  
            @Override  
            public void actionPerformed(ActionEvent arg0) {  
            

                    menu.show(MyMenuButton.this, 0, MyMenuButton.this.getHeight());  

            }  
        });  
        
      
     
    }  
    
    
//    public MyMenuButton(final String label){  
//        super(label);  
//        this.setText("¡ø "+label);  
//        this.setHorizontalTextPosition(SwingConstants.RIGHT );  
//        addActionListener(new ActionListener(){  
//            @Override  
//            public void actionPerformed(ActionEvent arg0) {  
//                if(isSelected()){  
//                    setText("¨‹"+label);  
//                    menu.show(MyMenuButton.this, 0, MyMenuButton.this.getHeight());  
//                }else{  
//                    setText("¡ø"+label);  
//                    menu.setVisible(false);  
//                }  
//            }  
//        });  
//    }  
    
    public MyMenuButton(ImageIcon imageIcon) {
		// TODO Auto-generated constructor stub
//    	super(imageIcon);  
        this.setIcon(imageIcon);
        this.setHorizontalTextPosition(SwingConstants.RIGHT );  
        addActionListener(new ActionListener(){  
            @Override  
            public void actionPerformed(ActionEvent arg0) {  
            
                    menu.show(MyMenuButton.this, 0, MyMenuButton.this.getHeight());  
               
                  
            }  
        });  
    	
	}
    
   

	public void addMenu(JPopupMenu menu){  
        this.menu=menu;  
    }  
    
    
}  

