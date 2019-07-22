package de.haw_hamburg.gkap.ui;

public class GraphStylesheet {

	public static final String STYLESHEET = 
			"node{\n" + 
			"	shape:circle; \n" + 
			"	fill-mode:plain; \n" + 
			"	size:15px; \n" +
			"	stroke-mode:dots; \n" + 
			"	stroke-width:1px; \n" +
			"	fill-color: grey;\n" + 
			"}\n" + 
			"\n" + 
			"node.green{ \n" + 
			"	fill-color:#CCFFCCFF,#FFFFFFFF,#FFFFFFFF; \n" + 
			"	stroke-color:#669966FF; \n" + 
			"}\n" + 
			"\n" + 
			"node.gray{ \n" + 
			"	fill-color:#CCCCCCFF,#FFFFFFFF,#FFFFFFFF; \n" + 
			"	stroke-color:#999999FF; \n" + 
			"}\n" + 
			"\n" + 
			"node.red{\n" + 
			"	fill-color:#FFB3B3FF,#FFFFFFFF,#FFFFFFFF; \n" + 
			"	stroke-color:#FF6666FF; \n" + 
			"}\n" + 
			"\n" + 
			"edge{\n" + 
			"	size:2px; \n" + 
			"	text-size:10px; \n" +
			"\n" +
			"}\n" + 
			"\n" + 
			"edge.green{\n" + 
			"	fill-color:#99CC99FF,#FFFFFFFF,#FFFFFFFF;\n" + 
			"} \n" + 
			"\n" + 
			"edge.red{\n" + 
			"	fill-color:#FF8080FF,#FFFFFFFF,#FFFFFFFF; \n" + 
			"}"
			+ "sprite{ \n" + 
			"	fill-color:#FFFFFF00,#FFFFFF00,#FFFFFF00; \n" + 
			"}";
}
