import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RpcBootstrap {
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("spring.xml");
		while (true) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
