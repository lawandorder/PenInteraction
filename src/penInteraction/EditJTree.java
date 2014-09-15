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
	// ����JTree�����Ӧ��model
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

	// ���弸����ʼ�ڵ�
	InkmlTreeNode root = new InkmlTreeNode("flowChart");
	// InkmlTreeNode guangdong = new InkmlTreeNode("�㶫");
	// InkmlTreeNode guangxi = new InkmlTreeNode("����");
	// InkmlTreeNode foshan = new InkmlTreeNode("��ɽ");
	// InkmlTreeNode shantou = new InkmlTreeNode("��ͷ");
	// InkmlTreeNode guilin = new InkmlTreeNode("����");
	// InkmlTreeNode nanning = new InkmlTreeNode("����");

	// ������Ҫ���϶���TreePath
	TreePath movePath;

	JButton addSiblingButton = new JButton("����ֵܽڵ�");
	JButton addChildButton = new JButton("����ӽڵ�");
	JButton deleteButton = new JButton("ɾ���ڵ�");
	JButton editButton = new JButton("�༭��ǰ�ڵ�");

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

		jf = new JFrame("��");
		tree = new JTree(root);
		// ��ȡJTree��Ӧ��TreeModel����
		model = (DefaultTreeModel) tree.getModel();
		// ����JTree�ɱ༭
		tree.setEditable(true);
		MouseListener ml = new MouseAdapter() {
			// �������ʱ���ñ��϶��Ľڵ�
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

			// ����ɿ�ʱ�����Ҫ�ϵ��ĸ����ڵ�
			public void mouseReleased(MouseEvent e) {
				// ��������ɿ�ʱ��TreePath����ȡTreePath
				// TreePath tp = tree.getPathForLocation(e.getX(), e.getY());

				// if (tp != null && movePath != null) {
				// // ��ֹ���ӽڵ��϶�
				// if (movePath.isDescendant(tp) && movePath != tp) {
				// JOptionPane.showMessageDialog(jf,
				// "Ŀ��ڵ��Ǳ��ƶ��ڵ���ӽڵ㣬�޷��ƶ���", "�Ƿ�����",
				// JOptionPane.ERROR_MESSAGE);
				// return;
				// }
				// // �Ȳ������ӽڵ��ƶ���������갴�¡��ɿ��Ĳ���ͬһ���ڵ�
				// else if (movePath != tp) {
				// System.out.println(tp.getLastPathComponent());
				// // add���������Ƚ�ԭ�ڵ��ԭ���ڵ�ɾ��������ӵ��¸��ڵ���
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
				// ��ȡѡ�нڵ�
				InkmlTreeNode selectedNode = (InkmlTreeNode) tree
						.getLastSelectedPathComponent();
				// ����ڵ�Ϊ�գ�ֱ�ӷ���
				if (selectedNode == null)
					return;
				// ��ȡ��ѡ�нڵ�ĸ��ڵ�
				InkmlTreeNode parent = (InkmlTreeNode) selectedNode.getParent();
				// ������ڵ�Ϊ�գ�ֱ�ӷ���
				if (parent == null)
					return;
				// ����һ���½ڵ�
				InkmlTreeNode newNode = new InkmlTreeNode("�½ڵ�");
				// ��ȡѡ�нڵ��ѡ������
				int selectedIndex = parent.getIndex(selectedNode);
				// ��ѡ��λ�ò����½ڵ�
				model.insertNodeInto(newNode, parent, selectedIndex + 1);
				// --------�������ʵ����ʾ�½ڵ㣨�Զ�չ�����ڵ㣩-------
				// ��ȡ�Ӹ��ڵ㵽�½ڵ�����нڵ�
				TreeNode[] nodes = model.getPathToRoot(newNode);
				// ʹ��ָ���Ľڵ�����������TreePath
				TreePath path = new TreePath(nodes);
				// ��ʾָ��TreePath
				tree.scrollPathToVisible(path);
			}
		});
		panel.add(addSiblingButton);

		addChildButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// ��ȡѡ�нڵ�
				InkmlTreeNode selectedNode = (InkmlTreeNode) tree
						.getLastSelectedPathComponent();
				// ����ڵ�Ϊ�գ�ֱ�ӷ���
				if (selectedNode == null)
					return;
				// ����һ���½ڵ�
				InkmlTreeNode newNode = new InkmlTreeNode("�½ڵ�");
				// ֱ��ͨ��model������½ڵ㣬������ͨ������JTree��updateUI����
				// model.insertNodeInto(newNode, selectedNode,
				// selectedNode.getChildCount());
				// ֱ��ͨ���ڵ�����½ڵ㣬����Ҫ����tree��updateUI����
				selectedNode.add(newNode);
				// --------�������ʵ����ʾ�½ڵ㣨�Զ�չ�����ڵ㣩-------
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
					// ɾ��ָ���ڵ�
					model.removeNodeFromParent(selectedNode);
				}
			}
		});
		panel.add(deleteButton);

		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				TreePath selectedPath = tree.getSelectionPath();
				if (selectedPath != null) {
					// �༭ѡ�нڵ�
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
