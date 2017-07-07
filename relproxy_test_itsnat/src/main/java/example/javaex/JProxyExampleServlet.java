
package example.javaex;

import com.sillelien.dollar.relproxy.jproxy.JProxy;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.itsnat.core.event.ItsNatServletRequestListener;
import org.itsnat.core.http.HttpServletWrapper;
import org.itsnat.core.tmpl.ItsNatDocumentTemplate;


/**
 * 
 * @author jmarranz
 */
public class JProxyExampleServlet extends HttpServletWrapper
{  
    public JProxyExampleServlet()
    {
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException 
    {
        super.init(config);

        ServletContext context = config.getServletContext();
        
        String pathPrefix = context.getRealPath("/") + "/WEB-INF/javaex/pages/";

        final FalseDB db = new FalseDB();
        
        ItsNatDocumentTemplate docTemplate;
        docTemplate = itsNatServlet.registerItsNatDocumentTemplate("javaex","text/html", pathPrefix + "javaex.html");                
        ItsNatServletRequestListener listener = JProxy.create(new example.javaex.JProxyExampleLoadListener(db), ItsNatServletRequestListener.class);
        docTemplate.addItsNatServletRequestListener(listener);       
        
        ItsNatServletRequestListener original = new example.javaex.JProxyExampleLoadListener(db);
        ItsNatServletRequestListener proxy = JProxy.create(original, ItsNatServletRequestListener.class);        
        ItsNatServletRequestListener proxy2 = JProxy.create(original, ItsNatServletRequestListener.class);         
        System.out.println("EQUALS TEST (true if not reloaded): " + (proxy.equals(proxy2)));
    }    
 
}

