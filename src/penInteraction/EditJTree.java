package penInteraction;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class EditJTree implements penInteraction.MyObserver {
	JFrame jf;

	JTree tree;
	// 上面JTree对象对应的model
	DefaultTreeModel model;
	MySubject s;
	//
	String FileName = "";
	MyCanvas canvas;

	public MyCanvas getCanvas() {
		return canvas;
	}

	public void setCanvas(MyCanvas canvas) {
		this.canvas = canvas;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	// 定义几个初始节点
	InkmlTreeNode root = new InkmlTreeNode("flowChart");
	// InkmlTreeNode guangdong = new InkmlTreeNode("广东");
	// InkmlTreeNode guangxi = new InkmlTreeNode("广西");
	// InkmlTreeNode foshan = new InkmlTreeNode("佛山");
	// InkmlTreeNode shantou = new InkmlTreeNode("汕头");
	// InkmlTreeNode guilin = new InkmlTreeNode("桂林");
	// InkmlTreeNode nanning = new InkmlTreeNode("南宁");

	// 定义需要被拖动的TreePath
	TreePath movePath;

	JButton addSiblingButton = new JButton("添加兄弟节点");
	JButton addChildButton = new JButton("添加子节点");
	JButton deleteButton = new JButton("删除节点");
	JButton editButton = new JButton("编辑当前节点");

	public void init(String filename) {

		InkmlHandler ih = new InkmlHandler();
		if (filename != "") {
			ih.readInkml(filename);
			ArrayList<InkmlNode> nodes = ih.nodes;
			ArrayList<InkmlArrow> arrows = ih.arrows;

			for (int i = 0; i < nodes.size(); i++) {
				InkmlNode tn = nodes.get(i);
				InkmlTreeNode tempNode1 = new InkmlTreeNode("node: "
						+ tn.getType() + " ( seg =" + tn.getType() + ")");

				InkmlTreeNode tempNode2 = new InkmlTreeNode("text: "
						+ tn.getText().getContent() + " ( seg =text)");
				tempNode2.setId(tn.getText().getTextId());
				tempNode1.add(tempNode2);
				tempNode1.setId(tn.getNodeId());
				root.add(tempNode1);
			}

			for (int i = 0; i < arrows.size(); i++) {
				InkmlArrow arrow = arrows.get(i);
				InkmlTreeNode tempNode1 = new InkmlTreeNode(
						"arrow ( seg =arrow)");

				InkmlTreeNode tempNode2 = new InkmlTreeNode("From -> Node: "
						+ Id2type(arrow.getSource(), nodes) + " ( seg ="
						+ Id2type(arrow.getSource(), nodes) + ")");

				InkmlTreeNode tempNode3 = new InkmlTreeNode("To -> Node: "
						+ Id2type(arrow.getTarget(), nodes) + " ( seg ="
						+ Id2type(arrow.getTarget(), nodes) + ")");

				tempNode2.setId(arrow.getSource());
				tempNode3.setId(arrow.getTarget());
				tempNode1.add(tempNode2);
				tempNode1.add(tempNode3);

				if (arrow.getText().getContent() != null) {
					InkmlTreeNode tempNode4 = new InkmlTreeNode("text: "
							+ arrow.getText().getContent() + " ( seg =text)");
					tempNode4.setId(arrow.getText().getTextId());
					tempNode1.add(tempNode4);
				}

				tempNode1.setId(arrow.getArrowId());
				root.add(tempNode1);
			}
		}

		jf = new JFrame("树");
		tree = new JTree(root);
		// 获取JTree对应的TreeModel对象
		model = (DefaultTreeModel) tree.getModel();
		// 设置JTree可编辑
		tree.setEditable(true);
		MouseListener ml = new MouseAdapter() {
			// 按下鼠标时候获得被拖动的节点
			public void mousePressed(MouseEvent e) {
				canvas.clear();
				canvas.paint(canvas.getGraphics());

				InkmlHandler ih = new InkmlHandler();
				ArrayList<TraceGroup> tg = null;
				if (canvas.filename != null) {
					ih.readInkml(canvas.filename);
					tg = ih.getTraceGroups();
					TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
					if (tp != null) {
						movePath = tp;
					}
					Object[] path = tp.getPath();
					InkmlTreeNode temp_ = (InkmlTreeNode) path[path.length - 1];

					String id = temp_.getId();
					String[] a = id.split("_");
					String type = a[0];
					int typeIndex = Integer.parseInt(a[1]);
					System.out.println(type + " " + typeIndex);
					int count = 0;
					int index = -1;
					for (int i = 0; i < tg.size(); i++) {
						if (tg.get(i).getAnnotation().equals(type)) {
							count++;
							if (count == typeIndex) {
								index = i;
								break;
							}
						}
					}

					System.out.println(index);
					canvas.drawRect(index);

				}

			}

			// 鼠标松开时获得需要拖到哪个父节点
			public void mouseReleased(MouseEvent e) {
				// 根据鼠标松开时的TreePath来获取TreePath
				// TreePath tp = tree.getPathForLocation(e.getX(), e.getY());

				// if (tp != null && movePath != null) {
				// // 阻止向子节点拖动
				// if (movePath.isDescendant(tp) && movePath != tp) {
				// JOptionPane.showMessageDialog(jf,
				// "目标节点是被移动节点的子节点，无法移动！", "非法操作",
				// JOptionPane.ERROR_MESSAGE);
				// return;
				// }
				// // 既不是向子节点移动，而且鼠标按下、松开的不是同一个节点
				// else if (movePath != tp) {
				// System.out.println(tp.getLastPathComponent());
				// // add方法可以先将原节点从原父节点删除，再添加到新父节点中
				// ((InkmlTreeNode) tp.getLastPathComponent())
				// .add((InkmlTreeNode) movePath
				// .getLastPathComponent());
				// movePath = null;
				// tree.updateUI();
				// }
				// }
			}
		};
		tree.addMouseListener(ml);

		tree.getCellEditor().addCellEditorListener(new CellEditorListener() {

			@Override
			public void editingCanceled(ChangeEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void editingStopped(ChangeEvent arg0) {
				// TODO Auto-generated method stub

				System.out.println(tree.getCellEditor().getCellEditorValue());
				tree.getModel().valueForPathChanged(tree.getSelectionPath(),
						tree.getCellEditor().getCellEditorValue() + "zzz");
				tree.updateUI();
			}

		});

		JPanel panel = new JPanel();

		addSiblingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// 获取选中节点
				InkmlTreeNode selectedNode = (InkmlTreeNode) tree
						.getLastSelectedPathComponent();
				// 如果节点为空，直接返回
				if (selectedNode == null)
					return;
				// 获取该选中节点的父节点
				InkmlTreeNode parent = (InkmlTreeNode) selectedNode.getParent();
				// 如果父节点为空，直接返回
				if (parent == null)
					return;
				// 创建一个新节点
				InkmlTreeNode newNode = new InkmlTreeNode("新节点");
				// 获取选中节点的选中索引
				int selectedIndex = parent.getIndex(selectedNode);
				// 在选中位置插入新节点
				model.insertNodeInto(newNode, parent, selectedIndex + 1);
				// --------下面代码实现显示新节点（自动展开父节点）-------
				// 获取从根节点到新节点的所有节点
				TreeNode[] nodes = model.getPathToRoot(newNode);
				// 使用指定的节点数组来创建TreePath
				TreePath path = new TreePath(nodes);
				// 显示指定TreePath
				tree.scrollPathToVisible(path);
			}
		});
		panel.add(addSiblingButton);

		addChildButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// 获取选中节点
				InkmlTreeNode selectedNode = (InkmlTreeNode) tree
						.getLastSelectedPathComponent();
				// 如果节点为空，直接返回
				if (selectedNode == null)
					return;
				// 创建一个新节点
				InkmlTreeNode newNode = new InkmlTreeNode("新节点");
				// 直接通过model来添加新节点，则无需通过调用JTree的updateUI方法
				// model.insertNodeInto(newNode, selectedNode,
				// selectedNode.getChildCount());
				// 直接通过节点添加新节点，则需要调用tree的updateUI方法
				selectedNode.add(newNode);
				// --------下面代码实现显示新节点（自动展开父节点）-------
				TreeNode[] nodes = model.getPathToRoot(newNode);
				TreePath path = new TreePath(nodes);
				tree.scrollPathToVisible(path);
				tree.updateUI();
			}
		});
		panel.add(addChildButton);

		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				InkmlTreeNode selectedNode = (InkmlTreeNode) tree
						.getLastSelectedPathComponent();
				if (selectedNode != null && selectedNode.getParent() != null) {
					// 删除指定节点
					model.removeNodeFromParent(selectedNode);
				}
			}
		});
		panel.add(deleteButton);

		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				TreePath selectedPath = tree.getSelectionPath();
				if (selectedPath != null) {
					// 编辑选中节点
					tree.startEditingAtPath(selectedPath);

					// System.out.println(selectedPath);
				}
			}
		});
		panel.add(editButton);

		expandAll(tree, new TreePath(root), true);

		jf.add(new JScrollPane(tree));
		jf.add(panel, BorderLayout.SOUTH);
		jf.pack();
		// jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);

		// tree.addTreeSelectionListener(new TreeSelectionListener(){
		//
		// @Override
		// public void valueChanged(TreeSelectionEvent arg0) {
		// // TODO Auto-generated method stub
		// DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree
		// .getLastSelectedPathComponent();
		// System.out.println(selectedNode.toString());
		// }
		//
		// });
	}

	private String Id2type(String m, ArrayList<InkmlNode> nodes) {
		// TODO Auto-generated method stub
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).getNodeId().equals(m)) {
				return nodes.get(i).getType();
			}
		}
		return null;
	}

	private static void expandAll(JTree tree, TreePath parent, boolean expand) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}

	@Override
	public void update(MySubject sb) {
		// TODO Auto-generated method stub
		canvas = (MyCanvas) sb;

	}

}
