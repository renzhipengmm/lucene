package org;

import org.junit.Test;

public class TestP {
	@Test
	public void tp() {
		int i = 0;
		int x = (i++) + (++i);
		System.out.println(x);
	}
}
