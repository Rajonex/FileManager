package app;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class MySwingWorker extends SwingWorker<Object, Long> {

	private File file;
	private File directory;
	private static long bytesTransferredTotal = 0L;
	private static long totalSizeOfWholeFiles = 0L;
	private JProgressBar progressBar;
	private JToolBar toolBar;

	FileInputStream fis;
	FileChannel input;
	FileOutputStream fos;
	FileChannel output;

	private static final long FILE_COPY_BUFFER_SIZE = 1024 * 1024 * 30;

	public MySwingWorker(File file, File directory, JProgressBar progressBar, JToolBar toolBar) {
		super();
		this.file = file;
		this.directory = directory;
		this.progressBar = progressBar;
		this.toolBar = toolBar;
	}

	@Override
	protected Object doInBackground() throws Exception {
//		progressBar.setVisible(true);
		toolBar.setVisible(true);
		try {
			if (file.isFile()) {
				totalSizeOfWholeFiles = FileUtils.sizeOf(file);
				bytesTransferredTotal = 0L;
				copyFileToDirectory(file, directory);
			} else if (file.isDirectory()) {
				totalSizeOfWholeFiles = FileUtils.sizeOfDirectory(file);
				bytesTransferredTotal = 0L;
				copyDirectoryToDirectory(file, directory);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void done() {
		try {
			if (fis != null)
				fis.close();
			if (input != null)
				input.close();
			if (fos != null)
				fos.close();
			if (output != null)
				output.close();
		} catch (IOException er) {
			er.printStackTrace();
		}
		progressBar.setValue(0);
//		progressBar.setVisible(false);
		toolBar.setVisible(false);
		progressBar = null;
	}

	@Override
	protected void process(List<Long> chunks) {
		// if(progressBar.isVisible() == false)
		// {
		// progressBar.setVisible(true);
		// }		if (chunks != null) {
		if (chunks.size() > 0) {
			if (chunks.get(0) != null) {
				if (progressBar != null) {
		progressBar.setValue(chunks.get(0).intValue());
				}
			}
		}
	}

	public void copyFileToDirectory(final File srcFile, final File destDir) throws IOException {
		copyFileToDirectory(srcFile, destDir, true);
	}

	public void copyFileToDirectory(final File srcFile, final File destDir, final boolean preserveFileDate)
			throws IOException {
		if (destDir == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if (destDir.exists() && destDir.isDirectory() == false) {
			throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
		}
		final File destFile = new File(destDir, srcFile.getName());
		copyFile(srcFile, destFile, preserveFileDate);
	}

	public void copyFile(final File srcFile, final File destFile) throws IOException {
		copyFile(srcFile, destFile, true);
	}

	public void copyFile(final File srcFile, final File destFile, final boolean preserveFileDate) throws IOException {
		checkFileRequirements(srcFile, destFile);
		if (srcFile.isDirectory()) {
			throw new IOException("Source '" + srcFile + "' exists but is a directory");
		}
		if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
			throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
		}
		final File parentFile = destFile.getParentFile();
		if (parentFile != null) {
			if (!parentFile.mkdirs() && !parentFile.isDirectory()) {
				throw new IOException("Destination '" + parentFile + "' directory cannot be created");
			}
		}
		if (destFile.exists() && destFile.canWrite() == false) {
			throw new IOException("Destination '" + destFile + "' exists but is read-only");
		}
		doCopyFile(srcFile, destFile, preserveFileDate);
	}

	public long copyFile(final File input, final OutputStream output) throws IOException {
		try (FileInputStream fis = new FileInputStream(input)) {
			return IOUtils.copyLarge(fis, output);
		}
	}

	private void doCopyFile(final File srcFile, final File destFile, final boolean preserveFileDate)
			throws IOException {
		if (destFile.exists() && destFile.isDirectory()) {
			throw new IOException("Destination '" + destFile + "' exists but is a directory");
		}
		if(isCancelled())
			return;

		try {
			fis = new FileInputStream(srcFile);
			input = fis.getChannel();
			fos = new FileOutputStream(destFile);
			output = fos.getChannel();

			final long size = input.size(); // TODO See IO-386
			long pos = 0;
			long count = 0;
			while (pos < size) {
				if(isCancelled())
					return;
				final long remain = size - pos;
				count = remain > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : remain;
				final long bytesCopied = output.transferFrom(input, pos, count);
				if (bytesCopied == 0) { // IO-385 - can happen if file is truncated after caching the
										// size
					break; // ensure we don't loop forever
				}
				/*
				 * TU WAZNE, TO DO PODMIANY ABY ZMIENIAC PROCENTY
				 *
				 */
				pos += bytesCopied;
				bytesTransferredTotal += bytesCopied;
				publish(100 * bytesTransferredTotal / totalSizeOfWholeFiles);
				// System.out.println(100 * bytesTransferredTotal /
				// totalSizeOfWholeFiles + "%");
				/*
				 *
				 *
				 */
			}

		} catch (Exception er) {
			// er.printStackTrace(); // Wyjatek wystepujacy w momencie przerwania dzialania
			// watku
		} finally {
			fis.close();
			input.close();
			fos.close();
			output.close();
		}
		//
		// final long srcLen = srcFile.length(); // TODO See IO-386
		// final long dstLen = destFile.length(); // TODO See IO-386
		// if (srcLen != dstLen) {
		// throw new IOException("Failed to copy full contents from '" + srcFile + "' to
		// '" + destFile
		// + "' Expected length: " + srcLen + " Actual: " + dstLen);
		// }
		if (preserveFileDate) {
			destFile.setLastModified(srcFile.lastModified());
		}
	}

	public void copyDirectoryToDirectory(final File srcDir, final File destDir) throws IOException {
		if (srcDir == null) {
			throw new NullPointerException("Source must not be null");
		}
		if (srcDir.exists() && srcDir.isDirectory() == false) {
			throw new IllegalArgumentException("Source '" + destDir + "' is not a directory");
		}
		if (destDir == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if (destDir.exists() && destDir.isDirectory() == false) {
			throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
		}
		copyDirectory(srcDir, new File(destDir, srcDir.getName()), true);
	}

	public void copyDirectory(final File srcDir, final File destDir) throws IOException {
		copyDirectory(srcDir, destDir, true);
	}

	public void copyDirectory(final File srcDir, final File destDir, final boolean preserveFileDate)
			throws IOException {
		copyDirectory(srcDir, destDir, null, preserveFileDate);
	}

	public void copyDirectory(final File srcDir, final File destDir, final FileFilter filter) throws IOException {
		copyDirectory(srcDir, destDir, filter, true);
	}

	public void copyDirectory(final File srcDir, final File destDir, final FileFilter filter,
			final boolean preserveFileDate) throws IOException {
		checkFileRequirements(srcDir, destDir);
		if (!srcDir.isDirectory()) {
			throw new IOException("Source '" + srcDir + "' exists but is not a directory");
		}
		if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
			throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
		}

		// Cater for destination being directory within the source directory
		// (see IO-141)
		List<String> exclusionList = null;
		if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
			final File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
			if (srcFiles != null && srcFiles.length > 0) {
				exclusionList = new ArrayList<>(srcFiles.length);
				for (final File srcFile : srcFiles) {
					final File copiedFile = new File(destDir, srcFile.getName());
					exclusionList.add(copiedFile.getCanonicalPath());
				}
			}
		}
		doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList);
	}

	private void checkFileRequirements(final File src, final File dest) throws FileNotFoundException {
		if (src == null) {
			throw new NullPointerException("Source must not be null");
		}
		if (dest == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if (!src.exists()) {
			throw new FileNotFoundException("Source '" + src + "' does not exist");
		}
	}

	private void doCopyDirectory(final File srcDir, final File destDir, final FileFilter filter,
			final boolean preserveFileDate, final List<String> exclusionList) throws IOException {
		// recurse
		final File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
		if (srcFiles == null) { // null if abstract pathname does not denote a directory, or if an I/O
								// error occurs
			throw new IOException("Failed to list contents of " + srcDir);
		}
		if (destDir.exists()) {
			if (destDir.isDirectory() == false) {
				throw new IOException("Destination '" + destDir + "' exists but is not a directory");
			}
		} else {
			if (!destDir.mkdirs() && !destDir.isDirectory()) {
				throw new IOException("Destination '" + destDir + "' directory cannot be created");
			}
		}
		if (destDir.canWrite() == false) {
			throw new IOException("Destination '" + destDir + "' cannot be written to");
		}
		for (final File srcFile : srcFiles) {
			final File dstFile = new File(destDir, srcFile.getName());
			if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
				if (srcFile.isDirectory()) {
					doCopyDirectory(srcFile, dstFile, filter, preserveFileDate, exclusionList);
				} else {
					doCopyFile(srcFile, dstFile, preserveFileDate);
				}
			}
		}

		// Do this last, as the above has probably affected directory metadata
		if (preserveFileDate) {
			destDir.setLastModified(srcDir.lastModified());
		}
	}
}
