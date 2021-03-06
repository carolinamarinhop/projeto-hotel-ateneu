package br.com.ateneu.hotel.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import br.com.ateneu.hotel.util.HibernateUtil;

/**
 * Esta classe serve para implementar a a t�cnica do Open Session in View, que consiste em
 * que a sess�o do Hibernate sera aberta no inicio do processamento do servidor e finalizada
 * no fim do processamento. 
 * @author Felipe
 *
 */

@WebFilter(urlPatterns = {"*.jsf"})
public class ConexaoHibernateFilter implements Filter{
private SessionFactory sf;
	
	//Metodo executado quando a aplica��o � ativada
		@Override
		public void init(FilterConfig config) throws ServletException {
			this.sf = HibernateUtil.getSessionFactory();
			
		}
	//Executado quando o aplicativo web � desativado ou o servidor cai
	
	
	//Metodo que ir� interceptar a requisi��o web
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws ServletException {
		Session currentSession = this.sf.getCurrentSession();
		
		Transaction transaction = null;
		
		try {
			//A linha abaixo inicia uma transa��o com o banco de dados
			transaction = currentSession.beginTransaction();
			//A linha abaixo serve para passar o processamento adiante
			chain.doFilter(servletRequest, servletResponse);
			//A linha abaixo conclui a transa��o com o banco de dados
			transaction.commit();
			
			//A condi��o abaixo fecha a conex�o com o banco caso o mesmo ainda esteja aberto
			if(currentSession.isOpen()) {
				currentSession.close();
			}
			
			//Abaixo seguem as tratativas de erro
		}catch (Throwable ex) {
			try {
				if(transaction.isActive()) {
					transaction.rollback();
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
			throw new ServletException(ex);
		}
	}
	
	
	@Override
	public void destroy() {
	
		
	}
}
