package velocityTemplate;

import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class velocitytest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Properties p = new Properties();
		p.setProperty("file.resource.loader.path", "C:/Users/zhanjung/Documents/parttimeproject/deal/src/velocityTemplate/");
		Velocity.init(p);

		VelocityContext context = new VelocityContext();

		context.put("name", new String("Velocity"));

		Template template = null;

		try {
			template = Velocity.getTemplate("mytemplate.vm");
		} catch (ResourceNotFoundException rnfe) {
			rnfe.printStackTrace();
			// couldn't find the template
		} catch (ParseErrorException pee) {
			// syntax error: problem parsing the template
		} catch (MethodInvocationException mie) {
			// something invoked in the template
			// threw an exception
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringWriter sw = new StringWriter();

		template.merge(context, sw);
		System.out.println(sw.toString());
	}
}
