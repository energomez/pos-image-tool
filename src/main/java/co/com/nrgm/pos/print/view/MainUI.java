package co.com.nrgm.pos.print.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import co.com.nrgm.pos.print.format.PrintableFactory;
import co.com.nrgm.pos.print.format.PrintableImage;
import co.com.nrgm.pos.print.format.PrintableType;
import co.com.nrgm.pos.print.util.ImageTool;


public class MainUI extends JFrame implements Observer {

	private static final long serialVersionUID = 1;

	private static final Dimension MIN_SIZE = new Dimension(800, 600);

	private static final int COLOR_GRAY_SYLVER = 192; // Silver

	private final int fileNameLimit = 8;

	private BufferedImage biMaster;
	private BufferedImage biWhiteBlack;

	private Browser panelBrowser;
	private JPanel panelMaster;
	private JPanel panelWhiteBlack;
	private JLabel lblTitulo;
	private JTextField txtFileName;
	private JPanel panelCheck;
	private List<JCheckBox> typeChecks = new ArrayList<>();
	private JSlider slider;
	private JButton btnRestart;
	private JButton btnGenerate;

	private String imageName;

	private void paintImage(Graphics g, Component component, BufferedImage image) {
		if (image != null) {
			int x = (component.getWidth() - image.getWidth()) / 2;
			int y = (component.getHeight() - image.getHeight()) / 2;

			g.drawImage(image, x, y, component);
		}
	}

	private void createFiles(String path) {
		for (JCheckBox jCheckBox : typeChecks) {
			if (jCheckBox.isSelected()) {
				PrintableType type = PrintableType.valueOf(jCheckBox.getText());
				PrintableImage printable = PrintableFactory.getPrintable(biWhiteBlack, type);
				printable.writeToFile(path, txtFileName.getText());
			}
		}
	}

