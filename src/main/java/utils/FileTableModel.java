package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.io.FileUtils;

public class FileTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private File[] files;
	private File parentFile;
	private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	private String[] columns;
	private JTextField textField;
	private Locale locale;

	public FileTableModel() {
		locale = new Locale("PL");
		this.files = new File[0];
		columns = new String[] { "Icon", "File", "Size", "Created", "Last modified", };
	}

	public FileTableModel(String[] columns) {
		locale = new Locale("PL");
		this.files = new File[0];
		this.columns = columns;
	}

	public FileTableModel(JTextField textField) {
		locale = new Locale("PL");
		this.files = new File[0];
		columns = new String[] { "Icon", "File", "Size", "Created", "Last modified", };
		this.textField = textField;
	}

	public FileTableModel(String[] columns, JTextField textField) {
		locale = new Locale("PL");
		this.files = new File[0];
		this.columns = columns;
		this.textField = textField;

	}

	public Object getValueAt(int row, int column) {
		File file = files[row];

		switch (column) {
		case 0:
			return fileSystemView.getSystemIcon(file);
		case 1:
			return fileSystemView.getSystemDisplayName(file);
		case 2:
			if (file.isFile()) {
				long bytes = file.length();
				if (bytes / 1000000000 >= 10) {
					return ((bytes / 1000000000) + " GB");
				} else if (bytes / 1000000 >= 10) {
					return ((bytes / 1000000) + " MB");
				} else if (bytes / 1000 >= 10) {
					return ((bytes / 1000) + " kB");
				}
				return (bytes + " B");
//				return FileUtils.byteCountToDisplaySize(file.length());
			}
			return "Folder";
		case 3:
			BasicFileAttributes attribute = null;
			try {
				attribute = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
			} catch (IOException er) {
				er.printStackTrace();
			}
			Date date = new Date();
			date.setTime(attribute.creationTime().toMillis());
			DateFormat sdf = DateFormat.getDateInstance(DateFormat.LONG, locale);
			String result = sdf.format(date);
			return result;
		case 4:
			Date date2 = new Date();
			date2.setTime(file.lastModified());
			DateFormat sdf2 = DateFormat.getDateInstance(DateFormat.LONG, locale);
			String result2 = sdf2.format(date2);
			return result2;

		default:
			System.err.println("Logic Error");
		}

		return "";
	}

	public void changeLocale(Locale locale) {
		this.locale = locale;
		fireTableDataChanged();

	}

	public int getRowOfFile(File file) {
		int result = -1;
		if (file != null) {
			for (int i = 0; i < files.length; i++) {
				if (file.equals(files[i])) {
					result = i;
				}
			}
		}
		return result;
	}

	public int getColumnCount() {
		return columns.length;
	}

	public Class<?> getColumnClass(int column) {
		switch (column) {
		case 0:
			return ImageIcon.class;
		// case 3:
		// case 4:
		// return Date.class;
		}
		return String.class;
	}

	public String getColumnName(int column) {
		return columns[column];
	}

	public int getRowCount() {
		return files.length;
	}

	public File getFile(int row) {
		if (files != null) {
			if (files.length > row) {
				return files[row];
			} else {
				return null;
			}
		}
		return null;
	}

	public void setParentFile(File parentFile) {
		if (parentFile != null) {
			this.parentFile = parentFile;
			this.files = parentFile.listFiles();
			if (files != null) {
				textField.setText(parentFile.getAbsolutePath());
				// if (files.length > 0)
				// {
				// if (files[0] != null)
				// {
				// parentFile = files[0].getParentFile();
				// if (parentFile != null && label != null)
				// {
				//
				// }
				// }
				// }
			} else {
				files = new File[0];
			}
		}
		fireTableDataChanged();
	}

	public void removeFile(File file) {
		if (file != null) {
			List<File> listFiles = new ArrayList<>();
			for (File f : files) {
				if (!file.equals(f)) {
					listFiles.add(f);
				}
			}
			files = listFiles.toArray(new File[0]);
			fireTableDataChanged();
		}
	}

	public File getParentFile() {
		// if (files.length <= 0)
		return parentFile;
		// return parentFile.getParentFile();
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
		fireTableStructureChanged();
	}
}