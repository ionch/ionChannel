package social.ionch.core;

public class PluginException extends Exception {
	private static final long serialVersionUID = -8588626457315510047L;
	
	public PluginException(String detail) { super(detail); }
	public PluginException(String detail, Throwable source) { super(detail, source); }
	
	public static class Load extends PluginException {
		private static final long serialVersionUID = 2156604419084987292L;
		
		public Load(String detail) { super(detail); }
		public Load(String detail, Throwable source) { super(detail, source); }
	}
}