	private void generateBinaryFiles() {
		if (txtFileName.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Ingrese el nombre del archivo");
			txtFileName.requestFocusInWindow();
			return;
		}
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setFileView(new FileView() {

			@Override
			public Icon getIcon(File f) {
				FileSystemView fileSystemView = FileSystemView.getFileSystemView();
				return fileSystemView.getSystemIcon(f);
			}
			
		});
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File saveFolder = chooser.getSelectedFile();
			createFiles(saveFolder.getAbsolutePath());
			JOptionPane.showMessageDialog(null, "Proceso finalizado");
		}
	}

	private void createFrame() {
		setTitle("Generador de imagenes para impresión");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(MIN_SIZE);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		// Panel con explorador de archivos
		panelBrowser = new Browser(this);
		panelBrowser.setBounds(10, 11, 610, 258);
		getContentPane().add(panelBrowser);

		// Visualización de la imagen seleccionada en el explorador
		panelMaster = new JPanel() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				paintImage(g, this, biMaster);
			}

		};
		FlowLayout flowLayout = (FlowLayout) panelMaster.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panelMaster.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelMaster.setBounds(10, 280, 610, 135);
		getContentPane().add(panelMaster);

		// Visualización en blanco y negro de la imagen seleccionada
		panelWhiteBlack = new JPanel() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				paintImage(g, this, biWhiteBlack);
			}

		};
		panelWhiteBlack.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelWhiteBlack.setBounds(10, 426, 610, 135);
		getContentPane().add(panelWhiteBlack);

		// Panel lateral con lista de los tipos exportables de la imagen
		JLabel lblNewLabel = new JLabel("Tecnolog\u00EDas");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(640, 92, 144, 24);
		getContentPane().add(lblNewLabel);

		panelCheck = new JPanel();
		panelCheck.setBounds(640, 117, 144, 116);
		getContentPane().add(panelCheck);
		panelCheck.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		for (PrintableType type : PrintableType.values()) {
			JCheckBox jCheckBox = new JCheckBox(type.name());
			jCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
			panelCheck.add(jCheckBox);
			typeChecks.add(jCheckBox);
		}

		JLabel lblFileName = new JLabel("Nombre");
		lblFileName.setToolTipText(new StringBuilder("Nombre del archivo destino. M\u00E1ximo ").append(fileNameLimit).append(" caracteres").toString());
		lblFileName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblFileName.setBounds(640, 36, 122, 24);
		getContentPane().add(lblFileName);

		// Deslizador para seleccionar el tono límite del gris para convertir a blanco y negro
		slider = new JSlider(SwingConstants.VERTICAL, 1, 254, COLOR_GRAY_SYLVER);
		slider.setBounds(630, 426, 22, 135);
		slider.setMinimum(1);
		slider.setMaximum(255);
		slider.setEnabled(false);
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (biMaster != null) {
					JSlider slider = (JSlider) e.getSource();
					generatePreview(slider.getValue(), false);
				}
			}
		});
		getContentPane().add(slider);

		// Reiniciar el valor del tono límite de gris a su valor por defecto
		btnRestart = new JButton("Reiniciar");
		btnRestart.setBounds(662, 484, 122, 23);
		btnRestart.setEnabled(false);
		btnRestart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				slider.setValue(COLOR_GRAY_SYLVER);
			}
		});
		getContentPane().add(btnRestart);

		// Ejecutar la exportación de la imagen seleccionada
		btnGenerate = new JButton("Generar");
		btnGenerate.setBounds(650, 244, 122, 23);
		btnGenerate.setEnabled(false);
		btnGenerate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateBinaryFiles();
			}
		});
		getContentPane().add(btnGenerate);

		// Captura del prefijo con que se nombraran los archivos generados
		txtFileName = new JTextField();
		txtFileName.setBounds(640, 61, 110, 20);
		txtFileName.setColumns(10);
		txtFileName.setDocument(new PlainDocument() {

			private static final long serialVersionUID = 1L;

			@Override
			public void insertString(int offset, String text, AttributeSet attr)
					throws BadLocationException {
				if (text == null) return;
				if ((getLength() + text.length()) <= fileNameLimit) {
					String newText = text.replaceAll("[^0-9a-zA-Z_]+", "");
					super.insertString(offset, newText.toUpperCase(), attr);
				}
			}

			@Override
			public void replace(int offset, int length, String text, AttributeSet attrs)
					throws BadLocationException {
				String newText = text.replaceAll("[^0-9a-zA-Z_]+", "");
				super.replace(offset, length, newText.toUpperCase(), attrs);
			}

		});
		txtFileName.setEnabled(false);
		getContentPane().add(txtFileName);
		
		lblTitulo = new JLabel("Configurar destino");
		lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblTitulo.setBounds(640, 11, 144, 24);
		getContentPane().add(lblTitulo);
		
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e) {
				panelBrowser.requestFocus();
			}
			
		});
	}

	private void generatePreview(int grayLimit, boolean newPreview) {
		if (newPreview || biWhiteBlack == null) {
			biWhiteBlack = new BufferedImage(biMaster.getWidth(), biMaster.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		}
		// Construir la imagen en blanco y negro usando el limite de gris
		for (int rgbColor, y = biMaster.getHeight() - 1; y >= 0; y--) {
			for (int x = biMaster.getWidth() - 1; x >= 0; x--) {
				rgbColor = biMaster.getRGB(x, y);
				biWhiteBlack.setRGB(x, y, ImageTool.rgb2WhiteBlack(rgbColor, grayLimit));
			}
		}
		panelWhiteBlack.repaint();

	}

	private void processImage(File file) {
		StringBuilder msg = new StringBuilder();

		cleanImages();
		if (file.isDirectory() || !file.exists()) {
			return;
		}
		if (!file.canRead()) {
			msg.append("El archivo ").append(file).append(" no se puede leer");
			JOptionPane.showMessageDialog(null, msg.toString());
			msg.setLength(0);
			return;
		}

		BufferedImage biFile = null;
		try {
			biFile = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		if (biFile == null) {
			msg.append("El archivo ").append(file).append(" tiene errores o no es una imagen");
			JOptionPane.showMessageDialog(null, msg.toString());
			return;
		}

		biMaster = ImageTool.resizeImage(biFile, ImageTool.getPrintDimentions(biFile, PrintableImage.MAX_WIDTH));
		panelMaster.repaint();
		biFile = null;

		generatePreview(slider.getValue(), true);
		imageName = file.getName();
		if (imageName.lastIndexOf('.') > 0) {
			imageName = imageName.substring(0, imageName.lastIndexOf('.'));
		}
		imageName.replaceAll("[^0-9a-zA-Z_]+", "_");
		txtFileName.setText(imageName.substring(0, Math.min(fileNameLimit, imageName.length())));
		slider.setEnabled(true);
		btnRestart.setEnabled(true);
		btnGenerate.setEnabled(true);
		txtFileName.setEnabled(true);
	}

	/**
	 * Create the frame.
	 */
	public MainUI() {
		super();
		createFrame();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof File) {
			processImage((File) arg);
			panelBrowser.requestFocus();
		}
	}

	private void cleanImages() {
		biMaster = null;
		panelMaster.repaint();
		biWhiteBlack = null;
		panelWhiteBlack.repaint();
		slider.setValue(COLOR_GRAY_SYLVER);
		slider.setEnabled(false);
		btnRestart.setEnabled(false);
		btnGenerate.setEnabled(false);
		txtFileName.setText("");
		txtFileName.setEnabled(false);
		imageName = null;
	}
}
