package client;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

// TODO: Auto-generated Javadoc
/**
 * The Class HtmlFormatter.
 *
 * Class creates logg in html format
 */
public class HtmlFormatter extends Formatter {
	
	/**
	 * Instantiates a new html formatter.
	 */
	public HtmlFormatter()
	{
		
	}
	
	/**
	 * Header of html
	 *
	 * @param h the h
	 * @return the head
	 */
	@Override
	public String getHead(Handler h)
	{

		return "<html><head><title>AppLog</title>" +
				"<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">" +
				"</head><body>" +
				"<table border=1>" +
				"<tr bgcolor=CYAN><td>date</td><td>level</td><td>class</td><td>method</td>" +
				"<td>message</td><td>thrown message</td><td>stacktrace</td></tr>";
	}
	
	
	/**
	 *
	 * @param h the h
	 * @return the tail
	 */
	@Override
	public String getTail(Handler h)
	{
		
		return "</table></body></html>";
	}
	
	/**
	 * 
	 *
	 * @param record the record
	 * @return the string
	 */
	@Override
	public String format(LogRecord record)
	{
		StringBuilder result=new StringBuilder();
		Date d = new Date();
		Level level = record.getLevel();

		if (level==Level.SEVERE)
		{
			result.append("<tr bgColor=Tomato><td>");
		}
		else if (level==Level.WARNING)
		{
			result.append("<tr bgColor=GRAY><td>");
		}
		else
		{
			result.append("<tr bgColor=WHITE><td>");
		}
		
		result.append("\n");
		
		
		result.append(d);
		result.append("</td><td>");
		result.append(record.getLevel().toString());
		result.append("</td><td>");
		result.append(record.getSourceClassName());
		result.append("</td><td>");
		result.append(record.getSourceMethodName());
		result.append("</td><td>");
		result.append(record.getMessage());
		result.append("</td><td>");
		
		
		
		Throwable thrown = record.getThrown();
		
		
		
		if (thrown!=null)
		{
			result.append(record.getThrown().getMessage());
			result.append("</td><td>");
		
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			record.getThrown().printStackTrace(pw);
			String stackTrace=sw.toString();
		
			result.append(stackTrace);
			result.append("</td>");
		}
		else
		{
			result.append("</td><td>null");
			result.append("</td>");
		}
		result.append("</tr>\n");
		return result.toString();
	}
}