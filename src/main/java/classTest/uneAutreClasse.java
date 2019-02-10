package classTest;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class uneAutreClasse {

	private int p;
	private int p2;
	private String p3;
	
	public uneAutreClasse(int m1, int m2, String m3) {
		this.p = m1;
		this.p2 = m2;
		this.p3 = m3;
	}

	public int getp() {
		return p;
	}

	public void setp(int p) {
		this.p = p;
	}

	public int getp2() {
		return p2;
	}

	public void setp2(int p2) {
		this.p2 = p2;
	}

	public String getp3() {
		return p3;
	}

	public void setp3(String p3) {
		this.p3 = p3;
	}
	
	public boolean jpp() {
		return true;
	}
}

class RasLeBolTest{
	
	@Test
	public void test1() {
		uneAutreClasse uAC = new uneAutreClasse(0, 1, "plz");
		assertTrue(uAC.jpp());
	}
}

