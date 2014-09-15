package penInteraction;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


public class AutoEditTree implements penInteraction.MyObserver {
	JFrame jf;

	JTree tree;
	// 上面JTree对象对应的model
	DefaultTreeModel model;
	MySubject s;
	//
	String FileName = ""; //
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

	// 定义需要被拖动的TreePath
	TreePath movePath;

	// JButton addSiblingButton = new JButton("添加兄弟节点");
	// JButton addChildButton = new JButton("添加子节点");
	// JButton deleteButton = new JButton("删除节点");
	JButton saveButton = new JButton("保存修改");
	JButton editButton = new JButton("编辑当前节点");
	HashMap<String, String[]> arrow_attacher = new HashMap<String, String[]>();
	ArrayList<InkmlNode> nodes = new ArrayList<InkmlNode>();
	ArrayList<InkmlArrow> arrows = new ArrayList<InkmlArrow>();

	public void init() {

		InkmlHandler ih = new InkmlHandler();

		if (canvas.filename != null) {
			ih.readInkml(canvas.filename);
			ArrayList<InkmlText> texts = new ArrayList<InkmlText>();
			ArrayList<TraceGroup> tgList = ih.getTraceGroups();
			if (ih.getNodes().size() > 0) {
				System.out.println("原文件有树状结构");
				nodes = ih.getNodes();
				if (ih.getArrows().size() > 0) {
					arrows = ih.getArrows();

				}
			} else {
				for (int i = 0; i < tgList.size(); i++) {
					if (!tgList.get(i).getAnnotation().equals("text")
							&& !tgList.get(i).getAnnotation().equals("arrow")) {
						nodes.add(new InkmlNode(String.valueOf(tgList.get(i)
								.getTraceGroupIndex()), tgList.get(i)
								.getAnnotation(), null));

					}
					if (tgList.get(i).getAnnotation().equals("arrow")) {
						arrows.add(new InkmlArrow(String.valueOf(tgList.get(i)
								.getTraceGroupIndex())));
					}

					if (tgList.get(i).getAnnotation().equals("text")) {
						InkmlText it = new InkmlText(String.valueOf(tgList.get(
								i).getTraceGroupIndex()));

						texts.add(it);

					}
				}

				FindTextLocation(nodes, texts, arrows);
				ih.createHrefForTraceGroup(nodes, arrows, texts, canvas.filename);
			}

			// 绘制到面板

			for (int i = 0; i < nodes.size(); i++) {
				InkmlNode tn = nodes.get(i);
				InkmlTreeNode tempNode1 = new InkmlTreeNode("node: "
						+ tn.getType() + " ( seg =" + tn.getType() + ")");

				tempNode1.setType(tn.getType());

				InkmlTreeNode tempNode2 = new InkmlTreeNode();

				// tree.getModel().
				// tree.getModel().valueForPathChanged(
				// tree.getSelectionPath(),
				// "From -> Node: " + newValue + " ( seg ="
				// + newValue + ")");

				tempNode2.setId(tn.getText().getTextId());

				tempNode2.setType("text");
				if (nodes.get(i).getText().getContent() != null) {
					tempNode2.setUserObject("text: "
							+ nodes.get(i).getText().getContent() + " ( seg ="
							+ tempNode2.getType() + ")");
				} else {
					if (canvas.getId_content().get(tn.getText().getTextId()) != null) {
						tempNode2.setUserObject("text: "
								+ canvas.getId_content().get(
										tn.getText().getTextId()) + " ( seg ="
								+ tempNode2.getType() + ")");
					} else {
						tempNode2.setUserObject("请输入:");
					}
				}
			
				tempNode1.add(tempNode2);
				tempNode1.setId(tn.getNodeId());
				root.add(tempNode1);
			}

			for (int i = 0; i < arrows.size(); i++) {
				InkmlArrow arrow = arrows.get(i);
				InkmlTreeNode tempNode1 = new InkmlTreeNode(
						"arrow ( seg =arrow)");
				String[] result = new String[2];
				if (arrows.get(i).getSource() != null
						&& arrows.get(i).getTarget() != null) {
					result[0] = arrows.get(i).getSource();
					result[1] = arrows.get(i).getTarget();
				
				} else {
					result = getSourceAndTarget(
							FindBeginAndLast(arrow.getArrowId(), nodes), nodes);
					arrow_attacher.put(arrow.getArrowId(), result);
				}

				
				InkmlTreeNode tempNode2 = new InkmlTreeNode();
				InkmlTreeNode tempNode3 = new InkmlTreeNode();
				String[] st = canvas.getArrow_SaT().get(arrow.getArrowId());

				if (st != null && !st[0].equals("-1")) {
					tempNode2.setUserObject(("From -> Node: "
							+ Id2type(st[0], nodes) + " ( seg ="
							+ Id2type(st[0], nodes) + ")"));
					tempNode2.setType(Id2type(st[0], nodes));
					tempNode2.setId(st[0]);

				} else {
					tempNode2.setUserObject(("From -> Node: "
							+ Id2type(result[0], nodes) + " ( seg ="
							+ Id2type(result[0], nodes) + ")"));
					tempNode2.setType(Id2type(result[0], nodes));
					tempNode2.setId(result[0]);
				}

				if (st != null && !st[1].equals("-1")) {
					tempNode3.setUserObject(("To -> Node: "
							+ Id2type(st[1], nodes) + " ( seg ="
							+ Id2type(st[1], nodes) + ")"));
					tempNode3.setId(st[1]);
					tempNode3.setType(Id2type(st[1], nodes));
				} else {
					tempNode3.setUserObject(("To -> Node: "
							+ Id2type(result[1], nodes) + " ( seg ="
							+ Id2type(result[1], nodes) + ")"));
					tempNode3.setId(result[1]);
					tempNode3.setType(Id2type(result[1], nodes));
				}

				tempNode1.add(tempNode2);
				tempNode1.add(tempNode3);

				arrow.setSource(tempNode2.getId());
				arrow.setTarget(tempNode3.getId());
				if (arrow.getText() != null) {
					System.out.println("arrow.getText:"+arrow.getText() );
					InkmlTreeNode tempNode4 = new InkmlTreeNode();
					tempNode4.setId(arrow.getText().getTextId());
					tempNode4.setType("text");

					if (arrows.get(i).getText().getContent() != null) {
						tempNode4.setUserObject("text: "
								+ arrows.get(i).getText().getContent()
								+ " ( seg =" + tempNode4.getType() + ")");
					} else {
						if (canvas.getId_content().get(
								arrow.getText().getTextId()) != null) {
							tempNode4.setUserObject("text: "
									+ canvas.getId_content().get(
											arrow.getText().getTextId())
									+ " ( seg =" + tempNode4.getType() + ")");
						} else {
							tempNode4.setUserObject("请输入:");
						}
					}
					tempNode1.add(tempNode4);

				}

				tempNode1.setId(arrow.getArrowId());
				tempNode1.setType("arrow");
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
				ArrayList<TraceGroup> tgList = null;
				if (canvas.filename != null) {
					ih.readInkml(canvas.filename);
					tgList = ih.getTraceGroups();
					TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
					if (tp != null) {
						movePath = tp;
						Object[] path = tp.getPath();

						InkmlTreeNode temp_ = (InkmlTreeNode) path[path.length - 1];

						String id = temp_.getId();
						if (id != null) {
							int index = -1;
							for (int i = 0; i < tgList.size(); i++) {
								if (tgList.get(i).getTraceGroupIndex() == Integer
										.parseInt(id)) {

									index = i;
									break;

								}
							}

							System.out.println(index);
							canvas.drawRect(index);
						}
					}

				}

			}

			// 鼠标松开时获得需要拖到哪个父节点
			public void mouseReleased(MouseEvent e) {

			}
		};
		tree.addMouseListener(ml);

		JPanel panel = new JPanel();

		// addSiblingButton.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent event) {
		// // 获取选中节点
		// InkmlTreeNode selectedNode = (InkmlTreeNode) tree
		// .getLastSelectedPathComponent();
		// // 如果节点为空，直接返回
		// if (selectedNode == null)
		// return;
		// // 获取该选中节点的父节点
		// InkmlTreeNode parent = (InkmlTreeNode) selectedNode.getParent();
		// // 如果父节点为空，直接返回
		// if (parent == null)
		// return;
		// // 创建一个新节点
		// InkmlTreeNode newNode = new InkmlTreeNode("新节点");
		// // 获取选中节点的选中索引
		// int selectedIndex = parent.getIndex(selectedNode);
		// // 在选中位置插入新节点
		// model.insertNodeInto(newNode, parent, selectedIndex + 1);
		// // --------下面代码实现显示新节点（自动展开父节点）-------
		// // 获取从根节点到新节点的所有节点
		// TreeNode[] nodes = model.getPathToRoot(newNode);
		// // 使用指定的节点数组来创建TreePath
		// TreePath path = new TreePath(nodes);
		// // 显示指定TreePath
		// tree.scrollPathToVisible(path);
		// }
		// });
		// panel.add(addSiblingButton);

		// addChildButton.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent event) {
		// // 获取选中节点
		// InkmlTreeNode selectedNode = (InkmlTreeNode) tree
		// .getLastSelectedPathComponent();
		// // 如果节点为空，直接返回
		// if (selectedNode == null)
		// return;
		// // 创建一个新节点
		// InkmlTreeNode newNode = new InkmlTreeNode("新节点");
		// // 直接通过model来添加新节点，则无需通过调用JTree的updateUI方法
		// // model.insertNodeInto(newNode, selectedNode,
		// // selectedNode.getChildCount());
		// // 直接通过节点添加新节点，则需要调用tree的updateUI方法
		// selectedNode.add(newNode);
		// // --------下面代码实现显示新节点（自动展开父节点）-------
		// TreeNode[] nodes = model.getPathToRoot(newNode);
		// TreePath path = new TreePath(nodes);
		// tree.scrollPathToVisible(path);
		// tree.updateUI();
		// }
		// });
		// panel.add(addChildButton);

		// deleteButton.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent event) {
		// InkmlTreeNode selectedNode = (InkmlTreeNode) tree
		// .getLastSelectedPathComponent();
		// if (selectedNode != null && selectedNode.getParent() != null) {
		// // 删除指定节点
		// model.removeNodeFromParent(selectedNode);
		// }
		// }
		// });
		// panel.add(deleteButton);
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				InkmlHandler ih = new InkmlHandler();
				ih.writeJtree(nodes, arrows, canvas.filename);

			}

		});
		panel.add(saveButton);

		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				TreePath selectedPath = tree.getSelectionPath();
				if (selectedPath != null) {
					// 编辑选中节点
					tree.getCellEditor().addCellEditorListener(
							new CellEditorListener() {

								@Override
								public void editingCanceled(ChangeEvent arg0) {
									// TODO Auto-generated method stub

								}

								@SuppressWarnings("unchecked")
								@Override
								public void editingStopped(ChangeEvent arg0) {
									// TODO Auto-generated method stub
									System.out.println("zz");
									String newValue = tree.getCellEditor()
											.getCellEditorValue().toString();
									TreePath tp = tree.getSelectionPath();
									if (tp != null) {
										movePath = tp;
									}
									Object[] path = tp.getPath();
									InkmlTreeNode temp_ = (InkmlTreeNode) path[path.length - 1];

									String id = temp_.getId();

									String type = temp_.getType();
									InkmlTreeNode parent = (InkmlTreeNode) path[path.length - 2];
									String parentId = parent.getId();
									if (!type.equals("text")) {
										boolean tag = false;
										for (InkmlNode node : nodes) {
											if (newValue.equals(node.getType())) {
												tag = true;
												break;
											}
										}

										if (tag == false) {
											JOptionPane.showMessageDialog(null,
													"请输入正确类型", "请输入正确类型",
													JOptionPane.ERROR_MESSAGE);
										} else {
											String[] attachers = arrow_attacher
													.get(parentId);
											String[] st = { "-1", "-1" };
											String index = "";
											if (attachers[0].equals(id)) {// 它是source
																			// comp
												Point tempP = FindBeginAndLast(
														parentId, nodes)[0];
												index = getSourceOrTarget(
														tempP,
														nodes,
														tree.getCellEditor()
																.getCellEditorValue()
																.toString());
												tree.getModel()
														.valueForPathChanged(
																tree.getSelectionPath(),
																"From -> Node: "
																		+ newValue
																		+ " ( seg ="
																		+ newValue
																		+ ")");
												st[0] = index;
												for (InkmlArrow arrow : arrows) {
													if (arrow.getArrowId()
															.equals(parentId)) {
														arrow.setSource(index);
													}
												}

											} else {

												Point tempP = FindBeginAndLast(
														parentId, nodes)[1];
												index = getSourceOrTarget(
														tempP,
														nodes,
														tree.getCellEditor()
																.getCellEditorValue()
																.toString());
												tree.getModel()
														.valueForPathChanged(
																tree.getSelectionPath(),
																"To -> Node: "
																		+ newValue
																		+ " ( seg ="
																		+ newValue
																		+ ")");
												st[1] = index;
												for (InkmlArrow arrow : arrows) {
													if (arrow.getArrowId()
															.equals(parentId)) {
														arrow.setTarget(index);
													}
												}

											}

											canvas.getArrow_SaT().put(parentId,
													st);
											temp_.setId(index);
											temp_.setType(Id2type(index, nodes));

										}

									} else {

										canvas.getId_content().put(
												id,
												tree.getCellEditor()
														.getCellEditorValue()
														.toString());
										tree.getModel().valueForPathChanged(
												tree.getSelectionPath(),
												"text: " + newValue
														+ " ( seg =text)");
										for (InkmlNode node : nodes) {
											if (node.getNodeId().equals(
													parentId)) {
												node.getText().setContent(
														newValue);
											}
										}

										for (InkmlArrow arrow : arrows) {
											if (arrow.getArrowId().equals(
													parentId)) {
												arrow.getText().setContent(
														newValue);
											}
										}
									}
									InkmlHandler ih = new InkmlHandler();

									System.out.println(tree.getCellEditor()
											.getCellEditorValue());

									tree.updateUI();

								}

							});
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

	private void FindTextLocation(ArrayList<InkmlNode> nodes,
			ArrayList<InkmlText> texts, ArrayList<InkmlArrow> arrows) {
		// TODO Auto-generated method stub
		InkmlHandler ih = new InkmlHandler();
		if (canvas.filename != "") {
			ih.readInkml(canvas.filename);
			ArrayList<TraceGroup> tgList = ih.getTraceGroups();
			ArrayList<Trace> traceList = ih.getTraces();

			ArrayList<InkmlText> arrowTextList = new ArrayList<InkmlText>();
			ArrayList<InkmlText> textList = new ArrayList<InkmlText>();
			for (int i = 0; i < texts.size(); i++) {
				double[] vertexs = get4V(texts.get(i).getTextId(), tgList,
						traceList);
				for (int j = 0; j < nodes.size(); j++) {
					double[] temp = get4V(nodes.get(j).getNodeId(), tgList,
							traceList);
					if ((temp[0] > vertexs[0]) && (temp[1] < vertexs[1])
							&& (temp[2] > vertexs[2]) && (temp[3] < vertexs[3])) {
						nodes.get(j).setText(texts.get(i));
						textList.add(texts.get(i));

					}
				}
			}

			arrowTextList = (ArrayList<InkmlText>) texts.clone();
			arrowTextList.removeAll(textList);

			for (int i = 0; i < arrowTextList.size(); i++) {
				ArrayList<Point> pointList = new ArrayList<Point>();
				pointList = TraceGroup2Points(arrowTextList.get(i).getTextId(),
						tgList, traceList);

				double Mindistance = 10000;
				int index = -1;
				for (int k = 0; k < arrows.size(); k++) {
					ArrayList<Point> ArrowPointList = new ArrayList<Point>();
					ArrowPointList = TraceGroup2Points(arrows.get(k)
							.getArrowId(), tgList, traceList);
					if (getMinDistance(pointList, ArrowPointList) < Mindistance) {
						Mindistance = getMinDistance(pointList, ArrowPointList);
						index = k;
					}
				}

				arrows.get(index).setText(arrowTextList.get(i));
			}
		}

	}

	private double getMinDistance(ArrayList<Point> pointList,
			ArrayList<Point> arrowPointList) {
		// TODO Auto-generated method stub
		double Min = 10000;
		for (int i = 0; i < pointList.size(); i++) {
			for (int j = 0; j < arrowPointList.size(); j++) {
				double temp = Math.pow(
						pointList.get(i).x - arrowPointList.get(j).x, 2)
						+ Math.pow(
								pointList.get(i).y - arrowPointList.get(j).y, 2);
				if (temp < Min) {
					Min = temp;

				}
			}
		}

		return Min;
	}

	private ArrayList<Point> TraceGroup2Points(String id,
			ArrayList<TraceGroup> tgList, ArrayList<Trace> traceList) {
		// TODO Auto-generated method stub
		ArrayList<Point> pointList = new ArrayList<Point>();
		TraceGroup textTg = GetTraceGroupById(id, tgList);
		if (textTg != null) {
			ArrayList<Integer> traceDataRefs = textTg.getTraceDataRefs();
			for (int j = 0; j < traceDataRefs.size(); j++) {
				pointList.addAll(getTraceByRef(traceDataRefs.get(j), traceList)
						.getPoints());
			}
		}

		return pointList;
	}

	private TraceGroup GetTraceGroupById(String index,
			ArrayList<TraceGroup> tgList) {
		// TODO Auto-generated method stub
		for (int i = 0; i < tgList.size(); i++) {
			if (tgList.get(i).getTraceGroupIndex() == Integer.parseInt(index)) {
				return tgList.get(i);
			}
		}

		return null;
	}

	private double[] get4V(String TraceGroupId, ArrayList<TraceGroup> tgList,
			ArrayList<Trace> traceList) {
		// TODO Auto-generated method stub
		double[] vertexs = new double[4];
		double MaxX = -1;
		double MinX = 10000;
		double MaxY = -1;
		double MinY = 10000;
		for (int k = 0; k < tgList.size(); k++) {

			if (tgList.get(k).getTraceGroupIndex() == Integer
					.parseInt(TraceGroupId)) {

				ArrayList<Integer> refs = tgList.get(k).traceDataRefs;

				for (int i = 0; i < refs.size(); i++) {

					Trace temptrace = traceList.get(refs.get(i));
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
			}
		}

		vertexs[0] = MaxX;
		vertexs[1] = MinX;
		vertexs[2] = MaxY;
		vertexs[3] = MinY;

		return vertexs;
	}

	private String FindCorrectComp(String arrowId, ArrayList<InkmlNode> nodes,
			String type) {

		return "";
	}

	private Point[] FindBeginAndLast(String arrowId, ArrayList<InkmlNode> nodes) {
		System.out.println(arrowId);
		// TODO Auto-generated method stub
		InkmlHandler ih = new InkmlHandler();
		Point[] result = new Point[2];
		if (canvas.filename != "") {
			ih.readInkml(canvas.filename);
			int id = Integer.parseInt(arrowId);
			ArrayList<TraceGroup> tgList = ih.getTraceGroups();
			ArrayList<Trace> traceList = ih.getTraces();
			int index = -1;
			for (int i = 0; i < tgList.size(); i++) {
				if (tgList.get(i).getTraceGroupIndex() == id) {
					index = i;
					break;
				}
			}

			System.out.println("index:" + index);
			TraceGroup tg = tgList.get(index);
			ArrayList<Integer> traceDataRefs = tg.getTraceDataRefs();
			double MaxDistance = -1;
			int RefIndex = -1;
			for (int i = 0; i < traceList.size(); i++) {
				for (int j = 0; j < traceDataRefs.size(); j++) {
					if (traceList.get(i).getTraceIndex() == traceDataRefs
							.get(j)) {
						Trace temp = traceList.get(i);
						Point start = temp.getPoints().get(0);
						Point end = temp.getPoints().get(
								temp.getPoints().size() - 1);
						double distance = Math.pow(start.x - end.x, 2)
								+ Math.pow(start.y - end.y, 2);
						if (distance > MaxDistance) {
							MaxDistance = distance;
							RefIndex = traceDataRefs.get(j);

						}
					}
				}
			}

			System.out.println("RefIndex:" + RefIndex);
			Point begin = new Point();
			Point last = new Point();
			for (int i = 0; i < traceDataRefs.size(); i++) {
				if (traceDataRefs.get(i) != RefIndex) {
					Trace t = getTraceByRef(traceDataRefs.get(i), traceList);
					Trace ref = getTraceByRef(RefIndex, traceList);
					if (t != null) {
						Point p = t.getPoints().get(0);
						Point a = ref.getPoints().get(0);
						Point b = ref.getPoints().get(
								ref.getPoints().size() - 1);
						double distance1 = Math.pow(a.x - p.x, 2)
								+ Math.pow(a.y - p.y, 2);
						double distance2 = Math.pow(b.x - p.x, 2)
								+ Math.pow(b.y - p.y, 2);
						if (distance1 > distance2) {
							begin = a;
							last = b;
						} else {
							begin = b;
							last = a;
						}
					}
					break;
				}
			}

			result[0] = begin;
			result[1] = last;

		}

		return result;
	}

	private String[] getSourceAndTarget(Point[] p, ArrayList<InkmlNode> nodes) {
		Point begin = p[0];
		Point last = p[1];
		// TODO Auto-generated method stub
		String[] result = new String[2];
		InkmlHandler ih = new InkmlHandler();
		ih.readInkml(canvas.filename);

		ArrayList<TraceGroup> tgList = ih.getTraceGroups();
		ArrayList<Trace> traceList = ih.getTraces();
		double BeginMinDistance = 10000;
		double LastMinDistance = 10000;
		String BeginNodeIndex = "";
		String LastNodeIndex = "";
		for (int i = 0; i < nodes.size(); i++) {
			ArrayList<Point> pointList = TraceGroup2Points(nodes.get(i)
					.getNodeId(), tgList, traceList);
			for (int j = 0; j < pointList.size(); j++) {
				double distance1 = Math.pow(pointList.get(j).x - begin.x, 2)
						+ Math.pow(pointList.get(j).y - begin.y, 2);
				double distance2 = Math.pow(pointList.get(j).x - last.x, 2)
						+ Math.pow(pointList.get(j).y - last.y, 2);
				if (distance1 < BeginMinDistance) {
					BeginMinDistance = distance1;
					BeginNodeIndex = nodes.get(i).getNodeId();
				}
				if (distance2 < LastMinDistance) {
					LastMinDistance = distance2;
					LastNodeIndex = nodes.get(i).getNodeId();
				}
			}
		}

		result[0] = BeginNodeIndex;
		result[1] = LastNodeIndex;
		return result;
	}

	private String getSourceOrTarget(Point p, ArrayList<InkmlNode> nodes,
			String type) {

		// TODO Auto-generated method stub
		String result = new String();
		InkmlHandler ih = new InkmlHandler();
		ih.readInkml(canvas.filename);

		ArrayList<TraceGroup> tgList = ih.getTraceGroups();
		ArrayList<Trace> traceList = ih.getTraces();
		double BeginMinDistance = 10000;

		String BeginNodeIndex = "";

		for (int i = 0; i < nodes.size(); i++) {
			ArrayList<Point> pointList = TraceGroup2Points(nodes.get(i)
					.getNodeId(), tgList, traceList);
			for (int j = 0; j < pointList.size(); j++) {
				double distance = Math.pow(pointList.get(j).x - p.x, 2)
						+ Math.pow(pointList.get(j).y - p.y, 2);

				if (distance < BeginMinDistance
						&& nodes.get(i).getType().equals(type)) {
					BeginMinDistance = distance;
					BeginNodeIndex = nodes.get(i).getNodeId();
				}

			}
		}

		result = BeginNodeIndex;

		return result;
	}

	private TraceGroup GetTraceGroupByTraceId(int Index,
			ArrayList<TraceGroup> tgList) {
		// TODO Auto-generated method stub
		for (int i = 0; i < tgList.size(); i++) {
			if (tgList.get(i).getTraceDataRefs().contains(Index)) {
				return tgList.get(i);
			}
		}

		return null;
	}

	private Trace getTraceByRef(Integer integer, ArrayList<Trace> traceList) {
		// TODO Auto-generated method stub
		int ref = integer.intValue();
		for (int i = 0; i < traceList.size(); i++) {
			if (traceList.get(i).getTraceIndex() == ref) {
				return traceList.get(i);
			}
		}

		return null;

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
