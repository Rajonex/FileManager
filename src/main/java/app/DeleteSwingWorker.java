package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;

public class DeleteSwingWorker extends SwingWorker<Object, Long> {
	private File file;
	private JProgressBar progressBar;
	private JToolBar toolBar;
	private long deletedBytes = 0L;
	private long allBytes = 0L;

	public DeleteSwingWorker(File file, JProgressBar progressBar, JToolBar toolBar) {
		super();
		this.file = file;
		this.progressBar = progressBar;
		this.toolBar = toolBar;
	}

	@Override
	protected Object doInBackground() throws Exception {
		// if(file.exists() && file != null)
		// {
		// allBytes = countBytes(file);
		// if(allBytes == 0)
		// file.delete();
		// deleteWithCounting(file);
		// }
		if (file != null) {
			if (file.exists()) {
				// progressBar.setVisible(true);
				toolBar.setVisible(true);
				deletedBytes = 0L;
				allBytes = FileUtils.sizeOf(file);
				if (allBytes > 0) {
					if (file.isDirectory())
						deleteDirectory(file);
					if (file.isFile())
						file.delete();
				} else {
					file.delete();
				}
			}
		}

		return null;
	}

	@Override
	protected void done() {
		progressBar.setValue(0);
		toolBar.setVisible(false);
		// progressBar.setVisible(false);
		progressBar = null;
	}

	@Override
	protected void process(List<Long> chunks) {
		// if (toolBar.isVisible() == false)
		// {
		// toolBar.setVisible(true);
		// }
		if (chunks != null) {
			if (chunks.size() > 0) {
				if (chunks.get(0) != null) {
					if (progressBar != null) {
						progressBar.setValue(chunks.get(0).intValue());
					}
				}
			}

		}
	}

	private void forceDelete(final File file) throws IOException {
		if (file.isDirectory()) {
			deleteDirectory(file);
		} else {
			final boolean filePresent = file.exists();
			/**
			 * DODANE PRZEZE MNIE
			 */
			long bytes = file.length();
			boolean isDeleted = file.delete();

			/**
			 *
			 */
			if (isDeleted) {
				deletedBytes += bytes;
				publish(100 * deletedBytes / allBytes);
			} else {
				if (!filePresent) {
					throw new FileNotFoundException("File does not exist: " + file);
				}
				final String message = "Unable to delete file: " + file;
				throw new IOException(message);
			}
		}
	}

	private void deleteDirectory(final File directory) throws IOException {
		if (!directory.exists()) {
			return;
		}

		if (!isSymlink(directory)) {
			cleanDirectory(directory);
		}

		if (!directory.delete()) {
			final String message = "Unable to delete directory " + directory + ".";
			throw new IOException(message);
		}
	}

	private void cleanDirectory(final File directory) throws IOException {
		final File[] files = verifiedListFiles(directory);

		IOException exception = null;
		for (final File file : files) {
			if(isCancelled())
				return;
			try {
				forceDelete(file);
			} catch (final IOException ioe) {
				exception = ioe;
			}
		}

		if (null != exception) {
			throw exception;
		}
	}

	private File[] verifiedListFiles(final File directory) throws IOException {
		if (!directory.exists()) {
			final String message = directory + " does not exist";
			throw new IllegalArgumentException(message);
		}

		if (!directory.isDirectory()) {
			final String message = directory + " is not a directory";
			throw new IllegalArgumentException(message);
		}

		final File[] files = directory.listFiles();
		if (files == null) { // null if security restricted
			throw new IOException("Failed to list contents of " + directory);
		}
		return files;
	}

	private boolean isSymlink(final File file) throws IOException {
		if (file == null) {
			throw new NullPointerException("File must not be null");
		}
		return Files.isSymbolicLink(file.toPath());
	}

	// private boolean deleteWithCounting(File file)
	// {
	//
	// if (file.isFile())
	// {
	// long size = file.length();
	// boolean isDeleted = file.delete();
	// if (isDeleted)
	// {
	// deletedBytes += size;
	// publish(100 * deletedBytes / allBytes);
	// return true;
	// }
	// }
	// if (file.isDirectory())
	// {
	// File[] files = file.listFiles();
	// if (files != null)
	// {
	// for (File f : files)
	// {
	// deleteWithCounting(f);
	// }
	// }
	// return file.delete();
	// }
	//
	// return false;
	// }
	//
	// private long countBytes(File file)
	// {
	//
	// if (file.isFile())
	// return file.length();
	// if (file.isDirectory())
	// {
	// File[] files = file.listFiles();
	// long bytesNumber = 0L;
	// for (File f : files)
	// {
	// bytesNumber += countBytes(f);
	// }
	// return bytesNumber;
	// }
	//
	// return 0L;
	// }
}
