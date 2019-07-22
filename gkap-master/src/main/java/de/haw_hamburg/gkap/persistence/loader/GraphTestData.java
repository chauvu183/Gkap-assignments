package de.haw_hamburg.gkap.persistence.loader;

public class GraphTestData {

	public static final String GRAPH01 = "# directed;\n" + 
			"a,b;\n" + 
			"a,c;\n" + 
			"a,k;\n" + 
			"b,d;\n" + 
			"b,e;\n" + 
			"b,j;\n" + 
			"b,k;\n" + 
			"b,l;\n" + 
			"c,a;\n" + 
			"c,d;\n" + 
			"c,l;\n" + 
			"d,a;\n" + 
			"d,e;\n" + 
			"d,k;\n" + 
			"e,b;\n" + 
			"e,c;\n" + 
			"e,e;\n" + 
			"e,f;\n" + 
			"f,c;\n" + 
			"f,g;\n" + 
			"g,g;\n" + 
			"g,e;\n" + 
			"g,b;\n" + 
			"g,d;\n" + 
			"h,b;\n" + 
			"h,c;\n" + 
			"i,a;\n" + 
			"i,c;\n" + 
			"i,i;\n" + 
			"j,k;\n" + 
			"j,c;\n" + 
			"j,a;\n" + 
			"j,b;\n" + 
			"k,c;\n" + 
			"k,g;\n" + 
			"k,d;\n" + 
			"l,a;\n" + 
			"l,f;\n" + 
			"l,h;";
	
	public static final String GRAPH02 = "a,b;\n" + 
			"a,c;\n" + 
			"a,g;\n" + 
			"a,e;\n" + 
			"b,b;\n" + 
			"b,h;\n" + 
			"b,i;\n" + 
			"c,a;\n" + 
			"c,d;\n" + 
			"d,f;\n" + 
			"d,e;\n" + 
			"d,g;\n" + 
			"e,j;\n" + 
			"e,c;\n" + 
			"e,e;\n" + 
			"e,f;\n" + 
			"f,a;\n" + 
			"f,g;\n" + 
			"f,h;\n" + 
			"f,i;\n" + 
			"g,g;\n" + 
			"g,e;\n" + 
			"g,b;\n" + 
			"g,d;\n" + 
			"h,b;\n" + 
			"h,c;\n" + 
			"h,f;\n" + 
			"i,a;\n" + 
			"i,c;\n" + 
			"i,i;\n" + 
			"i,g;\n" + 
			"j,k;\n" + 
			"j,c;\n" + 
			"j,a;\n" + 
			"j,b;\n" + 
			"k,c;\n" + 
			"k,g;\n" + 
			"k,d;";
	public static final String GRAPH05 = "v1,v2 :: 5;\n" + 
			"v1,v3 :: 7;\n" + 
			"v1,v4 :: 5;\n" + 
			"v1,v5 :: 3;\n" + 
			"v1,v6 :: 2;\n" + 
			"v1,v7 ::             6;\n" + 
			"v2,v3 :: 4;\n" + 
			"v2,v4 :: 1;\n" + 
			"v2,v5 :: 8;\n" + 
			"v2,v6 :: 3;\n" + 
			"v2,v7 :: 5;\n" + 
			"v3,v4 :: 3;\n" + 
			"v3,v5 :: 4;\n" + 
			"v3,v6 :: 7;\n" + 
			"v3,v7 :: 1;\n" + 
			"v4,v5 :: 7;\n" + 
			"v4,v6 :: ;\n" + 
			"v4,v7 :: 4;\n" + 
			"v5,v6 :: 5;\n" + 
			"v5,v7 :: 0;\n" + 
			"v6,v7 :: 8;";
	public static final String GRAPH09 = "\n" + 
			"a,b;\n" + 
			"a,d;\n" + 
			"a,i;\n" + 
			"a,g b,j;\n" + 
			"b,i;\n" + 
			"c,l;\n" + 
			"c,g;\n" + 
			"d,l;\n" + 
			"d,e;\n" + 
			"d,g;\n" + 
			"e,j;\n" + 
			"e,c;\n" + 
			"e,e;\n" + 
			"\n" + 
			"e,f;\n" + 
			"f,l;\n" + 
			"f,g;\n" + 
			"f,a;\n" + 
			"f,i;\n" + 
			"g,g;\n" + 
			"g,a;\n" + 
			"g,b;\n" + 
			"g,d;\n" + 
			"h,b;\n" + 
			"h,c;\n" + 
			"h,f;\n" + 
			"i,l;\n" + 
			"i,c;\n" + 
			"j,k;\n" + 
			"j,c;\n" + 
			"j,l;\n" + 
			"j,b;\n" + 
			"k,c;\n" + 
			"k,g;\n" + 
			"k,d;\n" + 
			"l,b;\n" + 
			"l,d;\n" + 
			"l,e;";

	public static final String GRAPH_ALL =
			"v1:2,v2::5;\n" +
			"v9,v3:3::7;\n" +
			"v11:2.2,v4:4.4::5;\n" +
			"v8,v5(ert)::3;\n" +
			"v7,v6;\n" +
			"v99;\n";
}
