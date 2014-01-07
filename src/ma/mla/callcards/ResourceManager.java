package ma.mla.callcards;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.PlatformUI;

public class ResourceManager {

	private static final ImageRegistry imageRegistry = new ImageRegistry();
	private static final ImageRegistry descRegistry = new ImageRegistry();
	private static Color messageForeground;
	public final static Color ERROR_COLOR = new Color(PlatformUI.getWorkbench()
			.getDisplay(), 255, 174, 174);
	public final static Color WARNING_COLOR = new Color(PlatformUI
			.getWorkbench().getDisplay(), 255, 196, 119);

	public static ImageDescriptor getDescriptor(String key) {
		return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				"./icons/" + key);
	}

	public static Image getImage(String key) {
		Image img = imageRegistry.get(key);
		if (img == null || img.isDisposed()) {
			imageRegistry.remove(key);
			ImageDescriptor desc = new CachedImageDescriptor(key,
					Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							"./icons/" + key));
			img = desc.createImage(PlatformUI.getWorkbench().getDisplay());
			if (descRegistry.getDescriptor(key) == null) {
				descRegistry.put(key, desc);
			}
		}
		return img;
	}

	public static URL getImageUrl(String key) {
		URL url = FileLocator.find(Activator.getDefault().getBundle(),
				new Path("./icons/" + key), null);
		try {
			return FileLocator.toFileURL(url);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static class CachedImageDescriptor extends ImageDescriptor {

		private ImageDescriptor delegate;
		private Image image;
		private String key;

		private CachedImageDescriptor(String key, ImageDescriptor delegate) {
			this.key = key;
			this.delegate = delegate != null ? delegate
					: getMissingImageDescriptor();
		}

		@Override
		public ImageData getImageData() {
			return delegate.getImageData();
		}

		@Override
		public Image createImage() {
			return createImage(true);
		}

		@Override
		public Image createImage(boolean returnMissingImageOnError) {
			return createImage(returnMissingImageOnError, PlatformUI
					.getWorkbench().getDisplay());
		}

		@Override
		public Image createImage(boolean returnMissingImageOnError,
				Device device) {
			if (image == null || image.isDisposed()) {
				image = imageRegistry.get(key);
				if (image == null || image.isDisposed()) {
					imageRegistry.remove(key);
					image = super
							.createImage(returnMissingImageOnError, device);
					imageRegistry.put(key, image);
				}
			}
			return image;
		}

		@Override
		public void destroyResource(Object previouslyCreatedObject) {
			imageRegistry.remove(key);
			image.dispose();
		}

	}

	public synchronized static Color getMessageForeground() {
		if (messageForeground == null) {
			messageForeground = new Color(PlatformUI.getWorkbench()
					.getDisplay(), 87, 87, 87);
		}
		return messageForeground;
	}

}
