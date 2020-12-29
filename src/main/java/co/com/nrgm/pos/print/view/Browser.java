package co.com.nrgm.pos.print.view;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;

public class Browser extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final List<String> FORMATOS_IMAGEN = Arrays.asList(ImageIO.getReaderFormatNames());

	private Observer observer;
	private JTree tree;

	private void createNavigationTree(Observer observer) {

		this.observer = observer;

		setLayout(new BorderLayout());

		tree = new JTree(addInitialNodes());
		tree.setRootVisible(false);
		tree.setCellRenderer(new TreeCell());
		tree.expandRow(0);
		tree.addTreeSelectionListener( new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent tse) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();
				showChildren(node);
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(tree);
		add(BorderLayout.CENTER, scrollPane);

		tree.setSelectionInterval(0, 0);
	}

	private DefaultMutableTreeNode addInitialNodes() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();

		FileSystemView fileSystemView = FileSystemView.getFileSystemView();
		File[] roots = fileSystemView.getRoots();
		for (File fileSystemRoot : roots) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
			root.add(node);

			File[] files = fileSystemView.getFiles(fileSystemRoot, false);
			Arrays.sort(files);
			for (File file : files) {
				if (file.isDirectory() && !file.getName().startsWith(".")) {
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file);
					node.add(childNode);
				}
			}

		}
		return root;
	}

	private boolean isSupportedFile(String fileName) {
		boolean supported = false;
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			supported = FORMATOS_IMAGEN.contains(fileName.substring(i + 1));
		}

		return supported;
	}

	private List<File> filterFiles(File[] list) {
		List<File> files = new ArrayList<File>();
		int lastDiretory = 0;
		Arrays.sort(list);
		for (File file : list) {
			if (file.isDirectory() && !file.getName().startsWith(".")) {
				// Directorios al inicio de la lista
				files.add(lastDiretory++, file);
			} else if (isSupportedFile(file.getName())) {
				files.add(file);
			}
		}

		return files;
	}
	
	protected void showChildren(final DefaultMutableTreeNode node) {
		tree.setEnabled(false);

		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {

			FileSystemView fileSystemView = FileSystemView.getFileSystemView();

			@Override
			protected Void doInBackground() {
				final File file = (File) node.getUserObject();
				if (file.isDirectory() && node.isLeaf()) {
					List<File> files = filterFiles(file.listFiles());
					for (File child : files) {
						publish(child);
					}
				}
				observer.update(null, file);
				return null;
			}

			@Override
			protected void process(List<File> chunks) {
				for (File child : chunks) {
					node.add(new DefaultMutableTreeNode(child));
				}
			}

			@Override
			protected void done() {
				tree.setEnabled(true);
			}

		};

		worker.execute();
	}

	public Browser(Observer observer) {
		super();
		createNavigationTree(observer);
	}

	public Browser() {
		this(null);
	}

	public void setObserver(Observer observer) {
		this.observer = observer;
	}

}
